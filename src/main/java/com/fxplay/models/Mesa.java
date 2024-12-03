package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.fxplay.utils.GameConstants;
import javafx.application.Platform;
import com.almasb.fxgl.dsl.FXGL;

public class Mesa {
    private final int id;
    private Entity mesaEntity;
    private Comensal comensalActual;
    private boolean ocupado = false;
    private Mesero mesero;

    public Mesa(int id, Mesero mesero) {
        this.id = id;
        this.comensalActual = null;
        this.mesero = mesero;
    }

    public synchronized boolean estaDisponible() {
        return !ocupado;
    }

    public synchronized boolean ocuparMesa(Comensal comensal) {
        if (ocupado) {
            return false;
        }
        ocupado = true;
        comensalActual = comensal;
        //System.out.println("Comensal " + comensal.getIdComensal() + " ocupa la mesa " + id);

        Orden nuevaOrden = new Orden(this, id);
        Mesero.getInstance().agregarOrden(nuevaOrden);
        notificarMesaOcupada();
        esperarPorMesero();
        return true;
    }

    public synchronized void esperarPorMesero() {
        while (!ocupado) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void notificarMesaOcupada() {
        mesero.tomarOrden(this);
        notify();
    }

    public synchronized void liberarMesa(Recepcionista recepcionista) {
        if (!ocupado) {
            return;
        }

        FXGL.runOnce(() -> {
            comensalActual.abandonarMesa();

            synchronized (this) {
                System.out.println("Mesa " + id + " liberada.");
                ocupado = false;
                
                notify();
                recepcionista.notificarComensalEnEspera();
            }
        }, javafx.util.Duration.seconds(3));
    }

    public int getIdMesa() {
        return id;
    }

    public Entity getMesaEntity() {
        return mesaEntity;
    }

    public Comensal getComensalActual() {
        return comensalActual;
    }

    public Entity crearMesa(double x, double y) {
        Platform.runLater(() -> {
            mesaEntity = FXGL.entityBuilder()
                    .at(x, y)
                    .viewWithBBox(FXGL.texture("mesa.png"))
                    .scale(GameConstants.MESA_SCALE, GameConstants.MESA_SCALE)
                    .buildAndAttach();
        });

        return mesaEntity;
    }

    public double getX() {
        return mesaEntity != null ? mesaEntity.getX() : 0;
    }

    public double getY() {
        return mesaEntity != null ? mesaEntity.getY() : 0;
    }
}
