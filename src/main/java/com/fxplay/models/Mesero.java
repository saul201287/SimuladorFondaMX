package com.fxplay.models;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import javafx.geometry.Point2D;
import java.util.List;
import com.almasb.fxgl.animation.Animation;

public class Mesero {
    private static Entity meseroEntity;
    private static boolean entregando = false;
    private static Point2D posicionInicial;
    private static int mesaActual = 0;

    public static Entity crearMesero(double x, double y) {
        posicionInicial = new Point2D(x, y);
        meseroEntity = FXGL.entityBuilder()
                .at(x, y)
                .viewWithBBox(FXGL.texture("mesero.png"))
                .scale(.15, .15)
                .buildAndAttach();
        return meseroEntity;
    }

    public static void iniciarServicio(List<Point2D> posicionesMesas) {
        if (meseroEntity == null || entregando) return;
        visitarSiguienteMesa(posicionesMesas);
    }

    private static void visitarSiguienteMesa(List<Point2D> posicionesMesas) {
        if (mesaActual >= posicionesMesas.size()) {
            mesaActual = 0;
            return;
        }

        Point2D posicionMesa = posicionesMesas.get(mesaActual);
        entregando = true;

        // Ir a la mesa
        Animation<?> irAMesa = animationBuilder()
            .duration(javafx.util.Duration.seconds(1))
            .translate(meseroEntity)
            .from(meseroEntity.getPosition())
            .to(posicionMesa)
            .build();
        
        irAMesa.start();

        // Esperar en la mesa y luego volver
        FXGL.runOnce(() -> {
            Animation<?> volverAPosicion = animationBuilder()
                .duration(javafx.util.Duration.seconds(1))
                .translate(meseroEntity)
                .from(meseroEntity.getPosition())
                .to(posicionInicial)
                .build();

            volverAPosicion.setOnFinished(() -> {
                entregando = false;
                mesaActual++;
                // Programar la siguiente visita
                FXGL.runOnce(() -> visitarSiguienteMesa(posicionesMesas), 
                    javafx.util.Duration.seconds(1));
            });
            
            volverAPosicion.start();
        }, javafx.util.Duration.seconds(2));
    }
} 