package de.bredex.demo.iot.snake;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls the matrix's content.
 */
public class Controller implements Runnable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Controller.class);

    private static final int COLUMNS = 10;
    private static final int ROWS = 10;

    private Matrix matrix;
    private GraphicsContext context;
    private long frameInterval;

    private boolean running;
    private boolean restart;

    private boolean busy;

    public Controller(GraphicsContext context) {
        Matrix matrix = new Matrix(COLUMNS, ROWS);
        this.matrix = matrix;
        this.context = context;
        frameInterval = 100;
        running = true;
        restart = false;
        busy = false;

        // create 2 snakes
        Cell cell1 = new Cell((int) (COLUMNS * 0.33), (int) (ROWS * 0.33));
        matrix.createSnake(Painter.SNAKE_COLOR_1, cell1);
        Cell cell2 = new Cell((int) (COLUMNS * 0.66), (int) (ROWS * 0.66));
        matrix.createSnake(Painter.SNAKE_COLOR_2, cell2);

        matrix.createFood();
    }

    @Override
    public void run() {
        while (running) {
            // when should the next frame be painted?
            long nextFrame = System.currentTimeMillis() + frameInterval;

            busy = false;
            matrix.update();
            Painter.paint(matrix, context);

            for (Snake snake : matrix.getSnakes()) {
                if (snake.hasCollided()) {
                    LOGGER.warn("Snake collided: '{}'", snake);
                    running = false;
                    Painter.paintResetMessage(context);
                    break;
                }
            }

            long sleepTime = nextFrame - System.currentTimeMillis();
            if (sleepTime > 0) {
                // wait till next frame
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    public void stop() {
        running = false;
    }

    public boolean isBusy() {
        return busy;
    }

    private void setBusy() {
        busy = true;
    }

    public boolean shouldRestart() {
        return restart;
    }

    public void handleKeyPress(KeyEvent e) {
        if (isBusy()) {
            return;
        }
        setBusy();

        KeyCode keyCode = e.getCode();
        LOGGER.info("handleKeyPress: keyCode '{}'", e);


        Snake snake0 = matrix.getSnakes().get(0);
        Snake snake1 = matrix.getSnakes().get(1);
        switch (keyCode) {
            case UP:
                LOGGER.info("handleKeyPress: turnNorth");
                snake0.turnNorth();
                break;
            case RIGHT:
                LOGGER.info("handleKeyPress: turnEast");
                snake0.turnEast();
                break;
            case DOWN:
                LOGGER.info("handleKeyPress: turnSouth");
                snake0.turnSouth();
                break;
            case LEFT:
                LOGGER.info("handleKeyPress: turnWest");
                snake0.turnWest();
                break;

            case W:
                LOGGER.info("handleKeyPress: turnNorth");
                snake1.turnNorth();
                break;
            case D:
                LOGGER.info("handleKeyPress: turnEast");
                snake1.turnEast();
                break;
            case S:
                LOGGER.info("handleKeyPress: turnSouth");
                snake1.turnSouth();
                break;
            case A:
                LOGGER.info("handleKeyPress: turnWest");
                snake1.turnWest();
                break;

            case ENTER:
                LOGGER.info("handleKeyPress: reset");
                restart = true;
        }
    }

}