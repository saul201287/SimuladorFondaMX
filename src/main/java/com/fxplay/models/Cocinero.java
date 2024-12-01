package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

public class Cocinero implements Runnable {
    private boolean cocinando = false;
    private Entity cocineroEntity;
    private MonitorComida monitorComida;
    private boolean enEjecucion = true;

    public Cocinero(MonitorComida monitorComida) {
        this.monitorComida = monitorComida;
    }

    public Entity crearCocinero(double x, double y) {
        cocineroEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("cocinero.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return cocineroEntity;
    }

    public void detener() {
        enEjecucion = false;
    }

    @Override
    public void run() {
        while (enEjecucion) {
            try {
                // Tomar una orden del monitor
                Orden orden = monitorComida.retirarOrden();
                if (orden != null) {
                    System.out.println("Cocinero preparando la orden: " + orden.getNumeroOrden());
                    cocinar();
                    añadirPlatoAMonitor();
                }
                Thread.sleep(1000); // Esperar antes de verificar otra orden
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void cocinar() {
        if (cocineroEntity == null)
            return;

        animationBuilder()
                .duration(javafx.util.Duration.seconds(1))
                .translate(cocineroEntity)
                .from(cocineroEntity.getPosition())
                .to(cocineroEntity.getPosition().subtract(0, 200))
                .buildAndPlay();

        FXGL.runOnce(() -> {
            animationBuilder()
                    .duration(javafx.util.Duration.seconds(1))
                    .translate(cocineroEntity)
                    .from(cocineroEntity.getPosition())
                    .to(cocineroEntity.getPosition().add(0, 200))
                    .buildAndPlay();
        }, javafx.util.Duration.seconds(1));
    }

    private void añadirPlatoAMonitor() {
        if (monitorComida == null)
            return;
        Comida comida = new Comida();
        try {
            monitorComida.insertarPlato(comida);
            System.out.println("Cocinero añadió un plato al monitor.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
