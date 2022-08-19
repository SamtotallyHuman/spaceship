package com.example;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;

public class SpaceObject extends Component {

    private Point2D position;
    private Point2D velocity;
    private Point2D acceleration;
    private Point2D pointingDirection;
    private Point2D resultantForce;
    private double mass;

    public SpaceObject(Point2D position, Point2D velocity, Point2D acceleration, double mass) {
        super();
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
        resultantForce = new Point2D(0, 0);
    }

    @Override
    public void onUpdate(double tpf) {
        velocity = velocity.add(acceleration.multiply(tpf));
        position = position.add(velocity.getX() * tpf, velocity.getY() * tpf);
        entity.setPosition(position);
    }

    public void addForwardVelocity(double magnitude) {
        pointingDirection = new Point2D(FXGLMath.cos(FXGLMath.toRadians(entity.getRotation())), FXGLMath.sin(FXGLMath.toRadians(entity.getRotation())));
        velocity = velocity.add(pointingDirection.multiply(magnitude));
    }

    public void autoBreak() {
        velocity = velocity.multiply(0.95);
    }

    public void addAcceleration(Point2D addingAcceleration) {
        acceleration = acceleration.add(addingAcceleration);
    }

    public void addVelocity(Point2D addingVelocity) {
        velocity = velocity.add(addingVelocity);
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void addForce(double forceMagnitude, Point2D direction) {
        // Normalise the dircetion vector.
        direction = direction.normalize();

        direction = direction.multiply(forceMagnitude);
        resultantForce = resultantForce.add(direction);
    }

    public void calculateAcceleration() {
        acceleration = resultantForce.multiply(1/mass);
        resultantForce = new Point2D(0, 0);
    }

    public void stopAcceleration () {
        velocity = new Point2D(0, 0);
        acceleration = new Point2D(0, 0);
    }

    public double getMass() {
        return mass;
    }

    public void setAccelerationAndVelocity(Point2D accel, Point2D vel) {
        velocity = vel;
        acceleration = accel;
    }

    public Point2D getAcceleration() {
        return acceleration;
    }

}
