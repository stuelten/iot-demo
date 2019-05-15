package de.bredex.demo.iot.snake;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Paints the {@link Matrix} and it's {@link Snake}s.
 */
public class Painter {

    /**
     * Canvas dimensions
     */
    static final int WIDTH = 750;
    static final int HEIGHT = 750;
    static final Color SNAKE_COLOR_1 = Color.YELLOW;
    static final Color SNAKE_COLOR_2 = Color.GREENYELLOW;
    /**
     * The side length of each square point in the grid.
     */
    private static final int CELL_SIZE = 75;
    private static final Color CELL_COLOR = Color.WHITESMOKE;
    private static final Color FOOD_COLOR = Color.LIGHTBLUE;
    private static final Color SNAKE_DEAD = Color.RED;

    private static final Color MESSAGE_COLOR = Color.GREEN;
    private static final String MESSAGE_RESET = "Hit RETURN to reset.";

    public static void paint(Matrix matrix, GraphicsContext context) {
        context.setFill(CELL_COLOR);
        context.fillRect(0, 0,
                matrix.getColumns() * CELL_SIZE, matrix.getColumns() * CELL_SIZE);

        // Now the Food
        context.setFill(FOOD_COLOR);
        paintCell(matrix.getFood(), context);

        // Now the snakes
        for (Snake snake : matrix.getSnakes()) {
            context.setFill(snake.getColor());
            snake.getCells().forEach(cell -> paintCell(cell, context));
            if (snake.hasCollided()) {
                context.setFill(SNAKE_DEAD);
                paintCell(snake.getHead(), context);
            }
        }
    }

    private static void paintCell(Cell cell, GraphicsContext gc) {
        gc.fillRect(cell.getColumn() * CELL_SIZE, cell.getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    public static void paintResetMessage(GraphicsContext gc) {
        gc.setFill(MESSAGE_COLOR);
        gc.fillText(MESSAGE_RESET, 10, 10);
    }

}