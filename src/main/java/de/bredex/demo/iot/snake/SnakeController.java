package de.bredex.demo.iot.snake;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls the matrix's content.
 */
public class SnakeController implements Runnable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SnakeController.class);

    private static final int COLUMNS = 20;
    private static final int ROWS = 20;

    private Matrix matrix;
    private GraphicsContext context;
    private long frameInterval;

    private boolean running;
    private boolean restart;

    private boolean busy;

    // my snakes are package-private
    Snake snake0;
    Snake snake1;

    public SnakeController(GraphicsContext context) {
        Matrix matrix = new Matrix(COLUMNS, ROWS);
        this.matrix = matrix;
        this.context = context;
        frameInterval = 200;
        restart = false;
        busy = false;

        // create 2 snakes
        Cell cell1 = new Cell((int) (COLUMNS * 0.33), (int) (ROWS * 0.33));
        matrix.createSnake(Painter.SNAKE_COLOR_1, cell1);
        Cell cell2 = new Cell((int) (COLUMNS * 0.66), (int) (ROWS * 0.50));
        matrix.createSnake(Painter.SNAKE_COLOR_2, cell2);

        snake0 = matrix.getSnakes().get(0);
        snake1 = matrix.getSnakes().get(1);

        matrix.createFood();
    }

    @Override
    public void run() {
        running = true;
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

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public boolean shouldRestart() {
        return restart;
    }

    public void handleKeyPress(KeyEvent e) {
        // Only one key press per frame
        if (isBusy()) {
            return;
        }
        setBusy();

        KeyCode keyCode = e.getCode();
        LOGGER.debug("handleKeyPress: keyCode '{}'", e);

        switch (keyCode) {
            case UP:
                LOGGER.debug("handleKeyPress: turnEast");
                snake0.turnNorth();
                break;
            case RIGHT:
                LOGGER.debug("handleKeyPress: turnEast");
                snake0.turnEast();
                break;
            case DOWN:
                LOGGER.debug("handleKeyPress: turnSouth");
                snake0.turnSouth();
                break;
            case LEFT:
                LOGGER.debug("handleKeyPress: turnWest");
                snake0.turnWest();
                break;


            case Y:
                LOGGER.debug("handleKeyPress: turnLeft");
                snake1.turn(snake1.getDirection().turnLeft());
                break;
            case X:
                LOGGER.debug("handleKeyPress: turnRight");
                snake1.turn(snake1.getDirection().turnRight());
                break;

            case W:
                LOGGER.debug("handleKeyPress: turnNorth");
                snake1.turnNorth();
                break;
            case D:
                LOGGER.debug("handleKeyPress: turnEast");
                snake1.turnEast();
                break;
            case S:
                LOGGER.debug("handleKeyPress: turnSouth");
                snake1.turnSouth();
                break;
            case A:
                LOGGER.debug("handleKeyPress: turnWest");
                snake1.turnWest();
                break;

            case ENTER:
                LOGGER.debug("handleKeyPress: reset");
                restart = true;
                break;
        }
    }

    protected void turnSnake(Snake snake, Direction direction) {
        LOGGER.debug("turnSnake: '{}', '{}'", snake, direction);
        snake.turn(direction);
    }

}