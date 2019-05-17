package de.bredex.demo.iot.snake;

import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The positional system for the game. This grid will be rendered in the Canvas.
 */
public class Matrix {
    private static final Logger LOGGER = LoggerFactory.getLogger(Matrix.class);

    /**
     * The number of columns
     */
    private int columns;
    /**
     * The number of rows
     */
    private int rows;

    private List<Snake> snakes = new ArrayList<>();
    // private Cell food;
    private List<Cell> foods = new ArrayList<>();

    public Matrix(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }

    /**
     * Add snake at center of matrix.
     *
     * @param color the snake's color
     */
    void createSnake(Color color, Cell cell) {
        Snake snake = new Snake(this, color, cell);
        this.snakes.add(snake);
        LOGGER.info("createSnake: '{}'", this);
    }

    /**
     * Add food on some empty cell in the matrix.
     */
    void createFood() {
        int maxFoods = (int) (Math.random() * 12);
        for (int i = 0; i < maxFoods; i++) {
            this.foods.add(getRandomPoint());
        }
        LOGGER.info("createFood: '{}'", this);
    }

    /**
     * Create a {@link Cell} inside this {@link Matrix}'s dimensions.
     *
     * @param cell the {@link Cell} to use as data source
     * @return a {@link Cell} inside this {@link Matrix}'s dimensions.
     */
    public Cell wrap(Cell cell) {
        int column = cell.getColumn() % columns;
        while (column < 0) column += columns;
        int row = cell.getRow() % rows;
        while (row < 0) row += rows;
        return new Cell(column, row);
    }

    public Cell getRandomPoint() {
        Random random = new Random();
        Cell ret;

        Set<Cell> usedCells = new HashSet<>();
        usedCells.addAll(foods);

        for (Snake snake : snakes) {
            usedCells.addAll(snake.getCells());
        }

        do {
            ret = new Cell(random.nextInt(rows), random.nextInt(columns));
        } while (usedCells.contains(ret));

        return ret;
    }

    /**
     * This method is called in every cycle of execution.
     */
    public void update() {
        boolean foodEaten = false;

        // Let each snake eat or move
        // this way food can get eaten multiple times
        for (Snake snake : snakes) {
            for (Cell food : foods) {
                if (food.equals(snake.getHead())) {
                    snake.eatFoodAndGrow();
                    foods.remove(food);
                    foodEaten = true;
                    break;
                }
            }
            if (!foodEaten) {
                snake.moveIntoCurrentDirection();
            }
        }

        if (foodEaten && foods.isEmpty()) {
            // create new food
            createFood();
        }
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

//    public Cell getFood() {
//        return food;
//    }

    @Override
    public String toString() {
        return "Matrix{" +
                "columns=" + columns +
                ", rows=" + rows +
                ", snakes=" + snakes +
                //", food=" + food +
                '}';
    }

    public boolean checkCollided(Snake snake, Cell cell) {
        boolean ret = false;

        for (Snake otherSnake : getSnakes()) {
            if (!snake.equals(otherSnake) && otherSnake.getCells().contains(cell)) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    public Iterable<Cell> getFoods() {
        return Collections.unmodifiableList(foods);
    }
}
