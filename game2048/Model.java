package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: Aarushi Walia
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        board.setViewingPerspective(side);
        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        for (int col = 0; col < board.size(); col += 1) {
            int[] currentcol = new int [board.size()];
            int i = 0;
            for (int row = board.size() - 1; row >= 0; row -= 1) {
                Tile current = board.tile(col, row);
                if (current == null) {
                    continue;
                }
                if (moveUp (col, row)) {
                    int numMoved = row + amountMoved(col, row, currentcol);
                    boolean scoredpoint = board.move(col, numMoved, current);
                    changed = true;
                    if (scoredpoint) {
                        score += 2*current.value();
                        currentcol[i] = numMoved;
                        i += 1;
                    }
                }
            }
        }
        board.setViewingPerspective(side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    private boolean moveUp (int col, int row) {
        if (row == 3) {
            return false;
        }
        for (int row1 = row + 1; row1 < board.size(); row1 += 1) {
            if (board.tile(col, row1) == null) {
                return true;
            }
            if (board.tile(col, row).value() == board.tile(col, row1).value()) {
                return true;
            }
        }
        return false;
    }

    private int amountMoved (int col, int row, int[] currentcol) {
        int counter = 0;
        for (int row1 = row + 1; row1 < board.size(); row1 += 1) {
            for (int j : currentcol) {
                if (row1 == j) {
                    return counter;
                }
            }
            if (board.tile(col, row1) == null) {
                counter += 1;
            } else if (board.tile(col, row).value() == board.tile(col, row1).value()) {
                counter += 1;
            } else if (board.tile(col, row).value() != board.tile(col, row1).value()) {
                break;
            }
        }
        return counter;
    }
//        for (int x = 0; x < board.size(); x = x + 1) {
//            for (int y = 0; y < board.size(); y = y + 1) {
//                Tile current = board.tile(x, y);
//                if (board.tile(x, y) != null) {
//                    board.move(x, 3, current);
//                    changed = true;
//                    score *= 2;
//                }
//            }
//       }

//    public boolean tileCanMove(Board b) {
//        if (emptySpaceExists(b)) {
//            return true;
//        }
//        else if (atLeastOneMoveExists(b)) {
//            return true;
//        }
//        return false;
//    }

//    public void singleColumn(int col) {
//        for (int row = 3; row => 0; row = row - 1) {
//            Tile current = board.tile(col, row);
//            if (board.tile(col, row + 1) = null) {
//                ...
//            }
//            else if (row = ...) {
//                if (board.tile(col, row).value() == board.tile(col, row + 1).value) {
//
//                }
//            }
//        }
//    }

//        for (int row = 2; row < board.size(); row = row - 1) {
//            Tile current = board.tile(int col, row)
//            if atLeastOneMoveExists(board) {
//
//            }
//        }
    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
//        for (int x = 0; x < b.size(); x = x + 1) {
//            for (int y = 0; y < b.size(); y = y + 1) {
//                if (b.tile(x, y) == null) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        }
//        return false;
//    }
    for (int x = 0; x < b.size(); x = x + 1) {
        for (int y = 0; y < b.size(); y = y + 1) {
            if (b.tile(x, y) == null) {
                return true;
            }
            if (x < 3) {
                if (b.tile(x + 1, y) == null) {
                    return true;
                }
            }
            if (y < 3) {
                if (b.tile(x, y + 1) == null) {
                    return true;
                }
            }
            if (x > 0) {
                if (b.tile(x - 1, y) == null) {
                    return true;
                }
            }
            if (y > 0) {
                if (b.tile(x, y - 1) == null) {
                    return true;
                }
            }
        }
    }
        return false;
}

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
            // TODO: Fill in this function.
            for (int x = 0; x < b.size(); x = x + 1) {
                for (int y = 0; y < b.size(); y = y + 1) {
                    if (b.tile(x, y) == null) {
                        continue;
                    }
                    if (b.tile(x, y).value() == MAX_PIECE) {
                        return true;
                    }
                    }
                }
        return false;
        }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if (emptySpaceExists(b)) {
            return true;
        }
        for (int x = 0; x < b.size(); x = x + 1) {
            for (int y = 0; y < b.size(); y = y + 1) {
//                if (x < b.size() - 1) {
                if (b.tile(x, y) == null) {
                    return true;
                }
                if (x < 3) {
                    if (b.tile(x + 1, y) == null) {
                        return true;
                    }
                    if (b.tile(x, y).value() == b.tile(x + 1, y).value()) {
                        return true;
                    }
                }
                if (y < 3) {
                    if (b.tile(x, y + 1) == null) {
                        return true;
                    }
                    if (b.tile(x, y).value() == b.tile(x, y + 1).value()) {
                        return true;
                    }
                }
                if (x > 0) {
                    if (b.tile(x - 1, y) == null) {
                        return true;
                    }
                    if (b.tile(x, y).value() == b.tile(x - 1, y).value()) {
                        return true;
                    }
                }
                if (y > 0) {
                    if (b.tile(x, y - 1) == null) {
                        return true;
                    }
                    if (b.tile(x, y).value() == b.tile(x, y - 1).value()) {
                        return true;
                    }
                }
            }
                }
        return false;
            }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
