package coders;

import endpoints.ChatEndpoint;
import entities.Message;
import org.assertj.core.api.Assert.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import java.io.StringReader;

public class DecodeTest {
    private static String stringToJsonMessage(final String name, final String message) {
        return Json.createObjectBuilder().add("name", name).add("text", message).build().toString();
    }



    @DisplayName("Check Decoder String -> Json")
    @Test
    void checkDecoder() throws DecodeException {
        MessageDecoder decoder = new MessageDecoder();
        String pat = stringToJsonMessage("Tom", "Ok");
        Message mes = decoder.decode(pat);
        assertEquals("Ok", mes.getText(), () -> "Object doesnt contain text");
        assertEquals("Tom", mes.getName(), () -> "Object doesnt contain name");

    }
}
