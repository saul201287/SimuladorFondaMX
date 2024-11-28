package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Comida {

    public Entity crearComida(double x, double y) {
        return FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("comida.png"))
                .scale(.1, .1)
                .buildAndAttach();
    }
}
