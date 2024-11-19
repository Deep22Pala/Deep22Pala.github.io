const MESSAGE_TYPE_SET_TITLE = "SetTitleMessage";
const MESSAGE_TYPE_SUCCESSFUL_LOGIN = "SuccessfulLoginMessage";
const MESSAGE_TYPE_ACTUALIZE_GAME = "ActualizeGameMessage";
const MESSAGE_TYPE_INITIALIZE_GAME = "InitializeGameMessage";

var uri = "ws://" + document.location.host + document.location.pathname + "endpoint";
var websocket = new WebSocket(uri);
var playerSessionId = null;
var gameId = null;

window.onload = function initial() {
    for (let i = 0; i < 9; i++) {
        var btn = document.getElementById(i);
        btn.disabled = true;
    }
};

websocket.onmessage = function (event) {
    console.log(event);
    executeCommand(event.data);
};

websocket.onopen = function () {
    console.log("Opened connection: " + uri);
};

websocket.onclose = function onClose() {
    console.log("Closed connection: " + uri);

    // Refresh the page... to see lost connection.
    location.reload();
};

function executeCommand(dataString) {
    console.log("Dealing with server message ..." + dataString);
    var message = JSON.parse(dataString);

    switch (message.messageType) {
        case MESSAGE_TYPE_SET_TITLE:
            var c = new TitleCommand(message);
            c.execute();
            break;
        case MESSAGE_TYPE_SUCCESSFUL_LOGIN:
            var c = new SuccessfulLoginCommand(message);
            c.execute();
            break;
        case MESSAGE_TYPE_ACTUALIZE_GAME:
            var c = new ActualizeGameCommand(message);
            c.execute();
            break;
        case MESSAGE_TYPE_INITIALIZE_GAME:
            var c = new InitGameCommand(message);
            c.execute();
            break;
        default:
    }
}

function login() {
    var username = document.getElementById("username").value;
    var gameId = document.getElementById("gameId").value;
    var json = {
        "messageType": "LoginMessage",
        "gameId": gameId,
        "username": username
    };
    console.log("Send " + username + " to server.");
    websocket.send(JSON.stringify(json));
}

function move(fieldId) {
    var json = {
        "messageType": "MoveMessage",
        "gameId": gameId,
        "move": fieldId
    };
    console.log("Send move to server ..");
    websocket.send(JSON.stringify(json));
}

class Command {

    execute() {
        throw new Error('Has to be implemented by subclasses!');
    }
}

class ActualizeGameCommand extends Command {

    constructor(msg) {
        super();
        this._msg = msg;
    }

    execute() {
        var board = this._msg.board;
        var turn = this._msg.turn;

        for (let i = 0; i < 9; i++) {
            var btn = document.getElementById(i);
            btn.disabled = playerSessionId !== turn;

            if (board[i] === "X") {
                btn.className = 'playedFieldX';
                btn.disabled = true;
            } else if (board[i] === "O") {
                btn.className = 'playedFieldO';
                btn.disabled = true;
            }
        }
        if (this._msg.gameEnd) {
            document.getElementById("output").innerHTML += '<br/><div class="alert alert-primary" role="alert"> The game is over! ' + this._msg.winner + ' has won the Game!' + '</div>';

            for (let i = 0; i < 9; i++) {
                document.getElementById(i).disabled = true;
            }
        }
    }
}

class SuccessfulLoginCommand extends Command {

    constructor(msg) {
        super();
        this._msg = msg;
    }

    execute() {
        playerSessionId = this._msg.sessionId;
        document.getElementById("sub").disabled = true;
        document.getElementById("login").innerHTML = "Logged in as <u>" + this._msg.username + "</u></br>";
    }
}

class InitGameCommand extends Command {

    constructor(msg) {
        super();
        this._turn = msg.turn;
        this._gameId = msg.gameId;
    }

    execute() {
        if (playerSessionId === this._turn) {
            for (let i = 0; i < 9; i++) {
                var btn = document.getElementById(i);
                btn.disabled = false;
            }
        }
        gameId = this._gameId;
    }
}

class TitleCommand extends Command {

    constructor(msg) {
        super();
        this._msg = msg;
    }

    execute() {
        document.getElementById("title").innerHTML = this._msg.title;
    }
}
