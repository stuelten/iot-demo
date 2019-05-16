package de.bredex.demo.iot.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    private Controller controller;
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

        reset();
    }

    private void reset() {
        LOGGER.info("reset: init controller");

        controller = new Controller(context);

        Thread logicThread = new Thread(controller);
        logicThread.setName(controller.getClass().getName());
        logicThread.start();
    }

    private void handleKeyPress(KeyEvent e) {
        LOGGER.debug("handleKeyPress: '{}'", e);

        controller.handleKeyPress(e);

        if (controller.shouldRestart()) {
            controller.stop();
            reset();
        }
    }

}
