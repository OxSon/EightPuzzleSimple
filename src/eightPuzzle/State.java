package eightPuzzle;

import java.util.ArrayList;

/**
 * @author Alec Mills
 * <p>
 * CS 1410 Assignment 09
 * Represents a node in the A* search tree
 */
public class State implements Comparable {
    /**
     * board arrangement associated with his state
     */
    private final Board board;
    /**
     * number of numMoves made to reach this state
     */
    private final int numMoves;
    /**
     * result of manhattan function, used to calculate 'cost' of reaching a state
     */
    private final int priority;
    /**
     * moves made on initial board to reach current board-state
     */
    private final ArrayList<Move> previousMoves;


    /**
     * generic constructor
     *
     * @param board    the initial board
     * @param previous the previous State
     * @param move     the move made to reach this state
     */
    public State(Board board, State previous, Move move) {
        this.board = board;
        numMoves = previous.getNumMoves() + 1;
        priority = manhattan(board);
        if (previous.getMoveList() != null) {
            previousMoves = new ArrayList<>(previous.getMoveList());
        } else
            previousMoves = new ArrayList<>();
        previousMoves.add(move);
    }

    /**
     * Specialized constructor representing the initial state of a game, i.e. no moves have been made
     *
     * @param initialBoard the initial tile-arrangement of the board
     */
    public State(Board initialBoard) {
        board = initialBoard;
        numMoves = 0;
        priority = manhattan(board);
        previousMoves = new ArrayList<>();
    }


    /**
     * @return previousMoves field
     */
    public ArrayList<Move> getMoveList() {
        return previousMoves;
    }

    /**
     * @return numMoves field
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * @return board field
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return priority field
     */
    private int getPriority() {
        return priority;
    }

    /**
     * Compares this object with the specified object for order, ordering according to their priority function;
     * <a href = "https://docs.oracle.com/javase/7/docs/api/java/lang/Comparable.html">see here for more</a>
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Object o) {
        //make sure 'o' is an object of the correct class and is not null
        if (this == o)
            return 0;
        if (o == null || getClass() != o.getClass())
            return 1;

        //if it is kosher, compare priority and return result
        State state = (State) o;
        return Integer.compare(this.getPriority(), state.getPriority());
    }

    /**
     * manhattan priority function; used as a heuristic for determining the quality of any given move
     *
     * @return sum of each block's distance away from goal position (in number of numMoves) + number of numMoves made so far
     */
    private int manhattan(Board board) {
        int distances = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int tile = board.getTiles()[i][j];
                //we don't compute manhattan distance of blank tile
                if (tile != 0) {
                    int goalRow;
                    int goalCol;
                    //goalRow depends on value of tile
                    switch (tile) {
                        case 1:
                        case 2:
                        case 3:
                            goalRow = 0;
                            break;
                        case 4:
                        case 5:
                        case 6:
                            goalRow = 1;
                            break;
                        case 7:
                        case 8:
                            goalRow = 2;
                            break;
                        default:
                            throw new IllegalArgumentException("passed bad tile value to State.manhattan(): " + tile);
                    }
                    //goalCol depends on value of goalRow and value of goalTile
                    goalCol = tile - 1 - (3 * goalRow);
                    //using formula |y2 - y1| + |x2 - x1| to compute distances
                    distances += Math.abs(goalRow - i) + Math.abs(goalCol - j);
                }
            }
        }
        return distances + getNumMoves();
    }

//    /**
//     * test client
//     *
//     * @param args command-line arguments (n/a)
//     */
//    public static void main(String[] args) {
//        //test initial State constructor
//        Board board = new Board();
//        State initial = new State(board);
//        System.out.printf("Board parameter;%nPriority: %d%n%s%n", initial.getPriority(), board);
//        if (initial.getBoard().equals(board))
//            System.out.println("Board initialization success");
//        else
//            System.out.println("Board initialization FAIL");
//        if (initial.getNumMoves() == 0)
//            System.out.println("Moves initialization success");
//        else
//            System.out.println("Moves initialization FAIL");
//        System.out.println();//formatting
//        //end initial State constructor test
//
//        //test secondary State constructor
//        Board secondBoard = new Board(board.getTiles());
//        //find a blank tile for us to move and move it
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (secondBoard.getTiles()[i][j] == 0) {
//                    if (j < 2)
//                        secondBoard.moveTile(i, j + 1);
//                    else
//                        secondBoard.moveTile(i, j - 1);
//                }
//            }
//        }
//        //third parameter, i.e. move is garbage value, not relevant for this test
//        State second = new State(secondBoard, initial, Move.UP);
//        System.out.printf("Board after first move:%n%sPriority: %d%n", secondBoard, second.getPriority());
//        if (second.getBoard().equals(secondBoard))
//            System.out.println("Board initialization success");
//        else
//            System.out.println("Board initialization FAIL");
//        if (second.getNumMoves() == initial.getNumMoves() + 1)
//            System.out.println("Moves initialization success");
//        else
//            System.out.println("Moves initialization FAIL");
//        System.out.println();//formatting
//        //end secondary State constructor test
//
//        //test compare function NOTE: must be evaluated manually
//        int comparedValue = initial.compareTo(second);
//        if (comparedValue == 0) {
//            System.out.printf("Initial and second have equal priority: %d == %d%n", initial.getPriority(), second.getPriority());
//        } else
//            System.out.printf("%s has greater priority%n", (comparedValue > 0) ? "Initial" : "Second");
//        //end compare function test
//
//        //test manhattan function by passing it two state with boards whose correct priority is known
//        Board manhattanTwo = new Board(new int[][]{
//            {1, 2, 3},
//            {4, 5, 6},
//            {8, 7, 0}
//        });
//        State oneMoveAway = new State(manhattanTwo);
//        System.out.println(manhattanTwo);
//        if (oneMoveAway.getPriority() == 2)
//            System.out.printf("Priority function with initial state success: %d%n", oneMoveAway.getPriority());
//        else
//            System.out.printf("Priority function with initial state FAIL: %d%n", oneMoveAway.getPriority());
//        Board manhattanThree = new Board(new int[][]{
//            {1, 2, 3},
//            {4, 5, 6},
//            {8, 7, 0}
//        });
//        //third parameter, i.e. move is garbage value, not relevant for this test
//        State oneMovePlusOneMove = new State(manhattanThree, oneMoveAway, Move.UP);
//        if (oneMovePlusOneMove.getPriority() == 3)
//            System.out.printf("Priority function with secondary state success: %d%n", oneMovePlusOneMove.getPriority());
//        else
//            System.out.printf("Priority function with secondary state FAIL: %d%n", oneMovePlusOneMove.getPriority());
//        //end manhattan function test
//
//        //test .equals
//
//
//    }

}
