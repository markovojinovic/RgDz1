package com.example.rgdz1.objects;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class Package extends Rectangle {

    public Package(double width, double height, Translate position) {
        super(width, height, Color.DARKRED);

        super.getTransforms().addAll(position);
    }

    public boolean handleCollision(Bounds helicopterBounds) {
        return super.getBoundsInParent().intersects(helicopterBounds);
    }
}
