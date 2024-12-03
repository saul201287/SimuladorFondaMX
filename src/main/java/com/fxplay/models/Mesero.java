package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import kotlin.collections.builders.ListBuilder;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mesero extends Thread {

    private static Mesero instancia;
    private static Entity meseroEntity;
    private static final AtomicBoolean entregando = new AtomicBoolean(false);
    private static Point2D posicionInicial;
    private static final double TIEMPO_MOVIMIENTO = 3.0;
    private static final AtomicBoolean yaVolviendoACocina = new AtomicBoolean(false);
    private static final Queue<Orden> ordenesPendientes = new ConcurrentLinkedQueue<>();
    private static List<Orden> ordenesListas = new ListBuilder<>();
    private static final MonitorComida monitor = new MonitorComida();
    private static Boolean sirviendo = false;

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
        ordenesPendientes.add(nuevaOrden);
        // System.out.println("Nueva orden agregada para la mesa " +
        // nuevaOrden.getMesa().getIdMesa());

        if (ordenesListas.size() == 0) {
            executorService.submit(this);
        }
    }

    public void tomarOrden(Mesa mesa) {
        System.out.println("tomando el pedido....");
        if (mesa == null || entregando.get()) {
            return;
        }
        System.out.println("tomando el pedido2....");
        if (sirviendo) {
            return;
        }
        System.out.println(ordenesListas.size());
        if (ordenesListas.size() > 0) {
            finalizarTarea();
        }
        System.out.println("tomando el pedido3....");
        Orden ordenPendiente = ordenesPendientes.stream()
                .filter(orden -> orden.getMesa().equals(mesa) && orden.getEstado() == Orden.Estado.PENDIENTE)
                .findFirst()
                .orElse(null);

        if (ordenPendiente != null) {
            ordenPendiente.cambiarEstado(Orden.Estado.EN_PROCESO);
            monitor.insertarOrden(ordenPendiente);
            // System.out.println("Orden " + ordenPendiente.getNumeroOrden() +
            // " para la mesa " + mesa.getIdMesa() + " ha sido tomada.");
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (ordenesPendientes.isEmpty()) {
                    break;
                }

                if (entregando.get()) {
                    Thread.sleep(1000);
                    continue;
                }

                procesarSiguienteOrden();
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            System.out.println("Mesero thread interrupted");
            Thread.currentThread().interrupt();
        } finally {
            if (ordenesListas.size() == 0 )
                finalizarTarea();
        }
    }

    public synchronized void finalizarTarea() {
        try {
            System.out.println(ordenesPendientes.size());

            System.out.println("Mesero finalizando tarea. Órdenes pendientes: " + ordenesPendientes.size());

            volverCocina();

            Thread.sleep(3000);

            if (ordenesListas.size() == 0) {
                ordenesListas = monitor.recogerPlatos();
                System.out.println("Platos recogidos: " + ordenesListas.size());
            }

            yaVolviendoACocina.set(false);
            sirviendo = true;
            atenderOrden(ordenesListas.remove(ordenesListas.size() - ordenesListas.size()));
            System.out.println(ordenesListas.size() + " y " + ordenesPendientes.size());
            if (ordenesPendientes.size() == 10 && ordenesListas.size() == 0) {
                executorService.submit(this);
            }

        } catch (InterruptedException e) {
            System.out.println("Mesero interrumpido durante finalización de tarea");
            Thread.currentThread().interrupt();
        } finally {
            sirviendo = false;
            entregando.set(false);
        }
    }

    private synchronized void procesarSiguienteOrden() {
        System.out.println("procesando orden entrante....");
        if (ordenesPendientes.isEmpty()) {
            return;
        }

        Orden ordenActual = ordenesPendientes.poll();
        if (ordenActual == null) {
            // System.out.println("No hay órdenes para procesar.");
            return;
        }

        Mesa mesaActual = ordenActual.getMesa();
        Point2D destino = new Point2D(mesaActual.getX() - 30, mesaActual.getY() + 30);

        animationBuilder()
                .duration(Duration.seconds(TIEMPO_MOVIMIENTO - 1))
                .translate(meseroEntity)
                .from(meseroEntity.getPosition())
                .to(destino)
                .buildAndPlay();

        ordenActual.crearOrden(mesaActual.getX() - 30, mesaActual.getY());
        /*
         * FXGL.runOnce(() -> {
         * // System.out.println("Mesero tomando orden en la mesa " +
         * // mesaActual.getIdMesa());
         * atenderOrden(ordenActual);
         * }, Duration.seconds(TIEMPO_ESPERA - 2));
         */

    }

    private synchronized void atenderOrden(Orden ordenActual) {
        int tiempoPreparacion = ThreadLocalRandom.current().nextInt(5, 11);

        System.out.println(
                "Atendiendo la orden " + ordenActual.getNumeroOrden() + " por " + tiempoPreparacion + " segundos.");

        FXGL.runOnce(() -> {
            entregarOrden(ordenActual);
        }, javafx.util.Duration.seconds(tiempoPreparacion));
    }

    private synchronized void entregarOrden(Orden ordenActual) {
        if (meseroEntity == null)
            return;

        Mesa mesaActual = ordenActual.getMesa();
        Point2D destino = new Point2D(mesaActual.getX() - 30, mesaActual.getY() + 30);

        if (mesaActual.getComensalActual() != null) {
            animationBuilder()
                    .duration(Duration.seconds(TIEMPO_MOVIMIENTO - 2))
                    .translate(meseroEntity)
                    .from(meseroEntity.getPosition())
                    .to(destino)
                    .buildAndPlay();

            FXGL.runOnce(() -> {
                ordenActual.cambiarEstado(Orden.Estado.ENTREGADA);
                FXGL.getGameWorld().removeEntity(ordenActual.ordenEntity);
                animationBuilder()
                        .duration(Duration.seconds(TIEMPO_MOVIMIENTO - 2))
                        .translate(meseroEntity)
                        .from(destino)
                        .to(posicionInicial)
                        .buildAndPlay();

                Platform.runLater(() -> {
                    entregando.set(false);
                    if (ordenActual.getEstado() == Orden.Estado.ENTREGADA) {
                        Recepcionista recepcionista = new Recepcionista(null);
                        recepcionista.liberarMesa(mesaActual);
                    }

                    
                });
            }, Duration.seconds(TIEMPO_MOVIMIENTO -1));
        } else {
            System.out.println("No se puede entregar la orden. Mesa ya liberada.");
            entregando.set(false);
        }
    }

    private synchronized void volverCocina() {
        if (yaVolviendoACocina.compareAndSet(false, true)) {
            // System.out.println("Órdenes pendientes: " +
            // MonitorComida.getInstance().ordenesPendientes());
            FXGL.runOnce(() -> {
                animationBuilder()
                        .duration(Duration.seconds(TIEMPO_MOVIMIENTO - 1))
                        .translate(meseroEntity)
                        .from(meseroEntity.getPosition())
                        .to(posicionInicial)
                        .buildAndPlay();
            }, javafx.util.Duration.seconds(3));

        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

}