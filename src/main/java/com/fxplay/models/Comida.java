package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Comida {
    private Entity comidaEntity;

    public Entity crearComida(double x, double y) {
        comidaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("comida.png"))
                .scale(.1, .1)
                .buildAndAttach();
        return comidaEntity;
    }

    public Entity getComidaEntity() {
        return comidaEntity;
    }
}
