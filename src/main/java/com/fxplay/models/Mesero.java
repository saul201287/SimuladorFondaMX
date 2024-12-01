package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import com.almasb.fxgl.animation.Animation;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mesero implements Runnable {

    private static Mesero instancia;
    private static Entity meseroEntity;
    private static final AtomicBoolean entregando = new AtomicBoolean(false);
    private static Point2D posicionInicial;
    private static final double TIEMPO_MOVIMIENTO = 3.0;
    private static final double TIEMPO_ESPERA = 4.0;
    private static final Queue<Orden> ordenesPendientes = new ConcurrentLinkedQueue<>();

    private final ExecutorService executorService;

    public Mesero() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized Mesero getInstance() {
        if (instancia == null) {
            instancia = new Mesero();
        }
        return instancia;
    }

    public Entity crearMesero(double x, double y) {
        posicionInicial = new Point2D(x, y);

        meseroEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("mesero.png"))
                .scale(0.15, 0.15)
                .buildAndAttach();

        return meseroEntity;
    }

    public void agregarOrden(Orden nuevaOrden) {
        MonitorComida monitorComida = MonitorComida.getInstance();
        monitorComida.insertarOrden(nuevaOrden);
        ordenesPendientes.add(nuevaOrden);
        System.out.println("Nueva orden agregada para la mesa " + nuevaOrden.getMesa().getIdMesa());

        if (!entregando.get()) {
            executorService.submit(this);
        }
    }

    public void tomarOrden(Mesa mesa) {
        if (mesa == null || entregando.get()) {
            return;
        }

        Orden ordenPendiente = ordenesPendientes.stream()
                .filter(orden -> orden.getMesa().equals(mesa) && orden.getEstado() == Orden.Estado.PENDIENTE)
                .findFirst()
                .orElse(null);

        if (ordenPendiente != null) {
            ordenPendiente.cambiarEstado(Orden.Estado.EN_PROCESO);

            System.out.println("Orden " + ordenPendiente.getNumeroOrden() +
                    " para la mesa " + mesa.getIdMesa() + " ha sido tomada.");

            // Reinsertar la orden con estado EN_PROCESO en el MonitorComida
            MonitorComida.getInstance().insertarOrden(ordenPendiente);
        }
    }

    @Override
    public void run() {
        try {
            while (!ordenesPendientes.isEmpty()) {
                // Process the next order without blocking other orders
                procesarSiguienteOrden();

                // Small wait to prevent intensive loops
                Thread.sleep(4000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Return to the kitchen when no more orders
            volverCocina();
        }
    }

    private void procesarSiguienteOrden() {
        if (ordenesPendientes.isEmpty()) {
            return;
        }

        Orden ordenActual = ordenesPendientes.poll();
        Mesa mesaActual = ordenActual.getMesa();
        Point2D destino = new Point2D(mesaActual.getX() - 30, mesaActual.getY() + 30);

        animationBuilder()
                .duration(Duration.seconds(TIEMPO_MOVIMIENTO))
                .translate(meseroEntity)
                .from(meseroEntity.getPosition())
                .to(destino)
                .buildAndPlay();

        ordenActual.crearOrden(mesaActual.getX() - 30, mesaActual.getY());

        FXGL.runOnce(() -> {
            System.out.println("Mesero tomando orden en la mesa " + mesaActual.getIdMesa());
            atenderOrden(ordenActual);
        }, Duration.seconds(TIEMPO_ESPERA - 1));
    }

    private void atenderOrden(Orden ordenActual) {
        int tiempoPreparacion = ThreadLocalRandom.current().nextInt(5, 11);

        System.out.println(
                "Atendiendo la orden " + ordenActual.getNumeroOrden() + " por " + tiempoPreparacion + " segundos.");

        FXGL.runOnce(() -> {
            entregarOrden(ordenActual);
        }, javafx.util.Duration.seconds(tiempoPreparacion));
    }

    private void entregarOrden(Orden ordenActual) {
        Mesa mesaActual = ordenActual.getMesa();
        Recepcionista recepcionista = new Recepcionista(null);

        Animation<?> entrega = FXGL.animationBuilder()
                .duration(javafx.util.Duration.seconds(1))
                .translate(meseroEntity)
                .from(meseroEntity.getPosition())
                .to(posicionInicial)
                .build();

        entrega.setOnFinished(() -> {
            System.out.println("Orden " + ordenActual.getNumeroOrden() + " entregada al comensal en la mesa "
                    + mesaActual.getIdMesa());
            recepcionista.liberarMesa(mesaActual);

            if (!ordenesPendientes.isEmpty()) {
                executorService.submit(this);
            }
        });

        entrega.start();
    }

    public void volverCocina() {
        System.out.println("Órdenes pendientes: " + ordenesPendientes.size());
        FXGL.runOnce(() -> {
            animationBuilder()
                    .duration(Duration.seconds(TIEMPO_MOVIMIENTO))
                    .translate(meseroEntity)
                    .from(meseroEntity.getPosition())
                    .to(posicionInicial)
                    .buildAndPlay();
        }, javafx.util.Duration.seconds(1));
    }

    public void shutdown() {
        executorService.shutdown();
    }
}