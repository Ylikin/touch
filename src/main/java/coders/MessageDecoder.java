package coders;

import com.google.gson.Gson;
import entities.Message;
import lombok.extern.log4j.Log4j;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
@Log4j
public class MessageDecoder implements Decoder.Text<Message> {
   private static Gson gson = new Gson();
    @Override
    public Message decode(String s) throws DecodeException {
        log.info(s+" in Decode message");
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return s!=null;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
