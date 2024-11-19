package ch.seg.inf.unibe.tictactoe.websockets.server.commands;

import ch.seg.inf.unibe.tictactoe.websockets.application.Player;
import ch.seg.inf.unibe.tictactoe.websockets.application.TicTacToe;
import ch.seg.inf.unibe.tictactoe.websockets.server.ServerWebsocket;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.client.ActualizeGameMessage;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.server.MoveMessage;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class MoveCommand implements Command {

    private final Session session;

    private final MoveMessage moveMessage;

    public static Map<Integer, String> boardMap = new HashMap<>() {
        {
            put(0, "a1");
            put(1, "b1");
            put(2, "c1");
            put(3, "a2");
            put(4, "b2");
            put(5, "c2");
            put(6, "a3");
            put(7, "b3");
            put(8, "c3");
        }
    };

    public MoveCommand(Session session, MoveMessage moveMessage) {
        this.session = session;
        this.moveMessage = moveMessage;
    }

    /**
     * Converts the server board representation to the client board representation.
     */
    private String[] getBoard(TicTacToe ticTacToe) {
        String[] board = new String[9];
        int counter = 0;

        for (int i = 0; i < 3; i++) {
            for (int a = 0; a < 3; a++) {
                board[counter] = String.valueOf(ticTacToe.get(a, i));
                counter++;
            }
        }
        return board;
    }

    /**
     * Converts the client board field representation to the server board field representation.
     */
    private String idxToCoord(String move) {
        return boardMap.get(Integer.valueOf(move));
    }

    @Override
    public void execute() {
        System.out.println("Execute: " + this);
        TicTacToe ticTacToe = ServerWebsocket.getGame(this.moveMessage.gameId());
        ticTacToe.move(idxToCoord(this.moveMessage.move()), ticTacToe.currentPlayer().getMark());
        System.out.println(ticTacToe);
        sendMessage(ticTacToe.getPlayer(), ticTacToe);
    }

    public void sendMessage(Player[] players, TicTacToe ticTacToe) {
        ServerWebsocket.sendMessage(players, new ActualizeGameMessage(getBoard(ticTacToe),
                ServerWebsocket.getSession(ticTacToe.currentPlayer()).getId(),
                !ticTacToe.notOver(),
                ticTacToe.currentPlayer().getMark(),
                !ticTacToe.notOver() ? ticTacToe.winner().getName() : null
                ));
    }

    @Override
    public String toString() {
        return "MoveCommand{" +
                "session=" + session.getId() +
                ", moveMessage=" + moveMessage +
                '}';
    }
}
