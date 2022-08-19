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
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;

import static com.almasb.fxgl.dsl.FXGL.*;

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
    private Entity planet1;

    private static final double gravitationalConstant = 6.674e-11;
    private static final double gravityBoosterConstant = 150000000;

    public enum EntityType {
        PLAYER, SUN, PLANET
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(2400);
        settings.setHeight(1300);
        settings.setTitle("Spaceship");
        settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void initGame() {

        spaceObjects = new ArrayList<>();
        allObjects = new ArrayList<>();

        player = FXGL.entityBuilder()
                .type(EntityType.PLAYER)
                .at(40, 40)
                .viewWithBBox(new Texture(FXGL.image("Spaceship.png")))
                .buildAndAttach();
        player.addComponent(new SpaceObject(new Point2D(player.getX(), player.getY()), new Point2D(0, 0), new Point2D(0, 0), 300));
        player.setRotationOrigin(new Point2D(player.getWidth()/2, player.getHeight()/2));
        spaceObjects.add(player);
        allObjects.add(player);

        /*player2 = FXGL.entityBuilder()
                .type(EntityType.PLAYER)
                .at(1960, 40)
                .viewWithBBox(new Texture(FXGL.image("Spaceship2.png")))
                .with(new CollidableComponent(true))
                .buildAndAttach();
        player2.addComponent(new SpaceObject(new Point2D(player.getX(), player.getY()), new Point2D(0, 0), new Point2D(0, 0), 300));
        player2.setRotationOrigin(new Point2D(player.getWidth()/2, player.getHeight()/2));
        spaceObjects.add(player2);
        allObjects.add(player2);*/


        sun = FXGL.entityBuilder()
                .type(EntityType.SUN)
                .at(1150, 550)
                .view(new Circle(100, 100, 100, Color.RED))
                .bbox(new HitBox(BoundingShape.circle(100)))
                .buildAndAttach();
        sun.addComponent(new FixedObject(2000000000, sun.getCenter()));
        allObjects.add(sun);

        planet1 = FXGL.entityBuilder()
                .type(EntityType.PLANET)
                .at(1150, 100)
                .view(new Circle(40, 40, 40, Color.GREEN))
                .bbox(new HitBox(BoundingShape.circle(40)))
                .buildAndAttach();
        planet1.addComponent(new SpaceObject(planet1.getCenter(), new Point2D(190, -70), new Point2D(10, 0), 10000000));
        spaceObjects.add(planet1);
        allObjects.add(planet1);
    }

    /* Constantly Calculates all the forces between all objects using
    /       m_1*m_2
    / F = G---------
    /         r^2
    */
    @Override
    protected void onUpdate(double tpf) {
        if (!spaceObjects.isEmpty()) {
            for (Entity spaceObject : spaceObjects) {
                for (Entity object : allObjects) {
    
                    double distance = object.getCenter().subtract(spaceObject.getCenter()).magnitude();
    
                    if (distance > 0.01) {
                        double m1 = spaceObject.getComponent(SpaceObject.class).getMass();
                        double m2 =  object.call("getMass");
    
                        double force = (gravityBoosterConstant * gravitationalConstant * m1 * m2)/Math.pow(distance, 2);

                        spaceObject.getComponent(SpaceObject.class).addForce(force, object.getCenter().subtract(spaceObject.getCenter()));
                        
                        Point2D vector = object.getCenter().subtract(spaceObject.getCenter());
                        
                        double threshold = spaceObject.getWidth()/2 + object.getWidth()/2;

                        if (vector.magnitude() == threshold) {// psuedo collision handling

                            if (spaceObject.hasComponent(SpaceObject.class) && object.hasComponent(SpaceObject.class)) {
                                if (spaceObject.getComponent(SpaceObject.class).getMass() > object.getComponent(SpaceObject.class).getMass()) {
                                    object.getComponent(SpaceObject.class).setAccelerationAndVelocity(spaceObject.getComponent(SpaceObject.class).getAcceleration(), spaceObject.getComponent(SpaceObject.class).getVelocity());
                                } else {
                                    spaceObject.getComponent(SpaceObject.class).setAccelerationAndVelocity(object.getComponent(SpaceObject.class).getAcceleration(), object.getComponent(SpaceObject.class).getVelocity());
                                }
                            } else {
                                spaceObject.getComponent(SpaceObject.class).stopAcceleration();
                            }
                            
                            spaceObject.getComponent(SpaceObject.class).addForce(force, (object.getCenter().subtract(spaceObject.getCenter())).multiply(-1));
                        
                        } else if (vector.magnitude() < threshold) {

                            if (spaceObject.hasComponent(SpaceObject.class) && object.hasComponent(SpaceObject.class)) {
                                if (spaceObject.getComponent(SpaceObject.class).getMass() > object.getComponent(SpaceObject.class).getMass()) {
                                    object.getComponent(SpaceObject.class).setAccelerationAndVelocity(spaceObject.getComponent(SpaceObject.class).getAcceleration(), spaceObject.getComponent(SpaceObject.class).getVelocity());
                                } else {
                                    spaceObject.getComponent(SpaceObject.class).setAccelerationAndVelocity(object.getComponent(SpaceObject.class).getAcceleration(), object.getComponent(SpaceObject.class).getVelocity());
                                }
                            } else {
                                spaceObject.getComponent(SpaceObject.class).stopAcceleration();
                            }

                            spaceObject.getComponent(SpaceObject.class).addForce(force * threshold/vector.magnitude(), (object.getCenter().subtract(spaceObject.getCenter())).multiply(-1)); 
                        
                        }

                    }
                }
                spaceObject.getComponent(SpaceObject.class).calculateAcceleration();
            }
        }
    }

    @Override
    protected void initInput() {

        FXGL.onKey(KeyCode.W, () -> {
            if(spaceObjects.contains(player)) {
                player.getComponent(SpaceObject.class).addForwardVelocity(3);
            }
        });

        FXGL.onKey(KeyCode.S, () -> {
            if(spaceObjects.contains(player)) {
                player.getComponent(SpaceObject.class).addForwardVelocity(-3);
            }
        });

        FXGL.onKey(KeyCode.A, () -> {
            if(spaceObjects.contains(player)) {
                player.rotateBy(-FXGLMath.PI/1.5);
            }
        });

        FXGL.onKey(KeyCode.D, () -> {
            if(spaceObjects.contains(player)) {
                player.rotateBy(FXGLMath.PI/1.5);
            }
        });

        FXGL.onKey(KeyCode.SPACE, () -> {
            if(spaceObjects.contains(player)) {
                player.getComponent(SpaceObject.class).autoBreak();
            }
        });



        /*FXGL.onKey(KeyCode.UP, () -> {
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
        });*/

    }

    public static void main(String[] args) {
        launch(args);
    }

}
