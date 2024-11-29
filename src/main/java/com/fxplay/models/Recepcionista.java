package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import java.util.List;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;

public class Recepcionista {
    private Entity recepcionistaEntity;
    private List<Mesa> mesas;

    public Recepcionista(List<Mesa> mesas) {
        this.mesas = mesas;
    }

    public Entity crearRecepcionista(double x, double y) {
        recepcionistaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("recepcionista.png"))
                .scale(.2, .2)
                .buildAndAttach();
        return recepcionistaEntity;
    }

    public Entity getRecepcionistaEntity() {
        return recepcionistaEntity;
    }

    public synchronized void asignarMesa(Comensal comensal) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (Mesa mesa : mesas) {
                    if (mesa.ocuparMesa(comensal)) { 
                        comensal.setMesa(mesa); 
                        comensal.moverAMesa(mesa.getX(), mesa.getY()); 
                        return; 
                    }
                }
                Espera espera = new Espera();
                Point2D posicionEspera = new Point2D(850, 320);
                Entity esperaEntity = espera.crearEspera(posicionEspera);
                
                wait(); 
                
                espera.eliminarEspera();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    }

    public synchronized void liberarMesa(Mesa mesa) {
        mesa.liberarMesa();
        System.out.println("Mesa " + mesa.getIdMesa() + " ha sido liberada. Notificando comensales...");
    }

}