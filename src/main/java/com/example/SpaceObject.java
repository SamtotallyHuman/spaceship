package com.example;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class SpaceObject extends Component {

    private Point2D position;
    private Vec2 velocity;
    private Vec2 acceleration;

    public SpaceObject(Point2D position, Vec2 velocity, Vec2 acceleration) {
        super();
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    @Override
    public void onUpdate(double tpf) {
        velocity = velocity.add(acceleration.mul(tpf));
        position = position.add(velocity.x * tpf, velocity.y * tpf);
        entity.setPosition(position);
    }

    public void addAcceleration(Vec2 addingAcceleration) {
        acceleration = acceleration.add(addingAcceleration);
    }
}
