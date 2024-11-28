package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;

public class Recepcionista {

    public Entity crearRecepcionista(double x, double y) {
        return FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("recepcionista.png"))
                .scale(.2, .2)
                .buildAndAttach();
    }

    
} 