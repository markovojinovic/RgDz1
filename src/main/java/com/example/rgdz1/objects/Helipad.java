package com.example.rgdz1.objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class Helipad extends Group {

    private Rectangle fill;
    ImageView fuelImage;
    String fuelImageName;
    Circle outBorder;
    boolean fuel;

    public Helipad(double width, double height, boolean fuel) {
        this.fill = new Rectangle(width, height, Color.GRAY);
        fuelImageName = "assets/fuelTank.png";
        this.fuel = fuel;

        outBorder = new Circle(width / 2);
        outBorder.setFill(Color.TRANSPARENT);
        outBorder.setStroke(Color.WHITE);
        outBorder.setStrokeWidth(3.0);
        outBorder.getTransforms().add(new Translate(width / 2, width / 2));

        Rectangle rectangle1 = new Rectangle(Math.sqrt(width * width + height * height), 3, Color.WHITE);
        Rectangle rectangle2 = new Rectangle(Math.sqrt(width * width + height * height), 3, Color.WHITE);
        rectangle1.setTranslateY(width / 2 - 3);
        rectangle2.setTranslateY(width / 2 - 3);
        rectangle1.setTranslateX(-width / 4 + 3);
        rectangle2.setTranslateX(-width / 4 + 3);
        rectangle1.setRotate(45);
        rectangle2.setRotate(-45);

        if (fuel) {
            fuelImage = new ImageView(new Image("file:" + fuelImageName));
            fuelImage.setFitWidth(width * 0.4);
            fuelImage.setFitHeight(height * 0.4);
        }

        super.getChildren().addAll(fill, outBorder, rectangle1, rectangle2);
        if (fuel)
            super.getChildren().add(fuelImage);
    }

    public boolean handleCollision(Bounds helicopterBounds) {
        return this.fill.getBoundsInParent().intersects(helicopterBounds);
    }

    public boolean isFuel() {
        return this.fuel;
    }

    public void deleteFuel() {
        this.fuel = false;
        this.getChildren().remove(this.fuelImage);
    }

    public Circle getCircle(){
        return this.outBorder;
    }
}
