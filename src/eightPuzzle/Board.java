package eightPuzzle;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Alec Mills
 * <p>
 * CS 1410 Assignment 09
 * Represents a 8-puzzle game board:
 * a 3x3 grid arrangement of 'tiles' each of which is an integer value
 */
public class Board implements Serializable {
    /**
     * serial version UID
     */
    private static final long serialVersionUID = -7694523832918690523L;
    /**
     * 2d-array representation of the game board
     */
    private final int[][] tiles;
    /**
     * 2d-array representation of a winning game board
     */
    private final int[][] goal = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
    };

    /**
     * constructs a new randomly arranged board consisting of values [0,8] wherein 0 indicates a blank space
     */
    public Board() {
        tiles = new int[3][3];
        int[] values = genValues(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 0});

        int offset;
        for (int i = 0; i < 3; i++) {
            offset = 2 * i;
            tiles[i] = Arrays.copyOfRange(values, i + offset, i + offset + 3);
        }
    }

    /**
     * constructs a new board consisting of values that are a copy of int[][] parameter
     *
     * @param values the values to be copied
     */
    public Board(int[][] values) {
        tiles = new int[3][];
        for (int i = 0; i < 3; i++) {
            tiles[i] = Arrays.copyOf(values[i], 3);
        }
    }

    /**
     * copy constructor
     *
     * @param board Board to be copied
     */
    public Board(Board board) {
        tiles = new int[3][];
        for (int i = 0; i < 3; i++) {
            tiles[i] = Arrays.copyOf(board.getTiles()[i], 3);
        }
    }


    /**
     * finds row and column indices of a given value
     *
     * @param value value to search for
     * @return {row, col} values are Integer.MIN if value was not found
     */
    public int[] getTileIndex(int value) {
        if (value < 0 || value > 8) {
            throw new IllegalArgumentException("Bad value passed to Board.getTileIndex(): must be in interval [0,8]");
        }
        int row = Integer.MIN_VALUE;
        int col = Integer.MIN_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (getTiles()[i][j] == value) {
                    row = i;
                    col = j;
                }
            }
        }
        return new int[]{row, col};
    }

    /**
     * @return a string representation of this
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : tiles) {
            for (int cell : row) {
                if (cell == 0) {
                    sb.append("[ ] ");
                } else
                    sb.append("[").append(cell).append("] ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @return tiles field
     */
    public int[][] getTiles() {
        return tiles;
    }


    /**
     * moves a 'tile' from one cell to another if the move is possible (i.e. moving a non-blank cell to a blank cell
     *
     * @return true or false as the move was possible or not possible
     */
    public boolean moveTile(int row, int col) {
        //first make sure the parameters are reasonable values
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return false;
        }
        int tile = getTiles()[row][col];
        if (tile == 0) {
            return false;
        }
        boolean inBounds;
        //check from one row to the left, to one row to the right
        for (int i = row - 1; i <= row + 1; i++) {
            //check from one column above, to one column below
            for (int j = col - 1; j <= col + 1; j++) {
                //use this to make sure our indices are reasonable
                inBounds = ((i >= 0 && i < 3) && (j >= 0 && j < 3));
                if (inBounds) {
                    int otherTile = getTiles()[i][j];
                    //can't move a cell to itself
                    if (otherTile != tile) {
                        //we want to ignore diagonals
                        if (!(i != row && j != col)) {
                            //if otherTile is blank, swap tile and otherTile
                            if (otherTile == 0) {
                                setTile(i, j, tile);
                                setTile(row, col, otherTile);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        //if we didn't return true yet, there was no adjacent blank tile
        return false;
    }

    public Board move(Move move) {
        int[] indices = getTileIndex(0);
        Board movedBoard = new Board(this);
        int value;

        switch (move) {
            case UP:
                value = movedBoard.getTiles()[indices[0] - 1][indices[1]];
                movedBoard.setTile(indices[0] - 1, indices[1], 0);
                movedBoard.setTile(indices[0], indices[1], value);
//                movedBoard.moveTile(indices[0] - 1, indices[1]);
                break;
            case DOWN:
                value = movedBoard.getTiles()[indices[0] + 1][indices[1]];
                movedBoard.setTile(indices[0] + 1, indices[1], 0);
                movedBoard.setTile(indices[0], indices[1], value);
//                movedBoard.moveTile(indices[0] + 1, indices[1]);
                break;
            case LEFT:
                value = movedBoard.getTiles()[indices[0]][indices[1] - 1];
                movedBoard.setTile(indices[0], indices[1] - 1, 0);
                movedBoard.setTile(indices[0], indices[1], value);
//                movedBoard.moveTile(indices[0], indices[1] - 1);
                break;
            case RIGHT:
                value = movedBoard.getTiles()[indices[0]][indices[1] + 1];
                movedBoard.setTile(indices[0], indices[1] + 1, 0);
                movedBoard.setTile(indices[0], indices[1], value);
//                movedBoard.moveTile(indices[0], indices[1] + 1);
                break;
            default:
                throw new IllegalArgumentException("Bad argument passed to Board.move(Move move)");
        }
        return movedBoard;
    }

    /**
     * compares boards
     *
     * @param o board to compared to this
     * @return true if parameter.tiles[][] values are all the same or false if they are not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getTiles()[i][j] != getTiles()[i][j])
                    return false;
            }
        }
        return true;
    }

    /**
     * uses Arrays.hashCode to implement
     *
     * @return hash
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(getTiles());
    }

    public boolean isWin() {
        return this.equals(new Board(goal));
    }

    /**
     * constructor helper: creates a copy an int array sorted in random order using simplified Fisher-Yates algorithm
     *
     * @param original array to be randomly sorted
     * @return a new array containing the original values sorted in a random order
     */
    private static int[] genValues(int[] original) {
        Random rand = new Random();
        int max = original.length - 1;

        while (max > 0) {
            swap(original, max, rand.nextInt(max));
            max--;
        }
        return original;
    }

    /**
     * constructor helper: swaps two values in an array
     *
     * @param arr the array that has the values to swap
     * @param a   index of first value to swap
     * @param b   index of second value to swap
     */
    private static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    /**
     * changes the value of a tile on the board
     *
     * @param row   row-index of tile to be changed
     * @param col   col-index of tile to be changed
     * @param value value tile should be changed to
     */
    private void setTile(int row, int col, int value) {
        //first make sure the parameters are reasonable values
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            throw new IllegalArgumentException("Passed bad index values to setTile()");
        }
        tiles[row][col] = value;
    }

//    /**
//     * test client
//     *
//     * @param args command-line arguments (n/a)
//     */
//    public static void main(String[] args) {
//        //test proper board generation
//        Board board = new Board();
//        System.out.println("Board one: \n" + board);
//        //end board gen test
//
//        //test .equals() functionality
//        Board boardTwo = new Board(Arrays.copyOf(board.getTiles(), 3));
//        System.out.println("Board two: \n" + boardTwo);
//        if (boardTwo.equals(board) && board.equals(boardTwo)) {
//            System.out.println("Success; boardOne = boardTwo");
//        } else
//            System.out.println("FAILURE");
//        //end .equals() test
//
////        //test .move()functionality NOTE!: success or failure must be manually noted
////        Scanner in = new Scanner(System.in);
////        System.out.println("Enter row and col of tile to move, in one line, separated by a space");
////        String[] inputs = in.nextLine().split(" ");
////        int[] values = {Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1])};
////        in.close();
////
////        System.out.println("Old board: \n" + board);
////        boolean moveExecuted = (board.moveTile(values[0], values[1]));
////
////        System.out.printf("Attempting to move tile (%d, %d):%n", values[0], values[1]);
////        if (moveExecuted) {
////            System.out.println("New board: \n" + board);
////        } else
////            System.out.printf("Cannot move tile (%d, %d)%n", values[0], values[1]);
//        //end .move() test
//
//        //test getTileNeighbors
////        for (int[] el : board.getTileNeighbors(0)) {
////            System.out.printf("Neighbor value: %d row: %d, col: %d%n", board.getTiles()[el[0]][el[1]], el[0], el[1]);
////        }
//
//
//    }
}
