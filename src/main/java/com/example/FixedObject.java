package com.example;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;

public class FixedObject extends Component {
    
    private double mass;
    private Point2D position;

    public FixedObject(double mass, Point2D position) {
        super();
        this.mass = mass;
        this.position = position;
    }

    public double getMass() {
        return mass;
    }

}
