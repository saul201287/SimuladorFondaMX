package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.animation.Interpolators;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

public class Cocinero {

    private boolean cocinando = false;
    private Entity cocineroEntity;

    public Entity crearCocinero(double x, double y) {
        cocineroEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("cocinero.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return cocineroEntity;
    }

    public void cocinarCocinero() {
        if (cocineroEntity == null) return;

        animationBuilder()
            .duration(javafx.util.Duration.seconds(1))
            .translate(cocineroEntity)
            .from(cocineroEntity.getPosition())
            .to(cocineroEntity.getPosition().subtract(0, 200))
            .buildAndPlay();

        FXGL.runOnce(() -> {
            animationBuilder()
                .delay(javafx.util.Duration.seconds(3))
                .duration(javafx.util.Duration.seconds(1))
                .translate(cocineroEntity)
                .from(cocineroEntity.getPosition())
                .to(cocineroEntity.getPosition().add(0, 200))
                .buildAndPlay();
        }, javafx.util.Duration.seconds(1));
    }

    
} 