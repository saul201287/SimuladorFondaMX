package com.fxplay;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Main extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Juego de Disparos");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        System.out.println("Juego iniciado.");
        // Crear el jugador y agregarlo a la escena
        player = entityBuilder()
                .at(400, 500)
                .viewWithBBox(new Rectangle(50, 50, Color.BLUE))
                .with(new CollidableComponent(true))
                .type(EntityType.PLAYER)
                .buildAndAttach();

        // Crear algunos enemigos
        for (int i = 0; i < 5; i++) {
            createEnemy(i * 150, 100 + (i * 50));
        }

        // Inicializar el puntaje
        FXGL.set("score", 0);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
    }

    @Override
    protected void initInput() {
        // Mover el jugador
        getInput().addAction(new UserAction("Mover izquierda") {
            @Override
            protected void onAction() {
                player.translateX(-5);
            }
        }, KeyCode.LEFT);

        getInput().addAction(new UserAction("Mover derecha") {
            @Override
            protected void onAction() {
                player.translateX(5);
            }
        }, KeyCode.RIGHT);

        getInput().addAction(new UserAction("Mover arriba") {
            @Override
            protected void onAction() {
                player.translateY(-5);
            }
        }, KeyCode.UP);

        getInput().addAction(new UserAction("Mover abajo") {
            @Override
            protected void onAction() {
                player.translateY(5);
            }
        }, KeyCode.DOWN);

        // Disparar
        getInput().addAction(new UserAction("Disparar") {
            @Override
            protected void onAction() {
                shootBullet(player.getX() + player.getWidth() / 2, player.getY());
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initUI() {
        Text scoreText = getUIFactoryService().newText("", Color.BLACK, 20);
        scoreText.textProperty().bind(FXGL.getip("score").asString("Puntaje: %d"));
        addUINode(scoreText, 10, 10);
    }

    private void createEnemy(double x, double y) {
        // Crear enemigos
        Entity enemy = entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(50, 50, Color.RED))
                .with(new CollidableComponent(true))
                .type(EntityType.ENEMY)
                .buildAndAttach();

        // Movimiento simple del enemigo
        FXGL.getGameTimer().runAtInterval(() -> {
            if (enemy.isActive()) {
                enemy.translateY(2);
            }
        }, Duration.seconds(0.02));
    }

    private void shootBullet(double x, double y) {
        // Crear una bala
        Entity bullet = entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(10, 20, Color.GREEN))
                .with(new CollidableComponent(true))
                .type(EntityType.BULLET)
                .buildAndAttach();

        // Mover la bala hacia arriba
        FXGL.getGameTimer().runAtInterval(() -> {
            if (bullet.isActive()) {
                bullet.translateY(-10);
            }
        }, Duration.seconds(0.02));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();
                enemy.removeFromWorld();

                inc("score", 10);
            }
        });
    }

    public enum EntityType {
        PLAYER, ENEMY, BULLET
    }

    public static void main(String[] args) {
        launch(args);
    }
}