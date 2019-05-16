package de.bredex.demo.iot.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates FX stuff and starts the logic.
 */
public class SnakeFX extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnakeFX.class);

    private SnakeController snakeController;
    private SnakeMqttController snakeMqttController;

    private GraphicsContext context;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Always close on exit
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        StackPane root = new StackPane();
        Canvas canvas = new Canvas(Painter.WIDTH, Painter.HEIGHT);
        context = canvas.getGraphicsContext2D();

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPress);

        root.getChildren().add(canvas);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.show();

        setupSnakeController();
        setupMqttController();
    }

    private void setupSnakeController() {
        LOGGER.info("setupSnakeController: init snakeController");

        snakeController = new SnakeController(context);
        if (snakeMqttController != null) {
            snakeMqttController.setSnakeController(snakeController);
        }

        Thread logicThread = new Thread(snakeController);
        logicThread.setName(snakeController.getClass().getName());
        logicThread.start();
    }

    protected void setupMqttController() {
        LOGGER.info("setupMqttController: init snakeController");
        snakeMqttController = new SnakeMqttController(this, snakeController);

        Thread mqttThread = new Thread(snakeMqttController);
        mqttThread.setName(snakeMqttController.getClass().getName());
        mqttThread.start();
    }

    private void handleKeyPress(KeyEvent e) {
        LOGGER.debug("handleKeyPress: '{}'", e);

        KeyCode keyCode = e.getCode();
        LOGGER.debug("handleKeyPress: keyCode '{}'", e);

        if (KeyCode.ENTER.equals(keyCode)) {
            reset();
        } else {
            snakeController.handleKeyPress(e);
            if (snakeController.shouldRestart()) {
                reset();
            }
        }
    }

    public void reset() {
        LOGGER.info("Reset Application");
        snakeController.stop();
        setupSnakeController();
    }
}
