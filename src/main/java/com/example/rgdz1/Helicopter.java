package com.example.rgdz1;

import com.example.rgdz1.objects.Helipad;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.io.File;

// TODO: ispraviti da se ne pokrece centralna osa rotora

enum State {
    Stopped, FlyUpp, FlyDown, Raddy
};

public class Helicopter extends Group {

    private static final double SPEED_INDICATOR_WIDTH = 200;
    private static final double SPEED_INDICATOR_HEIGHT = 15;
    private static final double MAX_HELICOPTER_SPEED = 100;
    private static final double FUEL_LEVEL_STEP = 0.0005;

    private double speedCircleX = SPEED_INDICATOR_WIDTH / 2;
    private double speedCircleY, heightIndicatorWidth = 0;
    public Rectangle speedIndicator, heightIndicatorDown, heightIndicatorUp;
    public Circle speedCircle;

    private Point2D direction;
    private Rotate rotate;
    private Translate position;
    private double speed;

    private Ellipse cockpit;
    private Rectangle tail;
    private Rectangle tailRotor;
    private Rectangle[] rotorElise = new Rectangle[3];
    private double rotorDiameter;
    private Group rotor;
    private Rotate[] rotorAngle = new Rotate[3];
    private Translate[] rotorTranslate = new Translate[3];
    private double fuelLevel = 100.0;
    private State state = State.Stopped;
    private boolean rotorOn = false;
    private Timeline flyUppAnimation, flyDownAnimaiton;
    public Line needle;
    public Arc scale, val;

