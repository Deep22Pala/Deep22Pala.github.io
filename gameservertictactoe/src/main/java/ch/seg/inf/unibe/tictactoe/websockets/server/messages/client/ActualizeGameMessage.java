package ch.seg.inf.unibe.tictactoe.websockets.server.messages.client;

import ch.seg.inf.unibe.tictactoe.websockets.server.messages.Message;

public record ActualizeGameMessage(String[] board,
                                   String turn,
                                   boolean gameEnd,
                                   char mark,
                                   String winner) implements Message {
}
