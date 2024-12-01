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
import com.fxplay.models.Espera;

public class GameFactory {

    private static List<Point2D> posicionesMesas = new ArrayList<>();
    private static List<Mesa> mesas = new ArrayList<>();
    private static List<Comensal> comensales = new ArrayList<>();
    private static Recepcionista recepcionista;
    private static List<Cocinero> cocineros = new ArrayList<>();
    private static Mesero mesero;

    public static Entity crearFondo() {
        return FXGL.entityBuilder()
                .at(0, 0)
                .viewWithBBox(FXGL.texture("fondo.png"))
                .scale(1, 1)
                .buildAndAttach();
    }

    public static void crearMesas(Mesero mesero) {
        int startX = 200;
        int startY = 0;
        int id = 1;

        for (int fila = 0; fila < GameConstants.MESAS_POR_FILA; fila++) {
            for (int columna = 0; columna < GameConstants.MESAS_POR_COLUMNA; columna++) {
                double x = startX + (columna * GameConstants.ESPACIO_ENTRE_X);
                double y = startY + (fila * GameConstants.ESPACIO_ENTRE_Y);

                Mesa mesa = new Mesa(id++, mesero);
                mesa.crearMesa(x, y);
                mesas.add(mesa);
                posicionesMesas.add(new Point2D(x, y));
                System.out.println("Creando mesa con coordenadas: x=" + mesa.getX() + ", y=" + mesa.getY());

            }
        }
    }

    public static void crearCocineros() {
        Cocinero cocinero1 = new Cocinero();
        Cocinero cocinero2 = new Cocinero();

        cocinero1.crearCocinero(-100, 100);
        cocinero2.crearCocinero(0, 100);

        cocineros.add(cocinero1);
        cocineros.add(cocinero2);

        cocinero1.cocinar();
        cocinero2.cocinar();
    }

    public static void crearMesero() {
        mesero = new Mesero();
        mesero.crearMesero(50, 320);
    }

    public static void crearRecepcionista() {
        if (mesas.isEmpty()) {
            throw new IllegalStateException("Las mesas deben estar creadas antes de crear el recepcionista");
        }

        recepcionista = new Recepcionista(mesas);
        recepcionista.crearRecepcionista(800, 320);
    }

    public static void crearComensales() {
        if (recepcionista == null) {
            throw new IllegalStateException("El recepcionista debe estar creado antes de crear comensales");
        }

        double startX = 900;
        double startY = 320;

        for (int i = 0; i < 20; i++) {
            Comensal comensal = new Comensal(recepcionista);
            Entity comensalEntity = comensal.crearComensal(startX, startY);
            comensales.add(comensal);
            FXGL.runOnce(comensal::start, javafx.util.Duration.seconds(i * 0.5));
        }
    }

    public static void crearComida() {
        double startX = -180;
        double startY = 210;

        for (int fila = 0; fila < 5; fila++) {
            double x = startX + (fila * GameConstants.COMIDA_ESPACIO_ENTRE_X);
            Comida comida = new Comida();
            comida.crearComida(x, startY);
        }
    }

    public static List<Mesa> getMesas() {
        return mesas;
    }

    public static Mesero getMesero() {
        return mesero;
    }

    public static List<Cocinero> getCocineros() {
        return cocineros;
    }
}
