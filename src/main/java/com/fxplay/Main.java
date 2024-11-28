package com.fxplay;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.fxplay.config.Config;
import com.fxplay.factories.GameFactory;

public class Main extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        Config.configureGame(settings);
    }

    @Override
    protected void initGame() {
        GameFactory.crearFondo();
        GameFactory.crearMesas();
        GameFactory.crearCocineros();
        GameFactory.crearRecepcionista();
        GameFactory.crearMesero();
        GameFactory.crearComensales();
        GameFactory.crearComida();
        GameFactory.crearOrden();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