    public Helicopter(double width, double height, double winWidth, double winHeight) {
        this.rotorDiameter = 2 * (height - width / 2) / 3;

        Stop[] stops = new Stop[]{
                new Stop(0, Color.ORANGE),
                new Stop(1, Color.BLUE)
        };

        this.cockpit = new Ellipse(0, 0, width / 3, height / 3);
        this.cockpit.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops));

        double tailWidth = 0.2 * width;
        double tailHeight = 2 * (height - width / 2) / 3;
        this.tail = new Rectangle(tailWidth, tailHeight);
        this.tail.setFill(Color.BLUE);

        double tailRotorHeight = tailHeight / 2;
        this.tailRotor = new Rectangle(tailWidth, tailRotorHeight);
        this.tailRotor.setFill(Color.BLUE);

        this.tail.setTranslateY(width / 2);
        this.tail.setTranslateX(-tailWidth / 2);
        this.tailRotor.getTransforms().addAll(
                new Translate(-tailWidth / 2, height / 2),
                new Rotate(90, tailWidth / 2, tailRotorHeight / 2)
        );

        for (int i = 0; i < 3; i++) {
            this.rotorElise[i] = new Rectangle(2 * tailWidth / 3, tailHeight);
            this.rotorElise[i].setFill(Color.BLACK);
        }
        rotorAngle[0] = new Rotate(40);
        rotorTranslate[0] = new Translate(0, -2 * tailWidth / 3);
        rotorAngle[1] = new Rotate(160);
        rotorTranslate[1] = new Translate(2 * tailWidth / 3, 0);
        rotorAngle[2] = new Rotate(280);
        rotorTranslate[2] = new Translate(0, 0);

        rotor = new Group();
        for (int i = 0; i < 3; i++) {
            this.rotorElise[i].getTransforms().addAll(rotorTranslate[i], rotorAngle[i]);
            rotor.getChildren().add(rotorElise[i]);
        }

        heightIndicatorDown = new Rectangle(SPEED_INDICATOR_HEIGHT, SPEED_INDICATOR_WIDTH);
        heightIndicatorDown.setFill(Color.BLUE);
        heightIndicatorDown.setTranslateX(-winWidth / 2 + 30);
        heightIndicatorDown.setTranslateY(-winHeight / 4 - 140);
        heightIndicatorUp = new Rectangle(SPEED_INDICATOR_HEIGHT, SPEED_INDICATOR_WIDTH);
        heightIndicatorUp.setFill(Color.RED);
        heightIndicatorUp.setTranslateX(-winWidth / 2 + 30);
        heightIndicatorUp.setTranslateY(-winHeight / 4 - 140);

        speedIndicator = new Rectangle(SPEED_INDICATOR_WIDTH, SPEED_INDICATOR_HEIGHT);
        speedIndicator.setFill(Color.BLUE);
        speedIndicator.setRotate(90);
        speedIndicator.setTranslateX(winWidth / 4 + 55);
        speedIndicator.setTranslateY(-winHeight / 4 - 55);
        speedCircle = new Circle(SPEED_INDICATOR_HEIGHT / 2, Color.RED);
        speedCircle.setCenterX(speedCircleX + winWidth / 4 + 55);
        speedCircleY = -winHeight / 4 - 55 + SPEED_INDICATOR_HEIGHT / 2;
        speedCircle.setCenterY(speedCircleY);

        flyUppAnimation = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(heightIndicatorUp.heightProperty(), SPEED_INDICATOR_WIDTH, Interpolator.EASE_BOTH),
                        new KeyValue(this.scaleXProperty(), 1, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(heightIndicatorUp.heightProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(this.scaleXProperty(), 1.1, Interpolator.EASE_BOTH)
                )
        );
        flyUppAnimation.setOnFinished(event -> {
            state = State.Raddy;
        });

        flyDownAnimaiton = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(heightIndicatorUp.heightProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(this.scaleXProperty(), 1.1, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(heightIndicatorUp.heightProperty(), SPEED_INDICATOR_WIDTH, Interpolator.EASE_BOTH),
                        new KeyValue(this.scaleXProperty(), 1, Interpolator.EASE_BOTH)
                )
        );
        flyDownAnimaiton.setOnFinished(event -> {
            state = State.Stopped;
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(event1 -> {
                rotorOn = false;
            });
            delay.playFromStart();
        });

        double arcPosX= 0;
        double arcPosY= -winHeight/2 *0.8;
        this.scale = new Arc(arcPosX, arcPosY, 50, 50, 180, -180);
        scale.setType(ArcType.OPEN);
        scale.setFill(Color.TRANSPARENT);
        scale.setStroke(Color.BLACK);
        scale.setStrokeWidth(5);

        this.val = new Arc(arcPosX, arcPosY, 50, 50, 180, -40);
        val.setType(ArcType.OPEN);
        val.setFill(Color.TRANSPARENT);
        val.setStroke(Color.RED);
        val.setStrokeWidth(5);

        this.needle = new Line(0, 0, 50,0);
        this.needle.setFill ( Color.BLACK );
        this.needle.setStrokeWidth(5);
        this.needle.getTransforms ( ).addAll (
                new Translate( arcPosX, arcPosY)
        );


        super.getChildren().addAll(this.cockpit, this.tail, this.tailRotor, rotor);

        this.direction = new Point2D(0, -1);
        this.rotate = new Rotate(0);
        this.position = new Translate();

        super.getTransforms().addAll(
                this.position,
                this.rotate
        );
    }

    private boolean isWallHit(double left, double right, double up, double down) {
        Bounds cockpitBounds = this.cockpit.localToScene(this.cockpit.getBoundsInLocal());
        Bounds tailBounds = this.tail.localToScene(this.tail.getBoundsInLocal());

        double cockpitMinX = cockpitBounds.getCenterX() - rotorDiameter;
        double cockpitMaxX = cockpitBounds.getCenterX() + rotorDiameter;
        double cockpitMinY = cockpitBounds.getCenterY() - rotorDiameter;
        double cockpitMaxY = cockpitBounds.getCenterY() + rotorDiameter;

        boolean cockpitWallHit = cockpitMinX <= left || cockpitMaxX >= right || cockpitMinY <= up || cockpitMaxY >= down;

        double tailMinX = tailBounds.getMinX();
        double tailMaxX = tailBounds.getMaxX();
        double tailMinY = tailBounds.getMinY();
        double tailMaxY = tailBounds.getMaxY();

        boolean tailWallHit = tailMinX <= left || tailMaxX >= right || tailMinY <= up || tailMaxY >= down;


        return cockpitWallHit || tailWallHit;
    }

    public void rotate(double dAngle, double left, double right, double up, double down) {
        if (state == State.Raddy) {
            double oldAngle = this.rotate.getAngle();

            double newAngle = oldAngle + dAngle;
            this.rotate.setAngle(newAngle);

            if (this.isWallHit(left, right, up, down)) {
                this.rotate.setAngle(oldAngle);
            } else {
                double magnitude = this.direction.magnitude();
                this.direction = new Point2D(
                        magnitude * Math.sin(Math.toRadians(newAngle)),
                        -magnitude * Math.cos(Math.toRadians(newAngle))
                );
            }
        }
    }

    public void stop() {
        double oldX = this.position.getX();
        double oldY = this.position.getY();
        this.speed = 0;
        speedCircleY = ((-speed / MAX_HELICOPTER_SPEED) * (SPEED_INDICATOR_WIDTH / 2)) + (SPEED_INDICATOR_HEIGHT / 2 - 750.0 / 4 - 55);
        speedCircle.setCenterY(speedCircleY);
        this.position.setX(oldX);
        this.position.setY(oldY);
    }

    public void playPacketSound() {
        String path = getClass().getResource("refren_paket.mp3").getPath();
        Media media = new Media(new File(path).toURI().toString());
        MediaPlayer refren_paket = new MediaPlayer(media);
        refren_paket.play();
    }

    public void playFlyUpSound() {
        File mediaFile2 = new File("refren_poletanje.mp3");
        Media media2 = new Media(mediaFile2.toURI().toString());
        MediaPlayer refren_poletanje = new MediaPlayer(media2);
        refren_poletanje.play();
    }

    public void changeSpeed(double dSpeed) {
        if (state == State.Raddy) {
            if (Math.abs(this.speed + dSpeed) < MAX_HELICOPTER_SPEED) {
                this.speed += dSpeed;
                speedCircleY = ((-speed / MAX_HELICOPTER_SPEED) * (SPEED_INDICATOR_WIDTH / 2)) + (SPEED_INDICATOR_HEIGHT / 2 - 750.0 / 4 - 55);
                speedCircle.setCenterY(speedCircleY);
                this.fuelLevel -= this.FUEL_LEVEL_STEP * Math.abs(this.speed);
                double angle = 180.0 - ((fuelLevel / 100.0) * -180.0);
                double length = Math.sqrt(Math.pow(this.needle.getEndX() - this.needle.getStartX(), 2)
                        + Math.pow(this.needle.getEndY() - this.needle.getStartY(), 2));
                double endX = this.needle.getStartX() + length * Math.cos(Math.toRadians(angle));
                double endY = this.needle.getStartY() + length * Math.sin(Math.toRadians(angle));
                this.needle.setEndX(endX);
                this.needle.setEndY(endY);
                if (fuelLevel <= 0)
                    state = State.FlyDown;
            }
        }
    }

    public void startStop() {
        if (state == State.Stopped && fuelLevel > 0)
            state = State.FlyUpp;
        else if (state == State.Raddy) {
            state = State.FlyDown;
            speed = 0;
            speedCircleY = ((-speed / MAX_HELICOPTER_SPEED) * (SPEED_INDICATOR_WIDTH / 2)) + (SPEED_INDICATOR_HEIGHT / 2 - 750.0 / 4 - 55);
            speedCircle.setCenterY(speedCircleY);
        }
    }

    public void update(double ds, double speedDamp, double left, double right, double up, double down, Helipad h) {
        if (rotorOn) {
            rotor.getTransforms().add(new Rotate(5));
        }
        if (state == State.Raddy) {
            double oldX = this.position.getX();
            double oldY = this.position.getY();

            double newX = oldX + ds * this.speed * this.direction.getX();
            double newY = oldY + ds * this.speed * this.direction.getY();

            this.position.setX(newX);
            this.position.setY(newY);

            if (this.isWallHit(left, right, up, down)) {
                this.speed = 0;
                speedCircleY = ((-speed / MAX_HELICOPTER_SPEED) * (SPEED_INDICATOR_WIDTH / 2)) + (SPEED_INDICATOR_HEIGHT / 2 - 750.0 / 4 - 55);
                speedCircle.setCenterY(speedCircleY);
                this.position.setX(oldX);
                this.position.setY(oldY);
            } else {
                if (this.speed != 0) {
                    this.speed *= speedDamp;
                    speedCircleY = ((-speed / MAX_HELICOPTER_SPEED) * (SPEED_INDICATOR_WIDTH / 2)) + (SPEED_INDICATOR_HEIGHT / 2 - 750.0 / 4 - 55);
                    speedCircle.setCenterY(speedCircleY);

                    this.fuelLevel -= this.FUEL_LEVEL_STEP * Math.abs(this.speed);
                    double angle = 180.0 - ((fuelLevel / 100.0) * -180.0);
                    double length = Math.sqrt(Math.pow(this.needle.getEndX() - this.needle.getStartX(), 2)
                            + Math.pow(this.needle.getEndY() - this.needle.getStartY(), 2));
                    double endX = this.needle.getStartX() + length * Math.cos(Math.toRadians(angle));
                    double endY = this.needle.getStartY() + length * Math.sin(Math.toRadians(angle));
                    this.needle.setEndX(endX);
                    this.needle.setEndY(endY);
                    if (fuelLevel <= 0)
                        state = State.FlyDown;
                }
            }
        } else if (state == State.FlyUpp) {
            flyUppAnimation.play();
            rotorOn = true;
        } else if (state == State.FlyDown) {
            flyDownAnimaiton.play();
        } else if (state == State.Stopped) {
            if (h.handleCollision(this.getBoundsInParent())) {
                this.fuelLevel += this.FUEL_LEVEL_STEP * (2 * MAX_HELICOPTER_SPEED / 3);
                if (this.fuelLevel > 100.0)
                    this.fuelLevel = 100.0;
                double angle = 180.0 - ((fuelLevel / 100.0) * -180.0);
                double length = Math.sqrt(Math.pow(this.needle.getEndX() - this.needle.getStartX(), 2)
                        + Math.pow(this.needle.getEndY() - this.needle.getStartY(), 2));
                double endX = this.needle.getStartX() + length * Math.cos(Math.toRadians(angle));
                double endY = this.needle.getStartY() + length * Math.sin(Math.toRadians(angle));
                this.needle.setEndX(endX);
                this.needle.setEndY(endY);
            }
        }
    }
}
