package de.bredex.demo.iot.snake;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * A snake lives inside the {@link Matrix}.
 */
public class Snake {
    private static final Logger LOGGER = LoggerFactory.getLogger(Snake.class);

    private Matrix matrix;
    private Color color;

    private Direction direction;
    private List<Cell> cells;

    private boolean collided;

    /**
     * Create and init the snake.
     */
    public Snake(Matrix matrix, Color color, Cell startCell) {
        this.matrix = matrix;
        this.color = color;

        this.direction = Direction.NORTH;
        cells = new LinkedList<>();
        cells.add(startCell);

        collided = false;
    }

    /**
     * Move head wrapping on matrix bounds and check for food or snake
     * collision.
     *
     * @param cell new cell for the head.
     */
    private void checkedMove(Cell cell) {
        if (cells.contains(cell)) {
            throw new IllegalArgumentException("Cell already in snake! " + cell + ":" + this);
        }

        cell = matrix.wrap(cell);
        collided = collided || matrix.checkCollided(this, cell);
        cells.add(cell);
    }

    /**
     * Grow snake by moving the head and not removing the tail.
     *
     * @param cell new cell for the head.
     */
    private void growTo(Cell cell) {
        checkedMove(cell);
    }

    /**
     * Move snake by moving the head and removing the tail.
     *
     * @param cell new cell for the head.
     */
    private void moveTo(Cell cell) {
        // moveIntoCurrentDirection head
        checkedMove(cell);
        // remove tail
        cells.remove(0);
    }

    // public

    /**
     * Make the snake grow into current {@link #direction}
     */
    public void eatFoodAndGrow() {
        growTo(getHead().translate(direction));
    }

    /**
     * Move head into current {@link #direction}.
     */
    public void moveIntoCurrentDirection() {
        moveTo(getHead().translate(direction));
    }

    public void turnNorth() {
        if (!cells.isEmpty() && Direction.SOUTH.equals(direction)) return;
        direction = Direction.NORTH;
    }

    public void turnSouth() {
        if (!cells.isEmpty() && Direction.NORTH.equals(direction)) return;
        direction = Direction.SOUTH;
    }

    public void turnWest() {
        if (!cells.isEmpty() && Direction.EAST.equals(direction)) return;
        direction = Direction.WEST;
    }

    public void turnEast() {
        if (!cells.isEmpty() && Direction.WEST.equals(direction)) return;
        direction = Direction.EAST;
    }

    // Getter and Setter

    public List<Cell> getCells() {
        return cells;
    }

    public Paint getColor() {
        return color;
    }

    /**
     * @return {@code true} if the Snake has {@link Matrix#checkCollided(Snake, Cell)}
     */
    public boolean hasCollided() {
        return collided;
    }

    /**
     * @return The head of the Snake.
     */
    public Cell getHead() {
        return cells.get(0);
    }

    @Override
    public String toString() {
        return "Snake{" +
                "color=" + color +
                ", head=" + getHead() +
                ", collided=" + collided +
                ", cells=" + cells +
                ", direction=" + direction +
                '}';
    }

}