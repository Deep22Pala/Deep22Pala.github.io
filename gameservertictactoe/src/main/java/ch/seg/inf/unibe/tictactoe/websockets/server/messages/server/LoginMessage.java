package ch.seg.inf.unibe.tictactoe.websockets.server.messages.server;

import ch.seg.inf.unibe.tictactoe.websockets.server.messages.Message;

public record LoginMessage(String gameId, String username) implements Message {
    public final static String MESSAGE_TYPE = "LoginMessage";
}
