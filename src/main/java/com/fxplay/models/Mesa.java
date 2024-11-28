package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import com.fxplay.utils.GameConstants;

public class Mesa {

    //private boolean ocupado = false;

    public static Entity crearMesa(double x, double y) {
        return FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("mesa.png"))
                .scale(GameConstants.MESA_SCALE, GameConstants.MESA_SCALE)
                .buildAndAttach();
    }
} 