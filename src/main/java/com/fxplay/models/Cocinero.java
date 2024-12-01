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
        monitorComida.marcarMeseroTerminado(); // Notificar que no habrá más órdenes
    }

    @Override
    public void run() {
        while (enEjecucion) {
            try {
                Orden orden = monitorComida.retirarOrdenParaCocinar();
                if (orden == null) {
                    break;
                }
                if (orden.getEstado() == Orden.Estado.EN_PROCESO) {
                    System.out.println("Cocinero preparando la orden: " + orden.getNumeroOrden());
                    cocinar(orden);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Cocinero ha terminado de procesar órdenes.");
    }

    public void cocinar(Orden orden) {
        if (cocineroEntity == null)
            return;

        System.out.println("cocinando");
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

        // Añadir plato al monitor
        añadirPlatoAMonitor(orden);
    }

    private void añadirPlatoAMonitor(Orden orden) {
        if (monitorComida == null)
            return;

        Comida comida = new Comida();
        comida.setOrden(orden); // Asociar la orden con el plato

        try {
            monitorComida.insertarPlato(comida);
            System.out.println("Cocinero añadió un plato para la orden " + orden.getNumeroOrden());
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}