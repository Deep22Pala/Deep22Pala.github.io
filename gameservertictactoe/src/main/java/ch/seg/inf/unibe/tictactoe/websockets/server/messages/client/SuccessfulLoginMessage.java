package ch.seg.inf.unibe.tictactoe.websockets.server.messages.client;

import ch.seg.inf.unibe.tictactoe.websockets.server.messages.Message;

public record SuccessfulLoginMessage(String sessionId, String username) implements Message {
}
