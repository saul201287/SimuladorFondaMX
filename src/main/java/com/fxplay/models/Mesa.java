package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import com.fxplay.utils.GameConstants;

public class Mesa {
    private Entity mesaEntity;
    private boolean ocupado = false;

    public Entity crearMesa(double x, double y) {
        mesaEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("mesa.png"))
                .scale(GameConstants.MESA_SCALE, GameConstants.MESA_SCALE)
                .buildAndAttach();
        return mesaEntity;
    }

    public Entity getMesaEntity() {
        return mesaEntity;
    }
} 