package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Orden {

    public Entity crearOrden(double x, double y) {
        return FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("orden.png"))
                .scale(.1, .1)
                .buildAndAttach();
    }
}