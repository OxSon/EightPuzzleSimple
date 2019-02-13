package eightPuzzle;

import javax.swing.*;

/**
 * @author Alec Mills
 * <p>
 * CS 1410 Assignment 09
 * Entry-point for the application
 */
public class Main {
    public static void main(String[] args) {
        JFrame game = new FifteenGUI();
        game.setVisible(true);
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}