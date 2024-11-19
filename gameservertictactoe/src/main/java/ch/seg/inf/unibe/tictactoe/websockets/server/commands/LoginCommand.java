package ch.seg.inf.unibe.tictactoe.websockets.server.commands;

import ch.seg.inf.unibe.tictactoe.websockets.application.Player;
import ch.seg.inf.unibe.tictactoe.websockets.application.TicTacToe;
import ch.seg.inf.unibe.tictactoe.websockets.server.ServerWebsocket;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.client.InitializeGameMessage;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.client.SetTitleMessage;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.client.SuccessfulLoginMessage;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.server.LoginMessage;

import javax.websocket.Session;
import java.util.Arrays;
import java.util.Objects;

public class LoginCommand implements Command {

    private final Session session;

    private final LoginMessage loginMessage;

    public LoginCommand(Session session, LoginMessage loginMessage) {
        this.session = session;
        this.loginMessage = loginMessage;
    }

    @Override
    public void execute() {
        System.out.println("Execute: " + this);
        Player player = new Player(this.loginMessage.username());
        ServerWebsocket.addSession(session, player);
        sendMessage(player);
    }

    public void sendMessage(Player player) {
        TicTacToe game = ServerWebsocket.getGame(this.loginMessage.gameId());
        if (game == null){
            TicTacToe g = new TicTacToe();
            try {
                g.addPlayer(player);
            }
            catch (TicTacToe.TooManyPlayerException ignore){}
            ServerWebsocket.addGame(this.loginMessage.gameId(),g);
            ServerWebsocket.sendMessage(player, new SuccessfulLoginMessage(this.session.getId(), this.loginMessage.username()));
            ServerWebsocket.sendMessage(player, new SetTitleMessage("Waiting for an opponent."));
        } else if (Arrays.stream(game.getPlayer()).filter(Objects::isNull).count() == 1) {
            try {
                game.addPlayer(player);
            }
            catch (TicTacToe.TooManyPlayerException ignored){}
            ServerWebsocket.sendMessage(player, new SuccessfulLoginMessage(this.session.getId(), this.loginMessage.username()));
            ServerWebsocket.sendMessage(game.getPlayer(), new SetTitleMessage(game.getPlayer()[0].getName()+" vs. "+game.getPlayer()[1].getName()));
            ServerWebsocket.sendMessage(game.getPlayer(), new InitializeGameMessage(this.loginMessage.gameId(), ServerWebsocket.getSession(game.currentPlayer()).getId()));
        }
        else if (game.getPlayer().length >= 2){
            ServerWebsocket.sendMessage(player, new SetTitleMessage("Too many players for game: 42"));
        }
    }

    @Override
    public String toString() {
        return "LoginCommand{" +
                "session=" + session.getId() +
                ", loginMessage=" + loginMessage +
                '}';
    }
}
