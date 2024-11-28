package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Recepcionista {
    private Entity recepcionistaEntity;

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
} 