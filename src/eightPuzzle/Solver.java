package eightPuzzle;

import java.util.ArrayList;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * @author Alec Mills
 * <p>
 * CS 1410 Assignment 09
 * Utilizes an A* algorithm to solve numbered tile puzzles based on the game 'Fifteen', but with dimensions of 3x3
 */
public class Solver {
    /**
     * We use a priority queue in order to allow the A* algorithm to have a heuristic for choosing which move
     * to make at any given point
     */
    private final PriorityQueue<State> queue;
    /**
     * Board representing the goal state, used for comparison against current board-state to detect win condition
     */
    private final Board goal = new Board(new int[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
    });

    /**
     * @param initial State corresponding to the initial board position with no moves made
     */
    public Solver(State initial) {
        queue = new PriorityQueue<>();
        queue.add(initial);
    }

    /**
     * uses inversions method to determine if a given board configuration is solveable
     *
     * @param board the Board to test
     * @return true or false as the board is or is not solveable
     */
    public static boolean isSolveable(Board board) {
        int inversions = 0;
        for (int[] row : board.getTiles()) {
            for (int tile : row) {
                inversions += countInversions(board, tile);
            }
        }
        return inversions % 2 == 0;
    }

    /**
     * @return queue field
     */
    public PriorityQueue<State> getQueue() {
        return queue;
    }

    /**
     * Attempts to find an optimal solution to any given initial board state using A* algorithm and Manhattan heuristic
     *
     * @param initial initial board state
     * @return solved board-state
     */
    public State solve(State initial) {
        State head = initial;
        do {
            ArrayList<State> children = getNeighboringStates(head);
            for (State el : children) {
                if (!el.getMoveList().equals(head.getMoveList())) {
                    queue.add(el);
                }
            }
            head = queue.poll();
        } while ((!head.getBoard().equals(goal)));
        return head;
    }

    /**
     * helper method for isSolveable; count's inversions for a given tile on a given board
     *
     * @param board the board to be checked
     * @param value the tile to be checked
     * @return number of inversions
     */
    private static int countInversions(Board board, int value) {
        int inversions = 0;
        if (value != 0) {
            //copy board[][] into a single dimension array for ease of comparison
            int[] tiles = new int[9];
            int index = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    tiles[index] = board.getTiles()[i][j];
                    index++;
                }
            }
            //compare values
            int[] rowCol = board.getTileIndex(value);
            int valueIndex = (rowCol[0] * 3) + rowCol[1];
            for (int i = valueIndex + 1; i < 9; i++) {
                if (tiles[i] < value && tiles[i] != 0) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    /**
     * finds all board-states that are reachable within one valid move from the current board state
     *
     * @param state the current board state
     * @return ArrayList<State> containing valid moves
     */
    private ArrayList<State> getNeighboringStates(State state) {
        //list of neighboring states
        ArrayList<State> children = new ArrayList<>();
        Board board = new Board(state.getBoard());
        int[] indices = state.getBoard().getTileIndex(0);

        //if tile is not on the left edge, there is a valid move to the left of our zero tile
        if (indices[1] > 0) {
            children.add(new State(board.move(Move.LEFT), state, Move.LEFT));
            board = new Board(state.getBoard());
        }
        //if tile is not on the right edge, there is a valid move to the right of our zero tile
        if (indices[1] < 2) {
            children.add(new State(board.move(Move.RIGHT), state, Move.RIGHT));
            board = new Board(state.getBoard());
        }
        //if tile is not on the top edge, there is a valid move above our zero tile
        if (indices[0] > 0) {
            children.add(new State(board.move(Move.UP), state, Move.UP));
            board = new Board(state.getBoard());
        }
        //if tile is not on the bottom edge, there is a valid move below our zero tile
        if (indices[0] < 2) {
            children.add(new State(board.move(Move.DOWN), state, Move.DOWN));
        }
        return children;
    }


    /**
     * test client
     *
     * @param args command-line arguments (n/a)
     */
    public static void main(String[] args) {
        Board initialBoard = new Board();
        System.out.printf("Board from self:%n%s", initialBoard);
        State initialState = new State(initialBoard);

        //test constructor
        Solver solver = new Solver(initialState);
        System.out.println("Board from queue: ");

        //test getChildrenNodes
        for (State el : solver.getNeighboringStates(Objects.requireNonNull(solver.getQueue().poll()))) {
            System.out.println(el.getBoard());
            System.out.println();//formatting
        }

        //test inversions function and isSolveable
        int[][] solveable = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8}
        };

        int[][] notSolveable = {
                {1, 2, 3},
                {4, 5, 6},
                {8, 7, 0}
        };

        Board solveBoard = new Board(solveable);
        Board notSolveBoard = new Board(notSolveable);
        System.out.printf("Board: %n%s%n%s%n", solveBoard, (isSolveable(solveBoard)) ? "solveable" : "not solveable");
        System.out.printf("Board: %n%s%n%s%n", notSolveBoard, (isSolveable(notSolveBoard)) ? "solveable" : "not solveable");


        //test algorithm

        //get a guaranteed solveable board
        while (!isSolveable(initialBoard)) {
            initialBoard = new Board();
            solver = new Solver(new State(initialBoard));
        }
        //used if we want to feed the algorithm a known board with a known solution
//        initialBoard = new Board(new int[][]{
//            {2, 0, 3},
//            {1, 5, 6},
//            {4, 7, 8}
//        });
        System.out.println("Initial Board: ");
        System.out.println(initialBoard);
        if (isSolveable(initialBoard)) {
            System.out.println("Board is solveable.");
            State win = solver.solve(new State(initialBoard));
            System.out.printf("Solved board in %d moves%n", win.getNumMoves());
            //test reconstruction of solution steps
            Board reconstructed = new Board(initialBoard);
            for (Move move : win.getMoveList()) {
                System.out.println(reconstructed);
                System.out.println();
                System.out.println("Moved " + move);
                reconstructed = reconstructed.move(move);
            }
            System.out.println(reconstructed);
        } else
            System.out.println("Board is not solveable.");

    }
}