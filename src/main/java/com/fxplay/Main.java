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

        GameFactory.crearMesero();

        GameFactory.crearMesas(GameFactory.getMesero());

        GameFactory.crearRecepcionista();

        FXGL.runOnce(GameFactory::crearCocineros, javafx.util.Duration.seconds(1));

        FXGL.runOnce(GameFactory::crearComensales, javafx.util.Duration.seconds(2));

        FXGL.runOnce(GameFactory::crearComida, javafx.util.Duration.seconds(3));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
