package ru.eninja.gameoflife;

/**
 * ??????
 */
public class Cell {
    private int x;              // ?????????? ????? x
    private int y;              // ?????????? ????? y
    public  int neighbCount;    // ?????????? ??????? ??????

    /* Getters */

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /* Setters */

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * @param x ?????????? ?????? x
     * @param y ?????????? ?????? y
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Cell)) return false;

        Cell cell = (Cell) o;

        if (x != cell.x) return false;
        return y == cell.y;

    }

    @Override
    public int hashCode() {
        return 31 * y + x;
    }


}
