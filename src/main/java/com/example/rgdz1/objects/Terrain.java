package com.example.rgdz1.objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class Terrain extends Group {

    ImageView background;
    String backgroundName;

    Package packages[];
    Translate packagePosition[];

    Obstacle smallObstacles[];
    Translate smallObstaclePosition[];
    int smallObstacleHeight[], smallObstacleWidth[];

    Rectangle forest;
    Translate forestPosition;
    int forestHeight, forestWidth;
    String forestBackground;

    Ellipse water;
    Translate waterPosition;
    double waterRotation;
    int waterHeight, waterWidth;
    String waterBackground;

    Helipad helipads[];
    Translate helipadPosition[];
    int helipadDimension;

    Translate helicoptersStart;

    int variant;

    public Terrain(double dimension, int variant) {

        this.variant = variant;

        helipadPosition = new Translate[2];
        packagePosition = new Translate[4];
        smallObstaclePosition = new Translate[3];
        forestPosition = new Translate();
        waterPosition = new Translate();

        smallObstacleHeight = new int[3];
        smallObstacleWidth = new int[3];
        helipadDimension = (int) (0.1 * dimension);

        // TODO: sve navedeno iznad plus dimenzije treba kreirati u varijantama dole
        switch (variant) {
            case 1:                         // Suma

                helipadPosition[0] = new Translate(220, 100);
                helicoptersStart = new Translate(220 + 0.05 * dimension, 100 + 0.05 * dimension);
                helipadPosition[1] = new Translate(-300, -300);

                packagePosition[0] = new Translate(-0.01 * dimension + dimension / 3, -0.01 * dimension - dimension / 3);
                packagePosition[1] = new Translate(0, 0);
                packagePosition[2] = new Translate(-0.01 * dimension + dimension / 3, 0.01 * dimension + dimension / 3);
                packagePosition[3] = new Translate(-0.01 * dimension - dimension / 3, 0.01 * dimension + dimension / 3);

                smallObstaclePosition[0] = new Translate(-25, -300);
                smallObstaclePosition[1] = new Translate(-80, 100);
                smallObstaclePosition[2] = new Translate(-400, -30);

                smallObstacleHeight[0] = 200;
                smallObstacleHeight[1] = 200;
                smallObstacleHeight[2] = 50;

                smallObstacleWidth[0] = 50;
                smallObstacleWidth[1] = 50;
                smallObstacleWidth[2] = 200;

                forestHeight = 400;
                forestWidth = 400;
                forestPosition = new Translate(25, -500);

                backgroundName = "assets/forestBottom.jpg";
                forestBackground = "assets/forest.jpg";
                waterBackground = "assets/water.jpeg";

                break;
            case 2:                         // Livada

                helipadPosition[0] = new Translate(220, 100);
                helicoptersStart = new Translate(220 + 0.05 * dimension, 100 + 0.05 * dimension);
                helipadPosition[1] = new Translate(-300, 100);

                packagePosition[0] = new Translate(-0.01 * dimension + dimension / 3, -0.01 * dimension - dimension / 3);
                packagePosition[1] = new Translate(-0.01 * dimension - dimension / 3, -0.01 * dimension - dimension / 3);
                packagePosition[2] = new Translate(-0.01 * dimension + dimension / 3, 0.01 * dimension + dimension / 3);
                packagePosition[3] = new Translate(-0.01 * dimension - dimension / 3, 0.01 * dimension + dimension / 3);

                smallObstaclePosition[0] = new Translate(-25, -300);
                smallObstaclePosition[1] = new Translate(-80, 100);
                smallObstaclePosition[2] = new Translate(-400, -30);

                smallObstacleHeight[0] = 200;
                smallObstacleHeight[1] = 200;
                smallObstacleHeight[2] = 50;

                smallObstacleWidth[0] = 50;
                smallObstacleWidth[1] = 50;
                smallObstacleWidth[2] = 200;

                waterWidth = 200;
                waterHeight = 140;
                waterPosition = new Translate(240, 340);
                waterRotation = -30;

                backgroundName = "assets/grass.png";
                forestBackground = "assets/forest.jpg";
                waterBackground = "assets/water.jpeg";

                break;
            case 3:                         // Pesak

                helipadPosition[0] = new Translate(220, 100);
                helicoptersStart = new Translate(220 + 0.05 * dimension, 100 + 0.05 * dimension);
                helipadPosition[1] = new Translate(-300, 100);

                packagePosition[0] = new Translate(-0.01 * dimension + dimension / 3, -0.01 * dimension - dimension / 3);
                packagePosition[1] = new Translate(-0.01 * dimension - dimension / 3, -0.01 * dimension - dimension / 3);
                packagePosition[2] = new Translate(-0.01 * dimension + dimension / 3, 0.01 * dimension + dimension / 3);
                packagePosition[3] = new Translate(-0.01 * dimension - dimension / 3, 0.01 * dimension + dimension / 3);

                smallObstaclePosition[0] = new Translate(-25, -300);
                smallObstaclePosition[1] = new Translate(-80, 100);
                smallObstaclePosition[2] = new Translate(-400, -30);

                smallObstacleHeight[0] = 200;
                smallObstacleHeight[2] = 50;

                smallObstacleWidth[0] = 50;
                smallObstacleWidth[2] = 200;

                waterWidth = 700;
                waterHeight = 340;
                waterPosition = new Translate(0, 520);
                waterRotation = 0;

                backgroundName = "assets/sand.jpg";
                forestBackground = "assets/forest.jpg";
                waterBackground = "assets/seaWater.jpg";

                break;
        }

        background = new ImageView(new Image("file:" + backgroundName));
        background.setFitHeight(dimension);
        background.setFitWidth(dimension);
        background.setFitHeight(dimension);
        background.setFitWidth(dimension);
        background.toBack();
        background.setTranslateX(-dimension / 2);
        background.setTranslateY(-dimension / 2);

        packages = new Package[4];
        packages[0] = new Package(0.02 * dimension, 0.02 * dimension, packagePosition[0]);
        packages[1] = new Package(0.02 * dimension, 0.02 * dimension, packagePosition[1]);
        packages[2] = new Package(0.02 * dimension, 0.02 * dimension, packagePosition[2]);
        packages[3] = new Package(0.02 * dimension, 0.02 * dimension, packagePosition[3]);

        smallObstacles = new Obstacle[3];
        smallObstacles[0] = new Obstacle(smallObstacleWidth[0], smallObstacleHeight[0], smallObstaclePosition[0]);
        smallObstacles[1] = new Obstacle(smallObstacleWidth[1], smallObstacleHeight[1], smallObstaclePosition[1]);
        smallObstacles[2] = new Obstacle(smallObstacleWidth[2], smallObstacleHeight[2], smallObstaclePosition[2]);

        forest = new Rectangle(forestHeight, forestWidth);
        forest.setFill(new ImagePattern(new Image("file:" + forestBackground)));
        forest.getTransforms().add(forestPosition);

        water = new Ellipse(waterWidth, waterHeight);
        water.setFill(new ImagePattern(new Image("file:" + waterBackground)));
        water.setTranslateX(waterPosition.getX());
        water.setTranslateY(waterPosition.getY());
        water.setRotate(waterRotation);

        helipads = new Helipad[2];
        helipads[0] = new Helipad(0.1 * dimension, 0.1 * dimension, false);
        helipads[0].getTransforms().add(helipadPosition[0]);
        helipads[1] = new Helipad(0.1 * dimension, 0.1 * dimension, true);
        helipads[1].getTransforms().add(helipadPosition[1]);

        super.getChildren().addAll(background, forest, water);
        super.getChildren().addAll(packages);
        super.getChildren().addAll(smallObstacles);
        super.getChildren().addAll(helipads);
    }

    public Package[] getPackages() {
        return this.packages;
    }

    public Obstacle[] getObstacles() {
        return this.smallObstacles;
    }

    public Helipad[] getHelipads() {
        return this.helipads;
    }

    public Translate getHelicoptersStart() {
        return this.helicoptersStart;
    }

    public Bounds getBigObstacleBoundInLocal() {
        if (variant == 2 || variant == 3)
            return this.water.getBoundsInLocal();
        else if (variant == 1)
            return this.forest.getBoundsInLocal();
        else
            return null;
    }

}
