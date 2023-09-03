package com.example.rgdz1.objects;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class Obstacle extends Rectangle {

    public Obstacle(double width, double height, Translate position){
        super(width, height);
        super.getTransforms().addAll(position);
        Image img = new Image("file:assets/wood.jpg");
        super.setFill(new ImagePattern(img));
    }

    public boolean handleCollision(Bounds helicopterBounds) {
        return super.getBoundsInParent().intersects(helicopterBounds);
    }
}
