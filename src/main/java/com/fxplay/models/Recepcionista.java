package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;

import javafx.application.Platform;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import com.almasb.fxgl.dsl.FXGL;

public class Recepcionista {
    private Entity recepcionistaEntity;
    private List<Mesa> mesas;
    private Queue<Comensal> colaEsperando;

    public Recepcionista(List<Mesa> mesas) {
        this.mesas = mesas;
        this.colaEsperando = new LinkedList<>();
    }

    public Entity crearRecepcionista(double x, double y) {
        Platform.runLater(() -> {
            recepcionistaEntity = FXGL.entityBuilder()
                    .at(x, y)
                    .viewWithBBox(FXGL.texture("recepcionista.png"))
                    .scale(.2, .2)
                    .buildAndAttach();
        });

        return recepcionistaEntity;
    }

    public synchronized void asignarMesa(Comensal comensal) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            for (Mesa mesa : mesas) {
                if (mesa.ocuparMesa(comensal)) {
                    comensal.setMesa(mesa);
                    comensal.moverAMesa(mesa.getX(), mesa.getY());
                    return;
                }
            }

            //System.out.println("Comensal " + Thread.currentThread().getName() + " no encontró mesa. Esperando...");
            colaEsperando.add(comensal);
            wait();
        }
    }

    public synchronized void notificarComensalEnEspera() {
        if (!colaEsperando.isEmpty()) {
            colaEsperando.poll();
            notify();
        }
    }

    public synchronized void liberarMesa(Mesa mesa) {
        mesa.liberarMesa(this);
    }
}
