package com.fxplay.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.fxplay.models.Mesa;

public class GameFactory {
    public static Entity crearFondo() {
        return FXGL.entityBuilder()
            .at(0, 0)
            .viewWithBBox(FXGL.texture("fondo.png"))
            .scale(1,1)
            .buildAndAttach();
    }
    
    public static void crearMesas() {
        int mesasPorFila = 3;
        int mesasPorColumna = 5;
        int espacioEntreX = 150;
        int espacioEntreY = 100;
        int startX = 200;
        int startY = 0;
        
        for (int fila = 0; fila < mesasPorFila; fila++) {
            for (int columna = 0; columna < mesasPorColumna; columna++) {
                double x = startX + (columna * espacioEntreX);
                double y = startY + (fila * espacioEntreY);
                Mesa.crearMesa(x, y);
            }
        }
    }
} 