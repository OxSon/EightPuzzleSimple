package eightPuzzle;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Alec Mills
 * <p>
 * CS 1410 Assignment 09
 * Handles GUI for fifteen puzzle
 */
public class FifteenGUI extends JFrame {
    private JLabel movesLabel;
    private JLabel infoLabel;
    private JPanel tilePanel;
    private JLabel[][] tiles;

    //game specific variables
    /**
     * the current board-state
     */
    private Board board;
    /**
     * the board-state the game began with (used for solver)
     */
    private Board initialBoard;
    /**
     * solver instance
     */
    private Solver solver;
    /**
     * a state corresponding to a completed board with all tiles in correct order
     */
    private State win;
    /**
     * number of moves user (or algorithm) has made
     */
    private int movesUsed;
    /**
     * used for saving and loading
     */
    private int savedMovesUsed;

    /**
     * constructor
     */
    public FifteenGUI() {
        initComponents();
    }

    /**
     * resets the game to an initial state (i.e. no moves made) with a new randomized board
     */
    private void newGame() {
        //make sure we get a solveable board and not frustrate our user or algorithm
        do {
            board = new Board();
            initialBoard = new Board(board);
        } while (!Solver.isSolveable(board));

        solver = new Solver(new State(board));
        win = solver.solve(solver.getQueue().peek());

        //Debugging console output
//        for (Move el : win.getMoveList()) {
//            System.out.println(el);
//        }
        movesUsed = 0;
        refreshBoard();
    }

    /**
     * resets the game state to a specified board
     *
     * @param savedBoard the board to be loaded into the initial state
     */
    private void newGame(Board savedBoard) {
        //make sure we get a solveable board and not frustrate our user or algorithm
        do {
            board = new Board(savedBoard);
            initialBoard = new Board(board);
        } while (!Solver.isSolveable(board));
        solver = new Solver(new State(board));

        win = solver.solve(solver.getQueue().peek());
        //Debugging console output
//        for (Move el : win.getMoveList()) {
//            System.out.println(el);
//        }
        refreshBoard();
    }

