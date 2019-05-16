package de.bredex.demo.iot.snake;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
    //private static final int CELL_SIZE = 75;
    private static final Color CELL_COLOR = Color.WHITESMOKE;
    private static final Color FOOD_COLOR = Color.LIGHTBLUE;
    private static final Color SNAKE_DEAD = Color.RED;

    private static final Color MESSAGE_COLOR = Color.GREEN;
    private static final String MESSAGE_RESET =
            "<Return> drücken für noch eine Runde...";

    public static void paint(Matrix matrix, GraphicsContext context) {
        final int CELL_WIDTH = WIDTH / matrix.getColumns();
        final int CELL_HEIGHT = HEIGHT / matrix.getRows();

        context.setFill(CELL_COLOR);
        context.fillRect(0, 0,
                matrix.getColumns() * CELL_WIDTH, matrix.getColumns() * CELL_HEIGHT);

        // Now the Food
        context.setFill(FOOD_COLOR);
        paintCell(matrix.getFood(), CELL_WIDTH, CELL_HEIGHT, context);

        // Now the snakes
        for (Snake snake : matrix.getSnakes()) {
            context.setFill(snake.getColor());
            snake.getCells().forEach(cell -> paintCell(cell, CELL_WIDTH, CELL_HEIGHT, context));
            if (snake.hasCollided()) {
                context.setFill(SNAKE_DEAD);
                paintCell(snake.getHead(), CELL_WIDTH, CELL_HEIGHT, context);
            }
        }
    }

    private static void paintCell(Cell cell, int CELL_WIDTH, int CELL_HEIGHT, GraphicsContext gc) {
        gc.fillRect(cell.getColumn() * CELL_WIDTH, cell.getRow() * CELL_HEIGHT,
                CELL_WIDTH, CELL_HEIGHT);
    }

    public static void paintResetMessage(GraphicsContext gc) {
        gc.setFill(MESSAGE_COLOR);
        gc.setFont(new Font("Maven Pro", 36));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(MESSAGE_RESET, WIDTH / 2, HEIGHT / 2);
    }

}