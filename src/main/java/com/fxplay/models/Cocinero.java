package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;

import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import com.almasb.fxgl.dsl.FXGL;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cocinero implements Runnable {
    private Entity cocineroEntity;
    private final MonitorComida monitorComida;
    private final ScheduledExecutorService scheduledExecutorService;

    public Cocinero(MonitorComida monitorComida) {
        this.monitorComida = monitorComida;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public Entity crearCocinero(double x, double y) {
        cocineroEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("cocinero.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return cocineroEntity;
    }

    @Override
    public void run() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                Comida comida = new Comida();
                cocinar();
                monitorComida.insertarPlato(comida);
            } catch (Exception e) {
                System.out.println("Error en la preparación de comida: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public synchronized void cocinar() {
        if (cocineroEntity == null)
            return;

        System.out.println("Cocinando...");

        Point2D currentPosition = cocineroEntity.getPosition();

        var subirAnimacion = animationBuilder()
                .duration(javafx.util.Duration.seconds(1))
                .translate(cocineroEntity)
                .from(currentPosition)
                .to(currentPosition.subtract(0, 150))
                .build();

        subirAnimacion.start();

        FXGL.runOnce(() -> {
            var bajarAnimacion = animationBuilder()
                    .duration(javafx.util.Duration.seconds(1))
                    .translate(cocineroEntity)
                    .from(cocineroEntity.getPosition())
                    .to(currentPosition)
                    .build();

            bajarAnimacion.start();
        }, javafx.util.Duration.seconds(3));
    }

    public synchronized void detener() {
        scheduledExecutorService.shutdown();
    }
}