    /**
     * initializes components
     */
    private void initComponents() {
        //The following are generic GUI elements
        JMenuBar menuBar1 = new JMenuBar();
        JMenu menuMain = new JMenu();
        JMenuItem menuNewGame = new JMenuItem();
        JMenu menuSaveLoad = new JMenu();
        JMenuItem menuSave = new JMenuItem();
        JMenuItem menuLoad = new JMenuItem();
        JMenuItem menuSolve = new JMenuItem();
        JPanel movesPanel = new JPanel();
        movesLabel = new JLabel();
        JPanel infoPanel = new JPanel();
        infoLabel = new JLabel();
        tilePanel = new JPanel();
        board = new Board();
        JLabel tile0 = new JLabel();
        JLabel tile1 = new JLabel();
        JLabel tile2 = new JLabel();
        JLabel tile3 = new JLabel();
        JLabel tile4 = new JLabel();
        JLabel tile5 = new JLabel();
        JLabel tile6 = new JLabel();
        JLabel tile7 = new JLabel();
        JLabel tile8 = new JLabel();

        //store our grid of JLabel's in an array for easy cognition regarding their location
        tiles = new JLabel[3][3];
        tiles[0][0] = tile0;
        tiles[0][1] = tile1;
        tiles[0][2] = tile2;
        tiles[1][0] = tile3;
        tiles[1][1] = tile4;
        tiles[1][2] = tile5;
        tiles[2][0] = tile6;
        tiles[2][1] = tile7;
        tiles[2][2] = tile8;

        //JFrame properties
        setTitle("Eight-puzzle by Alec Mills");
        setMinimumSize(new Dimension(500, 500));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //set a new game
        newGame();

        //JMenuBar properties
        {
            {
                menuMain.setText("Menu");

                //menuNewGame menuItem properties
                menuNewGame.setText("New Game");
                menuNewGame.addActionListener(e -> menuNewGameClicked());
                menuMain.add(menuNewGame);

                //menuSaveLoad menuItem properties
                {
                    menuSaveLoad.setText("Save/Load");

                    //---- menuSave ----
                    menuSave.setText("Save");
                    menuSave.addActionListener(e -> menuSaveClicked());
                    menuSaveLoad.add(menuSave);

                    //---- menuLoad ----
                    menuLoad.setText("Load");
                    menuLoad.addActionListener(e -> menuLoadClicked());
                    menuSaveLoad.add(menuLoad);
                }
                menuMain.add(menuSaveLoad);

                //---- menuSolve ----
                menuSolve.setText("Solve");
                menuSolve.addActionListener(e -> menuSolveClicked());
                menuMain.add(menuSolve);
            }
            menuBar1.add(menuMain);
        }
        setJMenuBar(menuBar1);

        //movesPanel properties
        {
            movesPanel.setLayout(new FlowLayout());

            movesLabel.setHorizontalAlignment(SwingConstants.LEFT);
            movesPanel.add(movesLabel);

        }
        contentPane.add(movesPanel, BorderLayout.SOUTH);

        //infoPanel properties
        {
            infoPanel.setLayout(new FlowLayout());

            infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
            movesPanel.add(infoLabel);
        }
        contentPane.add(infoPanel, BorderLayout.NORTH);

        //tilePanel (where our tiles are located) properties
        {
            tilePanel.setLayout(new GridLayout(3, 0, 3, 3));

            //tiles
            refreshBoard();
        }
        contentPane.add(tilePanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(getOwner());


        //action listeners for all of our tiles call the same method
        for (JLabel[] row : tiles) {
            for (JLabel el : row) {
                el.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        tileClicked(e);
                    }
                });
            }
        }
    }

    /**
     * Shows the solution steps, i.e. from initial state to goal state, one at a time
     */
    private void menuSolveClicked() {
        //First reset the board-state to the initial board (we solve from the beginning, not the current state)
        board = new Board(initialBoard);
        refreshBoard();
        infoLabel.setText("Your initial board");
        movesUsed = 0;

        //schedule solver step display to be human-readable using java.util.Timer and java.util.TimerTask
        java.util.Timer timer = new Timer();
        //delay in milliseconds before beginning scheduled repeated task
        long delay = 800L;
        //delay in milliseconds in between performing scheduled repeated task
        long period = 800L;
        TimerTask repeatRefresh = new TimerTask() {
            //keep track of where we are in our list of moves
            int index = 0;
            final ArrayList<Move> moves = win.getMoveList();

            @Override //the task to be repeated
            public void run() {
                //get the next move in our list
                Move move = moves.get(index);
                //make that move
                board = board.move(move);
                //Debugging console output
//                System.out.printf("Made move: %s%n", move);
                //tell the system we made a move and display the new board
                movesUsed++;
                refreshBoard();

                //if we're at the end of our list, stop repeating run()
                index++;
                if (index >= moves.size()) {
                    cancel();
                }
            }
        };
        timer.schedule(repeatRefresh, delay, period);
    }

    /**
     * Start a new game
     */
    private void menuNewGameClicked() {
        newGame();
    }

    /**
     * Save the current game (board and number of moves made)
     */
    private void menuSaveClicked() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("games.txt"))) {
            out.writeObject(board);
            savedMovesUsed = movesUsed;
            infoLabel.setText("Game saved");
            //debugging console output
//            System.out.println("Game saved.");
        } catch (IOException ex) {
            System.out.println("games.txt not found, please re-download application");
            ex.printStackTrace();
        }
    }

    /**
     * Save the current game (board and number of moves made)
     */
    private void menuLoadClicked() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("games.txt"))) {
            Object object = in.readObject();
            if (object instanceof Board) {
                movesUsed = savedMovesUsed;
                newGame((Board) object);
                //Debugging console output
//                System.out.println("Game loaded.");
            } else throw new IllegalArgumentException("Save file corrupted");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("games.txt not found, please re-download application");
            ex.printStackTrace();
        }
    }

    /**
     * Moves the clicked tile to the adjacent blank space, if there is one
     *
     * @param e the tile clicked
     */
    private void tileClicked(MouseEvent e) {
        JLabel source = null;
        if (e.getSource() instanceof JLabel) {
            source = (JLabel) e.getSource();
        }
        //stores board indices [row][column] of tile
        int[] rowCol;
        assert source != null;
        if (source.getText() != null) {
            rowCol = board.getTileIndex(Integer.parseInt(source.getText()));
        } else {
            rowCol = board.getTileIndex(0);
        }
        if (board.moveTile(rowCol[0], rowCol[1]))
            movesUsed++;
        refreshBoard();
    }

    /**
     * Refreshes GUI display to match internal board state
     */
    private void refreshBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getTiles()[i][j] != 0) {
                    tiles[i][j].setText(String.valueOf(board.getTiles()[i][j]));
                    tiles[i][j].setBorder(new BevelBorder(BevelBorder.RAISED));
                } else {
                    tiles[i][j].setText(null);
                    tiles[i][j].setBorder(null);
                }
                tiles[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                tiles[i][j].setFont(new Font("Segoe UI", Font.BOLD, 24));
                tilePanel.add(tiles[i][j]);
            }
        }
        if (board.isWin()) {
            infoLabel.setText("You win!");
            System.out.println("You win!");
            return;
        }
        movesLabel.setText(String.format("Moves used so far: %d", movesUsed));
    }
}
