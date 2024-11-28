package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Orden {
    private Entity ordenEntity;

    public Entity crearOrden(double x, double y) {
        ordenEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("orden.png"))
                .scale(.1, .1)
                .buildAndAttach();
        return ordenEntity;
    }

    public Entity getOrdenEntity() {
        return ordenEntity;
    }
}