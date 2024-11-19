package ch.seg.inf.unibe.tictactoe.websockets.server;

import ch.seg.inf.unibe.tictactoe.websockets.server.messages.Message;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.server.LoginMessage;
import ch.seg.inf.unibe.tictactoe.websockets.server.messages.server.MoveMessage;
import com.google.gson.*;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.lang.reflect.Type;

public class MessageDecoder implements Decoder.Text<Message> {

    private static class MessageDeserializer implements JsonDeserializer<Message> {

        Gson gsonBase = new Gson(); // basic GSON

        @Override
        public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement e = jsonObject.get(MessageEncoder.MESSAGE_TYPE_FIELD);
            switch (e.toString()) {
                case "\"LoginMessage\"":
                    return gsonBase.fromJson(json, LoginMessage.class);
                case "\"MoveMessage\"":
                    return gsonBase.fromJson(json, MoveMessage.class);
                default: return null;
            }
        }
    }

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .create();

    @Override
    public Message decode(String s) throws DecodeException {
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }

    @Override
    public void init(EndpointConfig config) {
        // Currently not needed ...
    }

    @Override
    public void destroy() {
        // Currently not needed ...
    }

}
