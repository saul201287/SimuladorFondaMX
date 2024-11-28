package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import javafx.geometry.Point2D;

public class Comensal {
    private Entity comensalEntity;
    private boolean estaComiendo = false;

    public Entity crearComensal(double x, double y) {
        comensalEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("comensal.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return comensalEntity;
    }

    public void moverAMesa(double mesaX, double mesaY) {
        if (comensalEntity == null) return;

        animationBuilder()
            .duration(javafx.util.Duration.seconds(2))
            .translate(comensalEntity)
            .from(comensalEntity.getPosition())
            .to(new Point2D(mesaX, mesaY))
            .buildAndPlay();
            
        estaComiendo = true;
        
        // Programar la salida del comensal después de un tiempo aleatorio
        double tiempoComida = 5 + Math.random() * 5; // Entre 5 y 10 segundos
        FXGL.runOnce(() -> abandonarMesa(), javafx.util.Duration.seconds(tiempoComida));
    }

    public void abandonarMesa() {
        if (comensalEntity == null || !estaComiendo) return;

        estaComiendo = false;
        
        animationBuilder()
            .duration(javafx.util.Duration.seconds(2))
            .translate(comensalEntity)
            .from(comensalEntity.getPosition())
            .to(new Point2D(900, 320)) 
            //.onFinished(() -> comensalEntity.removeFromWorld())
            .buildAndPlay();
    }
} 