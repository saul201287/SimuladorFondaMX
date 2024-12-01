package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.fxplay.factories.GameFactory;
import com.almasb.fxgl.dsl.FXGL;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import com.almasb.fxgl.animation.Animation;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import com.fxplay.utils.GameConstants;

public class Mesero {

    private static Mesero instancia;
    private static Entity meseroEntity;
    private static boolean entregando = false;
    private static Point2D posicionInicial;
    private static final double TIEMPO_MOVIMIENTO = 3.0; // 3 segundos para moverse
    private static final double TIEMPO_ESPERA = 4.0; // 4 segundos tomando la orden
    private static Queue<Orden> ordenesPendientes = new LinkedList<>();

    public Mesero() {
    }

    public static Mesero getInstance() {
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
    
    public synchronized void agregarOrden(Orden nuevaOrden) {
        ordenesPendientes.add(nuevaOrden);
        System.out.println("Nueva orden agregada para la mesa " + nuevaOrden.getMesa().getIdMesa());
        if (!entregando) {
            iniciarEntregaDeOrden();
        }
    }

    public synchronized void tomarOrden(Mesa mesa) {
        if (mesa == null || entregando)
            return;
        Orden nuevaOrden = new Orden(mesa, ordenesPendientes.size() + 1);
        ordenesPendientes.add(nuevaOrden);
        nuevaOrden.crearOrden(mesa.getX(), mesa.getY());

        System.out.println(
                "Orden " + nuevaOrden.getNumeroOrden() + " para la mesa " + mesa.getIdMesa() + " ha sido tomada.");

        if (!entregando) {
            System.out.println("iniciando orden");
            iniciarEntregaDeOrden();
        }
    }

    private synchronized void iniciarEntregaDeOrden() {
        System.out.println(ordenesPendientes.size());
        if (ordenesPendientes.isEmpty() || entregando) {
            System.out.println("No hay órdenes pendientes o el mesero ya está entregando.");
            return;
        }

        entregando = true;
        Orden ordenActual = ordenesPendientes.poll();
        Mesa mesaActual = ordenActual.getMesa();
        Point2D destino = new Point2D(mesaActual.getX(), mesaActual.getY());

        animationBuilder()
                .duration(Duration.seconds(TIEMPO_MOVIMIENTO))
                .translate(meseroEntity)
                .from(meseroEntity.getPosition())
                .to(destino)
                .buildAndPlay();

        FXGL.runOnce(() -> {
            System.out.println("Mesero tomando orden en la mesa " + mesaActual.getIdMesa());
            entregando = false;
            FXGL.runOnce(this::iniciarEntregaDeOrden, Duration.seconds(1));
        }, Duration.seconds(TIEMPO_ESPERA));
        atenderOrden(ordenActual);
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

            entregando = false;
            double tiempoComida = 10 + Math.random() * 5;
            FXGL.runOnce(() -> iniciarEntregaDeOrden(), javafx.util.Duration.seconds(tiempoComida));
        });

        entrega.start();
    }

    public void volverCocina() {
        FXGL.runOnce(() -> {
            animationBuilder()
                    .duration(Duration.seconds(TIEMPO_MOVIMIENTO))
                    .translate(meseroEntity)
                    .from(meseroEntity.getPosition())
                    .to(posicionInicial)
                    .buildAndPlay();
        }, javafx.util.Duration.seconds(1));
    }
}
