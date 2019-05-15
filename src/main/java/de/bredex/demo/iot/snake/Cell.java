package de.bredex.demo.iot.snake;

import java.util.Objects;

/**
 * A cell inside a {@link Matrix}.
 */
public class Cell {
    private final int column;
    private final int row;

    Cell(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    /**
     * @param direction the direction in which to move
     * @return A new {@link Cell}
     */
    public Cell translate(Direction direction) {
        return new Cell(column + direction.moveX, row + direction.moveY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return column == cell.column &&
                row == cell.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "column=" + column +
                ", row=" + row +
                '}';
    }

}