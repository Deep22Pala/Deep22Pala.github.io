package ch.seg.inf.unibe.tictactoe.websockets.server.messages.server;

import ch.seg.inf.unibe.tictactoe.websockets.server.messages.Message;

public record MoveMessage(String gameId, String move) implements Message {

    public final static String MESSAGE_TYPE = "MoveMessage";
}
