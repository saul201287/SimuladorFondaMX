package com.fxplay.config;

import com.almasb.fxgl.app.GameSettings;
import javafx.scene.image.Image;

public class Config {

    public static void configureGame(GameSettings settings) {
        Image imagen = new Image(Config.class.getResourceAsStream("/assets/textures/fondo.png"));
        settings.setWidth((int) imagen.getWidth());
        settings.setHeight((int) imagen.getHeight());
        settings.setTitle("Fonda MX");
        settings.setVersion("1.0");
    }

}
