package ru.eninja.gameoflife;

import java.awt.*;

/**
 * Created by eninja on 29.04.2015.
 */
public class Main {
    public static void main(String[] args) {
        Game game = new Game(1280, 720, Color.darkGray, Color.cyan);
        game.start();
    }
}
