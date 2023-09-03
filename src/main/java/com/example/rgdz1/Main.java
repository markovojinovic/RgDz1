package com.example.rgdz1;

import com.example.rgdz1.objects.Helipad;
import com.example.rgdz1.objects.Obstacle;
import com.example.rgdz1.objects.Package;
import com.example.rgdz1.objects.Terrain;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
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

    private static final double TRIANGLE_SIZE = 20;
    private static final double TRIANGLE_HEIGHT = Math.sqrt(3) * TRIANGLE_SIZE / 2;

    private Instant startTime;
    private Label timerLabel;
    private Label gameOver;
    private Label levelFinished;
    boolean started = false;
    int packageCnt = 0;
    TimerAnimationTimer timerAnimationTimer;
    Color[] helicopterVariants;
    int[] helicopterSpeeds;
    Helicopter chooseHelicopter;
    Helicopter gameHelicopter;
    int chopperIter = 0;
    int terrainIter = 0;
    ImageView[] terrains;
    Group mainRoot = new Group();

    MediaPlayer paketSound;
    MediaPlayer flyUpSound;

    Terrain terrain;
    Package[] packages;
    Obstacle[] obstacles;
    Helipad[] helipads;

    @Override
    public void start(Stage stage) throws IOException {

        timerLabel = new Label("00:00");
        gameOver = new Label("GAME OVER");
        levelFinished = new Label("LEVEL FINISHED");

        MyTimer.IUpdatable helicopterWrapper = ds -> {
            gameHelicopter.update(
                    ds,
                    Main.HELICOPTER_DAMP,
                    0,
                    Main.WINDOW_WIDTH,
                    0,
                    Main.WINDOW_HEIGHT,
                    helipads
            );

            for (int i = 0; i < packages.length; ++i) {
                if (packages[i] != null && packages[i].handleCollision(gameHelicopter.getBoundsInParent())) {
                    terrain.getChildren().remove(packages[i]);

                    flyUpSound.stop();
                    paketSound.stop();
                    paketSound.play();

                    packages[i] = null;
                    packageCnt++;
                }
            }
            if (packageCnt == packages.length) {
                mainRoot.getChildren().add(new StackPane(levelFinished));
                timerAnimationTimer.stop();
                gameHelicopter.endGame();
            }

            if (gameHelicopter.lostFuel) {
                PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(0.5));
                delay.setOnFinished(event1 -> {
                    mainRoot.getChildren().add(new StackPane(gameOver));
                    timerAnimationTimer.stop();
                    gameHelicopter.endGame();
                });
                delay.playFromStart();
            }

            for (int i = 0; i < obstacles.length; i++) {
                if (obstacles[i].handleCollision(gameHelicopter.getBoundsInParent())) {
                    gameHelicopter.stop();
                }
            }
        };

        MyTimer myTimer = new MyTimer(helicopterWrapper);
        Group choseRoot = new Group();

        File music = new File("refren_paket.mp3");
        Media sound = new Media(music.toURI().toString());
        this.paketSound = new MediaPlayer(sound);

        File music1 = new File("refren-poletanje.mp3");
        Media sound1 = new Media(music1.toURI().toString());
        this.flyUpSound = new MediaPlayer(sound1);


        Button chopperLeftButton = createTriangleButton("Left", false);
        Button chopperRightButton = createTriangleButton("Right", true);
        Button terrainLeftButton = createTriangleButton("Left", false);
        Button terrainRightButton = createTriangleButton("Right", true);
        Button startGame = new Button("Start game");


        Scene mainScene = new Scene(mainRoot, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        Scene choseScene = new Scene(choseRoot, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        terrains = new ImageView[3];
        terrains[0] = new ImageView(new Image("file:assets/1.png"));
        terrains[0].setFitHeight(300);
        terrains[0].setFitWidth(300);
        terrains[1] = new ImageView(new Image("file:assets/2.png"));
        terrains[1].setFitHeight(300);
        terrains[1].setFitWidth(300);
        terrains[2] = new ImageView(new Image("file:assets/3.png"));
        terrains[2].setFitHeight(300);
        terrains[2].setFitWidth(300);

        choseRoot.getChildren().addAll(terrains[0], terrainLeftButton, terrainRightButton);
        choseRoot.getChildren().add(startGame);
        startGame.setTranslateX(WINDOW_WIDTH / 2 - startGame.getMaxWidth());
        startGame.setTranslateY(WINDOW_HEIGHT - startGame.getMaxHeight() - 50);
        terrains[0].setTranslateY(420);
        terrainRightButton.setTranslateX(WINDOW_WIDTH - 100 - 3 * TRIANGLE_HEIGHT);
        terrainRightButton.setTranslateY(450);
        terrainLeftButton.setTranslateX(100);
        terrainLeftButton.setTranslateY(450);
        terrains[0].setTranslateY(320);
        terrains[0].setTranslateX(240);

        helicopterVariants = new Color[3];
        helicopterVariants[0] = Color.GREEN;
        helicopterVariants[1] = Color.RED;
        helicopterVariants[2] = Color.BLUE;
        helicopterSpeeds = new int[3];
        helicopterSpeeds[0] = 140;
        helicopterSpeeds[1] = 85;
        helicopterSpeeds[2] = 100;

        chooseHelicopter = new Helicopter(Main.HELICOPTER_WIDTH, Main.HELICOPTER_HEIGHT, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, helicopterVariants[0], null);
        choseRoot.getChildren().addAll(chopperLeftButton, chopperRightButton, chooseHelicopter);
        chopperRightButton.setTranslateX(WINDOW_WIDTH - 100 - 3 * TRIANGLE_HEIGHT);
        chopperRightButton.setTranslateY(150);
        chopperLeftButton.setTranslateX(100);
        chopperLeftButton.setTranslateY(150);

        chooseHelicopter.setTranslateX(370);
        chooseHelicopter.setTranslateY(150);
        chooseHelicopter.setRotate(-90);
        chooseHelicopter.setScaleX(2);
        chooseHelicopter.setScaleY(2);

        chopperLeftButton.setOnAction(event -> {
            choseRoot.getChildren().remove(chooseHelicopter);
            chopperIter--;

            if (chopperIter < 0)
                chopperIter = 2;

            chooseHelicopter = new Helicopter(Main.HELICOPTER_WIDTH, Main.HELICOPTER_HEIGHT, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, helicopterVariants[chopperIter], null);
            choseRoot.getChildren().add(chooseHelicopter);
            chooseHelicopter.setTranslateX(370);
            chooseHelicopter.setTranslateY(150);
            chooseHelicopter.setRotate(-90);
            chooseHelicopter.setScaleX(2);
            chooseHelicopter.setScaleY(2);
        });

        chopperRightButton.setOnAction(event -> {
            choseRoot.getChildren().remove(chooseHelicopter);
            chopperIter++;

            if (chopperIter > 2)
                chopperIter = 0;
            chooseHelicopter = new Helicopter(Main.HELICOPTER_WIDTH, Main.HELICOPTER_HEIGHT, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, helicopterVariants[chopperIter], null);
            choseRoot.getChildren().add(chooseHelicopter);
            chooseHelicopter.setTranslateX(370);

            chooseHelicopter.setTranslateY(150);
            chooseHelicopter.setRotate(-90);
            chooseHelicopter.setScaleX(2);
            chooseHelicopter.setScaleY(2);
        });

        terrainLeftButton.setOnAction(event -> {
            choseRoot.getChildren().remove(terrains[terrainIter]);
            terrainIter--;

            if (terrainIter < 0)
                terrainIter = 2;

            choseRoot.getChildren().add(terrains[terrainIter]);
            terrains[terrainIter].setTranslateY(320);
            terrains[terrainIter].setTranslateX(240);
        });

        terrainRightButton.setOnAction(event -> {
            choseRoot.getChildren().remove(terrains[terrainIter]);
            terrainIter++;

            if (terrainIter > 2)
                terrainIter = 0;

            choseRoot.getChildren().add(terrains[terrainIter]);
            terrains[terrainIter].setTranslateY(320);
            terrains[terrainIter].setTranslateX(240);
        });

        startGame.setOnAction(event -> {
            terrain = new Terrain(WINDOW_HEIGHT, terrainIter + 1);
            mainRoot.getChildren().add(terrain);

            gameHelicopter = new Helicopter(HELICOPTER_WIDTH, HELICOPTER_HEIGHT, WINDOW_WIDTH, WINDOW_HEIGHT, helicopterVariants[chopperIter], terrain.getBigObstacleBoundInLocal());
            gameHelicopter.setMAX_HELICOPTER_SPEED(helicopterSpeeds[chopperIter]);
            terrain.getChildren().addAll(gameHelicopter, gameHelicopter.heightIndicatorUp,
                    gameHelicopter.heightIndicatorDown, gameHelicopter.speedIndicator, gameHelicopter.speedCircle,
                    gameHelicopter.scale, gameHelicopter.val, gameHelicopter.needle);

            gameHelicopter.setTranslateX(terrain.getHelicoptersStart().getX());
            gameHelicopter.setTranslateY(terrain.getHelicoptersStart().getY());
            packages = terrain.getPackages();
            obstacles = terrain.getObstacles();
            helipads = terrain.getHelipads();

            mainRoot.getChildren().addAll(new StackPane(timerLabel));
            timerLabel.setTranslateY(Main.WINDOW_HEIGHT / 2 - 50);
            timerLabel.setTranslateX(-40);
            timerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 40px; -fx-opacity: 0.5;");

            gameOver.setTranslateX(-270);
            gameOver.setTranslateY(-90);
            gameOver.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 90px; -fx-opacity: 0.9;");

            levelFinished.setTranslateX(-335);
            levelFinished.setTranslateY(-90);
            levelFinished.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 90px; -fx-opacity: 0.9;");

            myTimer.start();
            stage.setScene(mainScene);
        });

        mainRoot.getTransforms().addAll(
                new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT / 2)
        );

        mainScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                if (!started) {
                    started = true;
                    startTime = Instant.now();
                    timerAnimationTimer = new TimerAnimationTimer();
                    timerAnimationTimer.start();
                    gameHelicopter.startStop();
                    flyUpSound.stop();
                    flyUpSound.play();
                } else {
                    Bounds bigObstacleBounds = terrain.getBigObstacleBoundInLocal();
                    if (bigObstacleBounds != null)
                        if (!Helicopter.checkIntersection(gameHelicopter.getBoundsInParent(), bigObstacleBounds))
                            gameHelicopter.startStop();
                }
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameHelicopter.changeSpeed(Main.HELICOPTER_SPEED_STEP);
            } else if (event.getCode().equals(KeyCode.DOWN)) {
                gameHelicopter.changeSpeed(-Main.HELICOPTER_SPEED_STEP);
            }

            if (event.getCode().equals(KeyCode.LEFT)) {
                gameHelicopter.rotate(
                        -Main.HELICOPTER_DIRECTION_STEP,
                        0,
                        Main.WINDOW_WIDTH,
                        0,
                        Main.WINDOW_HEIGHT
                );
            } else if (event.getCode().equals(KeyCode.RIGHT)) {
                gameHelicopter.rotate(
                        Main.HELICOPTER_DIRECTION_STEP,
                        0,
                        Main.WINDOW_WIDTH,
                        0,
                        Main.WINDOW_HEIGHT
                );
            }
        });

        stage.setTitle("Helicopter");
        stage.setScene(choseScene);

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

    private Button createTriangleButton(String text, boolean rightDirection) {
        Button button = new Button(text);

        Polygon triangle = new Polygon();
        if (rightDirection) {
            triangle.getPoints().addAll(
                    0.0, 0.0,
                    TRIANGLE_SIZE, TRIANGLE_HEIGHT / 2,
                    0.0, TRIANGLE_HEIGHT
            );
        } else {
            triangle.getPoints().addAll(
                    TRIANGLE_SIZE, 0.0,
                    0.0, TRIANGLE_HEIGHT / 2,
                    TRIANGLE_SIZE, TRIANGLE_HEIGHT
            );
        }
        button.setGraphic(triangle);

        return button;
    }
}
