package ch.seg.inf.unibe.tictactoe.websockets.server;

import ch.seg.inf.unibe.tictactoe.websockets.application.Player;
import ch.seg.inf.unibe.tictactoe.websockets.application.TicTacToe;
import ch.seg.inf.unibe.tictactoe.websockets.server.commands.Command;
import ch.seg.inf.unibe.tictactoe.websockets.server.commands.LoginCommand;
import ch.seg.inf.unibe.tictactoe.websockets.server.commands.MoveCommand;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.Message;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.server.LoginMessage;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.server.MoveMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/endpoint", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ServerWebsocket {

    private final static Map<Session, Player> sessions = Collections.synchronizedMap(new HashMap<>());

    private final static Map<Player, Session> players = Collections.synchronizedMap(new HashMap<>());

    /**
     * Game ID -> BoardGame
     */
    private final static HashMap<String, TicTacToe> games = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Session for new client: " + session.getId());
        addSession(session, null);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("End session: " + session.getId());
        removeSession(session, getPlayer(session));
    }

    @OnMessage
    public void onMessage(Session session, Message message) {
        System.out.println("Dealing with client message ...");
        executeCommand(session, message);
    }

    private void executeCommand(Session session, Message message) {
        System.out.println("create and execute command: " + message);
        if (message == null) return;
        switch (message.getClass().getSimpleName()){
            case "LoginMessage":{
                Command cmd = new LoginCommand(session, (LoginMessage) message);
                cmd.execute();
                return;
            }
            case "MoveMessage": {
                Command cmd = new MoveCommand(session, (MoveMessage) message);
                cmd.execute();
                return;
            }


        }
    }

    public static void addSession(Session session, Player player) {
        sessions.put(session, player);

        if (player != null) {
            players.put(player, session);
        }
    }

    public static void removeSession(Session session, Player player) {
        sessions.remove(session);

        if (player != null) {
            players.remove(player);
        }
    }

    public static Session getSession(Player player) {
        return players.get(player);
    }

    public static Player getPlayer(Session session) {
        return sessions.get(session);
    }

    public static void addGame(String gameID, TicTacToe game) {
        games.put(gameID, game);
    }

    public static void removeGame(String gameID) {
        games.remove(gameID);
    }

    public static TicTacToe getGame(String gameID) {
        return games.get(gameID);
    }

    public static boolean hasGame(String gameID) {
        return games.containsKey(gameID);
    }

    public static int playerInGame(String gameID) {
        int count = 0;

        if (hasGame(gameID)) {
            for (Player player : getGame(gameID).getPlayer())  {
                if (player != null) {
                    ++count;
                }
            }
        }
        return count;
    }

    public static void sendMessage(Player[] receivers, Message message) {
        for (Player player : receivers) {
            if (player != null) {
                sendMessage(player, message);
            }
        }
    }

    public static void sendMessage(Player receiver, Message message) {
        try {
            getSession(receiver).getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }
}
