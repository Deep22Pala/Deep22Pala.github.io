package ch.seg.inf.unibe.tictactoe.websockets.application;

import java.io.IOException;

public class TicTacToe {

    static final int X = 0;
    static final int O = 1;

    protected int rows;
    protected int cols;
    protected int winningScore;

    private Player winner;

    protected char[][] gameState;
    protected Player[] player;
    protected int turn = X; // initial turn
    protected int squaresLeft;

    /**
     * The state of the game is represented as 3x3 array of chars marked ' ',
     * 'X', or 'O'. We index the state using chess notation, i.e., column is 'a'
     * through 'c' and row is '1' through '3'.
     */
    public TicTacToe() {
        this.player = new Player[2];
        this.rows = this.cols = 3;
        this.winningScore = 3;
        this.squaresLeft = this.squares();
        this.gameState = new char[rows][cols];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                this.set(col, row, ' ');
            }
        }
    }

    /**
     * set() and get() translate between chess coordinates and array indices.
     */
    void set(int col, int row, char mark) {
        assert inRange(col, row);
        gameState[col][row] = mark;
    }

    public char get(int col, int row) {
        assert inRange(col, row);
        return gameState[col][row];
    }

    /**
     * The game is not over as long as there is no winner and somebody can still
     * make a move ...
     */
    public boolean notOver() {
        return winner == null;
        //return this.winner().isNobody() && this.squaresLeft() > 0;
    }

    /**
     * A plain ascii representation of the game, mainly for debugging purposes.
     */
    public String toString() {
        StringBuffer rep = new StringBuffer();

        for (int row = this.rows - 1; row >= 0; row--) {
            rep.append(1 + row);
            if (row < 9) {
                rep.append(' '); // extra space for single digit
            }
            rep.append("  ");
            for (int col = 0; col < this.cols; col++) {
                rep.append(this.get(col, row));
                if (col < this.cols - 1) {
                    rep.append(" | ");
                }
            }
            rep.append('\n');
            if (row > 0) {
                rep.append("   ---");
                for (int i = 1; i < this.cols; i++) {
                    rep.append("+---");
                }
                rep.append("\n");
            }
        }
        rep.append(" ");
        for (int col = 0; col < this.cols; col++) {
            rep.append("   ");
            rep.append((char) ('a' + col));
        }
        rep.append("\n");
        return (rep.toString());
    }

    /**
     * Needed for getter and setter preconditions. Reports true if coordinates
     * are valid.
     */
    public boolean inRange(int col, int row) {
        return ((0 <= col) && (col < this.cols) && (0 <= row) && (row < this.rows));
    }

    /**
     * Called by the current player during an update(). The player attempts to
     * put a mark at a location.
     */
    public void move(String coord, char mark) {
        assert this.notOver();
        int col = getCol(coord);
        int row = getRow(coord);
        assert this.get(col, row) == ' ';
        this.set(col, row, mark);
        this.squaresLeft--;
        this.swapTurn();
        this.checkWinner(col, row);
        assert this.invariant();
    }

    /**
     * Extract the column letter from a String coordinate and convert to an
     * index. E.g., "b17" -> 1 NB: package scope for testing
     */
    int getCol(String coord) {
        assert coord != null;
        assert coord.length() > 0;
        char col = coord.charAt(0);
        assert (col >= 'a') && (col <= 'z');
        return col - 'a';
    }

    /**
     * Extract the column digits from a String coordinate and convert to an
     * index. E.g., "b17" -> 16 NB: package scope for testing
     */
    int getRow(String coord) {
        assert coord != null;
        assert coord.length() > 1;
        try {
            int row = Integer.parseInt(coord.substring(1));
            return row - 1;
        } catch (NumberFormatException err) {
            throw new AssertionError(err.getMessage());
        }
    }

    /**
     * Ask the current player to make a move.
     */
    public void update(String coord) throws IOException {
        this.player[turn].move(this, coord);
    }

    public Player[] getPlayer() {
        return this.player;
    }

    /**
     * Adds a new player for this game and sets the player's mark.
     */
    public void addPlayer(Player player) throws TooManyPlayerException {
        if (this.player[0] == null) {
            this.player[0] = player;
            player.setMark('X');
        } else if (this.player[1] == null) {
            this.player[1] = player;
            player.setMark('O');
        } else {
            throw new TooManyPlayerException();
        }
    }

    public static class TooManyPlayerException extends Exception {
        public TooManyPlayerException() {
            super("Too many players!");
        }
    }

    public Player currentPlayer() {
        return player[turn];
    }

    public Player winner() {
        return this.winner;
    }

    public int squaresLeft() {
        return this.squaresLeft;
    }

    protected void swapTurn() {
        turn = (turn == X) ? O : X;
    }

    /**
     * New algorithm needed for larger boards. Create a "Runner" that starts at
     * the current position, and runs back in forth in a given direction,
     * returning the length of the run (i.e., number of consecutive pieces of
     * the same Player). If the number is big enough, the current Player wins.
     */
    protected void checkWinner(int col, int row) {
        char player = this.get(col, row);
        Runner runner = new Runner(this, col, row);

        // check vertically
        if (runner.run(0, 1) >= this.winningScore) {
            this.setWinner(player);
            return;
        }
        // check horizontally
        if (runner.run(1, 0) >= this.winningScore) {
            this.setWinner(player);
            return;
        }
        // check diagonally
        if (runner.run(1, 1) >= this.winningScore) {
            this.setWinner(player);
            return;
        }
        // and check the other diagonal
        if (runner.run(1, -1) >= this.winningScore) {
            this.setWinner(player);
        }
    }

    /**
     * Look up which player is the winner, and set winner accordingly. Hm. Maybe
     * we should store Players instead of chars in our array!
     */
    protected void setWinner(char aPlayer) {
        if (aPlayer == ' ') {
            return;
        }
        if (aPlayer == player[X].getMark()) {
            winner = player[X];
        } else {
            winner = player[O];
        }
    }

    /**
     * These seem obvious, which is exactly why they should be checked.
     */
    protected boolean invariant() {
        return (turn == X || turn == O) && (this.notOver() || this.winner() == player[X] || this.winner() == player[O] || this.winner().isNobody()) && (squaresLeft < this.squares()
                // else, initially:
                || turn == X && this.winner().isNobody());
    }

    /**
     * @return total number of squares Package scope for testing.
     */
    int squares() {
        return rows * cols;
    }
}
