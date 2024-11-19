package ch.seg.inf.unibe.tictactoe.websockets.server.messages.client;

import ch.seg.inf.unibe.tictactoe.websockets.server.messages.Message;

public record InitializeGameMessage(String gameId, String turn) implements Message {
}
