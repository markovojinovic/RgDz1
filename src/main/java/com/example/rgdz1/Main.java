package com.example.rgdz1;

import com.example.rgdz1.objects.Helipad;
import com.example.rgdz1.objects.Obstacle;
import com.example.rgdz1.objects.Package;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Main extends Application {
    private static final double WINDOW_WIDTH = 750;
    private static final double WINDOW_HEIGHT = 750;

    private static final double HELICOPTER_WIDTH = 0.03 * Main.WINDOW_WIDTH;
    private static final double HELICOPTER_HEIGHT = 0.07 * Main.WINDOW_HEIGHT;
    private static final double HELICOPTER_SPEED_STEP = 5;
    private static final double HELICOPTER_DIRECTION_STEP = 5;
    private static final double HELICOPTER_DAMP = 0.995;

    private static final double HELIPAD_WIDTH = 0.1 * Main.WINDOW_WIDTH;
    private static final double HELIPAD_HEIGHT = 0.1 * Main.WINDOW_HEIGHT;

    private static final double PACKAGE_WIDTH = 0.02 * Main.WINDOW_WIDTH;
    private static final double PACKAGE_HEIGHT = 0.02 * Main.WINDOW_HEIGHT;

    private Instant startTime;
    private Label timerLabel;
    boolean pokrenuto = false;
    TimerAnimationTimer timerAnimationTimer;

    @Override
    public void start(Stage stage) throws IOException {
        timerLabel = new Label("00:00");
        Group root = new Group();

        Helicopter helicopter = new Helicopter(Main.HELICOPTER_WIDTH, Main.HELICOPTER_HEIGHT, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        Helipad helipad = new Helipad(Main.HELIPAD_WIDTH, Main.HELIPAD_HEIGHT);
        helipad.getTransforms().addAll(
                new Translate(-Main.HELIPAD_WIDTH / 2, -Main.HELIPAD_HEIGHT / 2)
        );

        Translate package0Position = new Translate(
                -Main.PACKAGE_WIDTH / 2 + Main.WINDOW_WIDTH / 3,
                -Main.PACKAGE_HEIGHT / 2 - Main.WINDOW_HEIGHT / 3
        );
        Translate package1Position = new Translate(
                -Main.PACKAGE_WIDTH / 2 - Main.WINDOW_WIDTH / 3,
                -Main.PACKAGE_HEIGHT / 2 - Main.WINDOW_HEIGHT / 3
        );
        Translate package2Position = new Translate(
                -Main.PACKAGE_WIDTH / 2 + Main.WINDOW_WIDTH / 3,
                Main.PACKAGE_HEIGHT / 2 + Main.WINDOW_HEIGHT / 3
        );
        Translate package3Position = new Translate(
                -Main.PACKAGE_WIDTH / 2 - Main.WINDOW_WIDTH / 3,
                Main.PACKAGE_HEIGHT / 2 + Main.WINDOW_HEIGHT / 3
        );
        Package packages[] = {
                new Package(Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package0Position),
                new Package(Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package1Position),
                new Package(Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package2Position),
                new Package(Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package3Position)
        };

        Obstacle obstacles[] = {
                new Obstacle(150, 50, new Translate(-200, 250)),
                new Obstacle(120, 60, new Translate(200, -20)),
                new Obstacle(50, 100, new Translate(-100, -30))
        };

        root.getChildren().addAll(helipad, helicopter, helicopter.speedCircle, helicopter.speedIndicator, helicopter.heightIndicatorUp, helicopter.heightIndicatorDown, helicopter.scale, helicopter.val, helicopter.needle);
        helicopter.speedCircle.toFront();
        helicopter.heightIndicatorUp.toFront();
        root.getChildren().addAll(packages);
        root.getChildren().addAll(obstacles);
        root.getChildren().addAll(new StackPane(timerLabel));
        timerLabel.setTranslateY(Main.WINDOW_HEIGHT / 2 - 50);
        timerLabel.setTranslateX(-20);
        timerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 20px;");

        ImageView img = new ImageView(new Image("file:grass.png"));
        img.setPreserveRatio(true);
        img.setFitHeight(WINDOW_HEIGHT);
        img.setFitWidth(WINDOW_WIDTH);
        root.getChildren().addAll(img);
        img.toBack();
        img.setTranslateX(-WINDOW_WIDTH / 2);
        img.setTranslateY(-WINDOW_HEIGHT / 2);

        root.getTransforms().addAll(
                new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT / 2)
        );
        Scene scene = new Scene(root, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                helicopter.startStop();
                if (!pokrenuto) {
                    pokrenuto = true;
                    startTime = Instant.now();
                    timerAnimationTimer = new TimerAnimationTimer();
                    timerAnimationTimer.start();
                }
            }
            if (event.getCode().equals(KeyCode.UP)) {
                helicopter.changeSpeed(Main.HELICOPTER_SPEED_STEP);
            } else if (event.getCode().equals(KeyCode.DOWN)) {
                helicopter.changeSpeed(-Main.HELICOPTER_SPEED_STEP);
            }

            if (event.getCode().equals(KeyCode.LEFT)) {
                helicopter.rotate(
                        -Main.HELICOPTER_DIRECTION_STEP,
                        0,
                        Main.WINDOW_WIDTH,
                        0,
                        Main.WINDOW_HEIGHT
                );
            } else if (event.getCode().equals(KeyCode.RIGHT)) {
                helicopter.rotate(
                        Main.HELICOPTER_DIRECTION_STEP,
                        0,
                        Main.WINDOW_WIDTH,
                        0,
                        Main.WINDOW_HEIGHT
                );
            }
        });

        MyTimer.IUpdatable helicopterWrapper = ds -> {
            helicopter.update(
                    ds,
                    Main.HELICOPTER_DAMP,
                    0,
                    Main.WINDOW_WIDTH,
                    0,
                    Main.WINDOW_HEIGHT,
                    helipad
            );

            for (int i = 0; i < packages.length; ++i) {
                if (packages[i] != null && packages[i].handleCollision(helicopter.getBoundsInParent())) {
                    root.getChildren().remove(packages[i]);

                    packages[i] = null;
                }
            }

            for (int i = 0; i < obstacles.length; i++) {
                if (obstacles[i].handleCollision(helicopter.getBoundsInParent())) {
                    helicopter.stop();
                }
            }
        };

        MyTimer myTimer = new MyTimer(helicopterWrapper);
        myTimer.start();

        stage.setTitle("Helicopter");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private class TimerAnimationTimer extends AnimationTimer {

        @Override
        public void handle(long now) {
            Duration elapsedTime = Duration.between(startTime, Instant.now());

            long minutes = elapsedTime.toMinutes() % 60;
            long seconds = elapsedTime.getSeconds() % 60;

            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            timerLabel.setText(formattedTime);
        }
    }
}
