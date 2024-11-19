package ch.seg.inf.unibe.tictactoe.websockets.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;

/**
 * Starts an embedded webserver.
 */
public class GameServer {

    public GameServer() throws Exception {

        // Server settings:
        final String address = "localhost"; // for local testing
        final int port = 8090;
        final String contextPath = "/ESE-TicTacToe";

        // The application can be provided either as a WAR file path or as a webapp folder path:
        final String webAppPath = "src/main/webapp";

        // Create the web server:
        Server server = new Server();

        // Server connector:
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        // Server handlers:
        HandlerCollection handlers = new HandlerCollection();
        server.setHandler(handlers);

        // Setup Web Application Context Handler:
        WebAppContext webAppHandler = new WebAppContext(webAppPath, contextPath);
        handlers.addHandler(webAppHandler);

        // Setup TicTacToe Servlet:
        // ws://127.0.0.1:8085/ESE-TicTacToe/endpoint
        // (1)//(2)      :(3) /(4)          /(5)
        // (1) websocket protocol
        // (2) server address (localhost for testing)
        // (3) port (see ServerConnector)
        // (4) context path (see WebAppContext)
        // (5) websocket endpoint (see ServerWebsocket @ServerEndpoint annotation value attribute)
        JavaxWebSocketServletContainerInitializer.configure(webAppHandler, (servletContext, wsContainer) -> {
            wsContainer.setDefaultMaxSessionIdleTimeout(0); // Sessions will never timeout due to inactivity.
            wsContainer.addEndpoint(ServerWebsocket.class); // Connections handled by TicTacToe websocket endpoint.
        });

        server.start();
        System.out.println("Server started: " + "http://" + address + ":" + port + contextPath + "/");
        server.join();
    }
}
