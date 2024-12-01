package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;

public class Espera {

    private Entity esperaEntity;
    private boolean esperando = true;
    private static final double OFFSET_X = 50; 
    private static final double OFFSET_Y = -40; 

    public Entity crearEspera(Point2D posicionMesa) {
        double x = posicionMesa.getX() + OFFSET_X;
        double y = posicionMesa.getY() + OFFSET_Y;
        
        esperaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("espera.png"))
                .scale(.1, .1)
                .buildAndAttach();
        return esperaEntity;
    }

    public void eliminarEspera() {
        if (esperando && esperaEntity != null) {
            esperaEntity.removeFromWorld();
            esperando = false;
        }
    }

    public Entity getEsperaEntity() {
        return esperaEntity;
    }

}
