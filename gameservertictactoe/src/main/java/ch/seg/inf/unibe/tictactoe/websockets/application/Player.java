package ch.seg.inf.unibe.tictactoe.websockets.application;

public class Player {

    private String name;

    protected char mark;
    
    public Player(String name) {
        this.name = name;
    }

    /**
     * @return the username of the Player
     */
    public String getName() {
        return name;
    }

    /**
     * @return the char representing the Player
     */
    public char getMark() {
        return mark;
    }

    public void setMark(char mark) {
        this.mark = mark;
    }

    /**
     * @return String representation of Player
     */
    public String name() {
        if (this.isNobody())
            return "nobody";
        else
            return Character.toString(this.getMark());
    }

    /**
     * By convention, a Player without a mark is nobody!
     */
    public boolean isNobody() {
        return this.getMark() == ' ';
    }

    /**
     * Ask the user (or script) where to move and attempt to perform that move.
     */
    public void move(TicTacToe game, String coord)  {
        game.move(coord, this.getMark());
    }

}
