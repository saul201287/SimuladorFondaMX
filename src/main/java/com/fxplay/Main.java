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
        GameFactory.crearFondo();
        GameFactory.crearMesas();
        
        // Retraso para cocineros
        FXGL.runOnce(() -> {
            GameFactory.crearCocineros();
        }, javafx.util.Duration.seconds(1));
        
        // Retraso para recepcionista
        FXGL.runOnce(() -> {
            GameFactory.crearRecepcionista();
        }, javafx.util.Duration.seconds(2));
        
        // Retraso para mesero
        FXGL.runOnce(() -> {
            GameFactory.crearMesero();
        }, javafx.util.Duration.seconds(3));
        
        // Retraso para comensales
        FXGL.runOnce(() -> {
            GameFactory.crearComensales();
        }, javafx.util.Duration.seconds(4));
        
        // Retraso para comida y órdenes
        FXGL.runOnce(() -> {
            GameFactory.crearComida();
            GameFactory.crearOrden();
        }, javafx.util.Duration.seconds(4.5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
