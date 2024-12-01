package com.fxplay.models;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.animation.Animation;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class Orden {
    public enum Estado {
        PENDIENTE,
        EN_PROCESO,
        ENTREGADA
    }

    private Estado estado;
    private Mesa mesa;
    private Entity ordenEntity;
    private int numeroOrden;

    public Orden(Mesa mesa, int numeroOrden) {
        this.mesa = mesa;
        this.numeroOrden = numeroOrden;
        this.estado = Estado.PENDIENTE;
    }


    public Entity crearOrden(double x, double y) {
        FXGL.runOnce(() -> {
            Platform.runLater(() -> {
                ordenEntity = FXGL.entityBuilder()
                        .at(x, y)
                        .viewWithBBox(FXGL.texture("orden.png"))
                        .scale(0.1, 0.1)
                        .buildAndAttach();
                estado = Estado.PENDIENTE;
            });
        }, Duration.seconds(2.5));

        return ordenEntity;
    }


    public void entregarOrdenPorMesero(Point2D posicionMesa) {
        if (estado != Estado.PENDIENTE) {
            return; 
        }

        estado = Estado.EN_PROCESO;

        Animation<?> animacionEntrega = FXGL.animationBuilder()
                .duration(javafx.util.Duration.seconds(1))
                .translate(ordenEntity)
                .from(ordenEntity.getPosition())
                .to(posicionMesa)
                .build();

        animacionEntrega.start();

        animacionEntrega.setOnFinished(() -> {
            estado = Estado.ENTREGADA;
            System.out.println("La orden " + numeroOrden + " ha sido entregada a la mesa " + mesa.getIdMesa());
        });
    }

    public void cambiarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public Estado getEstado() {
        return estado;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public Point2D getPosicion() {
        return ordenEntity != null ? ordenEntity.getPosition() : new Point2D(0, 0);
    }
}
