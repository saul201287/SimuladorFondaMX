package com.fxplay;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.fxplay.config.Config;
import com.fxplay.factories.GameFactory;
import com.almasb.fxgl.dsl.FXGL;

public class Main extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        Config.configureGame(settings);
    }

    @Override
    protected void initGame() {
        GameFactory.crearFondo(); // Fondo estático, sin dependencias

        // Crear el mesero primero
        GameFactory.crearMesero();

        // Crear mesas con referencia al mesero
        GameFactory.crearMesas(GameFactory.getMesero());

        // Crear recepcionista (depende de las mesas)
        GameFactory.crearRecepcionista();

        // Retraso para crear cocineros (simulación visual)
        FXGL.runOnce(GameFactory::crearCocineros, javafx.util.Duration.seconds(1));

        // Retraso para comensales (simulación de llegadas)
        FXGL.runOnce(GameFactory::crearComensales, javafx.util.Duration.seconds(2));

        // Retraso para comida (si es visual o estática)
        FXGL.runOnce(GameFactory::crearComida, javafx.util.Duration.seconds(3));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
