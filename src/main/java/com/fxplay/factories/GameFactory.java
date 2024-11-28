package com.fxplay.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.fxplay.models.Mesa;
import com.fxplay.utils.GameConstants;
import com.fxplay.models.Cocinero;
import com.fxplay.models.Mesero;
import com.fxplay.models.Orden;
import com.fxplay.models.Comensal;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import com.fxplay.models.Recepcionista;
import com.fxplay.models.Comida;

public class GameFactory {
    private static List<Point2D> posicionesMesas = new ArrayList<>();

    public static Entity crearFondo() {
        return FXGL.entityBuilder()
            .at(0, 0)
            .viewWithBBox(FXGL.texture("fondo.png"))
            .scale(1,1)
            .buildAndAttach();
    }
    
    public static void crearMesas() {
        int startX = 200;
        int startY = 0;
        
        for (int fila = 0; fila < GameConstants.MESAS_POR_FILA; fila++) {
            for (int columna = 0; columna < GameConstants.MESAS_POR_COLUMNA; columna++) {
                double x = startX + (columna * GameConstants.ESPACIO_ENTRE_X);
                double y = startY + (fila * GameConstants.ESPACIO_ENTRE_Y);
                Mesa.crearMesa(x, y);
                posicionesMesas.add(new Point2D(x, y));
            }
        }
    }

    public static void crearCocineros() {
        Cocinero cocinero1 = new Cocinero();
        Cocinero cocinero2 = new Cocinero();
        
        cocinero1.crearCocinero(-100, 100);
        cocinero2.crearCocinero(0, 100);
        
        cocinero1.cocinarCocinero();
        cocinero2.cocinarCocinero();    
    }

    public static void crearMesero() {
        Mesero.crearMesero(50, 320);
        // Iniciar el servicio después de un pequeño retraso
        FXGL.runOnce(() -> {
            Mesero.iniciarServicio(posicionesMesas);
        }, javafx.util.Duration.seconds(2));
    }

    public static void crearComensales() {
        double startX = 900;
        double startY = 320;

        for (int i = 0; i < 10; i++) {
            Comensal comensal = new Comensal();
            Entity comensalEntity = comensal.crearComensal(startX, startY);
            final int index = i;

            FXGL.runOnce(() -> {
                Point2D posicionMesa = posicionesMesas.get(index);
                comensal.moverAMesa(posicionMesa.getX(), posicionMesa.getY());
            }, javafx.util.Duration.seconds(i * 0.5));
            System.out.println("Comensal " + i);
        }
    }

    public static void crearRecepcionista() {
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.crearRecepcionista( 800, 320); 
    }

    public static void crearComida() {
        double startX = -180;
        double startY = 210;

        
        for (int fila = 0; fila < 5; fila++) {
            double x = startX + (fila * GameConstants.COMIDA_ESPACIO_ENTRE_X);
            Comida comida = new Comida();
            comida.crearComida( x, startY);
        } 
    }
    
    public static void crearOrden() {

        double startX = -200;
        double startY = 50;
        
        for (int fila = 0; fila < 5; fila++) {
            double y = startY + (fila * GameConstants.ORDEN_ESPACIO_ENTRE_Y);
            Orden orden = new Orden();
            orden.crearOrden(startX, y);
        } 
    }   
} 