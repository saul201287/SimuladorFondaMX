package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.fxplay.utils.GameConstants;
import com.almasb.fxgl.dsl.FXGL;

public class Mesa {
    private final int id;
    private Entity mesaEntity;
    private Comensal comensalActual;
    private boolean ocupado = false;

    public Mesa(int id) {
        this.id = id;
        this.comensalActual = null;
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
        System.out.println("Comensal " + comensal.getIdComensal() + " ocupa la mesa " + id);
        return true;
    }

    public synchronized void liberarMesa(Recepcionista recepcionista) {
        if (!ocupado) {
            return; 
        }
        System.out.println("Mesa " + id + " liberada.");
        ocupado = false;
        comensalActual = null;
        recepcionista.notificarComensalEnEspera();
    }

    public int getIdMesa() {
        return id;
    }

    public Entity getMesaEntity() {
        return mesaEntity;
    }

    public Entity crearMesa(double x, double y) {
        mesaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("mesa.png"))
                .scale(GameConstants.MESA_SCALE, GameConstants.MESA_SCALE)
                .buildAndAttach();
        return mesaEntity;
    }

    public double getX() {
        return mesaEntity != null ? mesaEntity.getX() : 0;
    }

    public double getY() {
        return mesaEntity != null ? mesaEntity.getY() : 0;
    }
}
