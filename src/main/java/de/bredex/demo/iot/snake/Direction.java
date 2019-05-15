package de.bredex.demo.iot.snake;

public enum Direction {
    NONE(0, 0),
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    int moveX;
    int moveY;

    Direction(int moveX, int moveY) {
        this.moveX = moveX;
        this.moveY = moveY;
    }

    Direction turnRight() {
        Direction ret;
        switch (this) {
            case NONE:
                ret = NONE;
                break;
            case NORTH:
                ret = EAST;
                break;
            case EAST:
                ret = SOUTH;
                break;
            case SOUTH:
                ret = WEST;
                break;
            case WEST:
                ret = NORTH;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return ret;
    }

    Direction turnLeft() {
        Direction ret;
        switch (this) {
            case NONE:
                ret = NONE;
                break;
            case NORTH:
                ret = WEST;
                break;
            case WEST:
                ret = SOUTH;
                break;
            case SOUTH:
                ret = EAST;
                break;
            case EAST:
                ret = NORTH;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return ret;
    }

}
