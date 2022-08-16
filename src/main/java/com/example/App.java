package com.example;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dev.editor.EntityInspector;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.texture.Texture;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App extends GameApplication {

    private List<Entity> spaceObjects;
    private List<Entity> allObjects;
    private Entity player;
    private Entity player2;
    private Entity sun;
    private Entity sun2;

    private static final double gravitationalConstant = 6.674e-11;
    private static final double gravityBoosterConstant = 150000000;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(2000);
        settings.setHeight(1000);
        settings.setTitle("Spaceship");
    }

    @Override
    protected void initGame() {

        spaceObjects = new ArrayList<>();
        allObjects = new ArrayList<>();

        player = FXGL.entityBuilder()
                .at(40, 40)
                .viewWithBBox(new Texture(FXGL.image("Spaceship.png")))
                .buildAndAttach();
        player.addComponent(new SpaceObject(new Point2D(player.getX(), player.getY()), new Point2D(0, 0), new Point2D(0, 0), 300));
        player.setRotationOrigin(new Point2D(player.getWidth()/2, player.getHeight()/2));
        spaceObjects.add(player);
        allObjects.add(player);

        player2 = FXGL.entityBuilder()
                .at(1960, 40)
                .viewWithBBox(new Texture(FXGL.image("Spaceship2.png")))
                .buildAndAttach();
        player2.addComponent(new SpaceObject(new Point2D(player.getX(), player.getY()), new Point2D(0, 0), new Point2D(0, 0), 300));
        player2.setRotationOrigin(new Point2D(player.getWidth()/2, player.getHeight()/2));
        spaceObjects.add(player2);
        allObjects.add(player2);


        sun = FXGL.entityBuilder()
                .at(500, 500)
                .viewWithBBox(new Circle(60, Color.RED))
                .buildAndAttach();
        sun.addComponent(new FixedObject(1000000000, new Point2D(sun.getX(), sun.getY())));
        allObjects.add(sun);

        sun2 = FXGL.entityBuilder()
                .at(1500, 500)
                .viewWithBBox(new Circle(60, Color.RED))
                .buildAndAttach();
        sun2.addComponent(new FixedObject(1000000000, new Point2D(sun2.getX(), sun2.getY())));
        allObjects.add(sun2);

    }

    /* Constantly Calculates all the forces between all objects using
    /       m_1*m_2
    / F = G---------
    /         r^2
    */
    @Override
    protected void onUpdate(double tpf) {
        
        for (Entity spaceObject : spaceObjects) {
            for (Entity object : allObjects) {

                double distance = FXGLMath.sqrt(Math.pow(spaceObject.getX()-object.getX(), 2) + Math.pow(spaceObject.getY()-object.getY(), 2));

                if (distance > 0.01) {
                    double m1 = spaceObject.getComponent(SpaceObject.class).getMass();
                    double m2 =  object.call("getMass");

                    double force = (gravityBoosterConstant * gravitationalConstant * m1 * m2)/Math.pow(distance, 2);
                    
                    spaceObject.getComponent(SpaceObject.class).addForce(force, new Point2D(object.getX()-spaceObject.getX(), object.getY()-spaceObject.getY()));
                }
            }
            spaceObject.getComponent(SpaceObject.class).calculateAcceleration();
        }

        System.out.println(player.getComponent(SpaceObject.class).getVelocity());
    }

    @Override
    protected void initInput() {

        FXGL.onKey(KeyCode.W, () -> {
            player.getComponent(SpaceObject.class).addForwardVelocity(3);
        });

        FXGL.onKey(KeyCode.S, () -> {
            player.getComponent(SpaceObject.class).addForwardVelocity(-3);
        });

        FXGL.onKey(KeyCode.A, () -> {
            player.rotateBy(-FXGLMath.PI/1.5);
        });

        FXGL.onKey(KeyCode.D, () -> {
            player.rotateBy(FXGLMath.PI/1.5);
        });

        FXGL.onKey(KeyCode.SPACE, () -> {
            player.getComponent(SpaceObject.class).autoBreak();
        });



        FXGL.onKey(KeyCode.UP, () -> {
            player2.getComponent(SpaceObject.class).addForwardVelocity(3);
        });

        FXGL.onKey(KeyCode.DOWN, () -> {
            player2.getComponent(SpaceObject.class).addForwardVelocity(-3);
        });

        FXGL.onKey(KeyCode.LEFT, () -> {
            player2.rotateBy(-FXGLMath.PI/1.5);
        });

        FXGL.onKey(KeyCode.RIGHT, () -> {
            player2.rotateBy(FXGLMath.PI/1.5);
        });

        FXGL.onKey(KeyCode.NUMPAD0, () -> {
            player2.getComponent(SpaceObject.class).autoBreak();
        });

    }

    public static void main(String[] args) {
        launch(args);
    }

}
