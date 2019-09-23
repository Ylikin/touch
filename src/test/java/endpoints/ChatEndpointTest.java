package endpoints;

import entities.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChatEndpointTest {
    ChatEndpoint chatEndpoint;

    @BeforeEach
    void init() {
        chatEndpoint = new ChatEndpoint();
    }

    @DisplayName("check null collections after initialisation")
    @Test
    void checkCollectionsAftInitialisation() {
        assertNotNull(ChatEndpoint.agentClient, () -> "agentClient shouldn`t be null ");
        assertNotNull(ChatEndpoint.mapOfMsg, () -> "mapOfMsg shouldn`t be null ");
        assertNotNull(ChatEndpoint.sessionListAg, () -> "sessionLstAg shouldn`t be null ");
        assertNotNull(ChatEndpoint.sessionListCl, () -> "sessionLstCl shouldn`t be null ");
        assertNull(chatEndpoint.session, () -> "session should be null ");
        assertEquals("anonymous", chatEndpoint.usern, () -> "Before connection user should be anonymous");
        assertEquals("anonymous", chatEndpoint.role, () -> "Before connection role should be anonymous");
    }

    @DisplayName("check name parsing and adding client session to collection")
    @Test
    void onOpenClient() throws IOException, EncodeException {
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.getBasicRemote()).thenThrow(new NotNowException());
        chatEndpoint.OnOpen(session, "tom_client");
        assertTrue(ChatEndpoint.sessionListCl.size() == 1);
        assertTrue(ChatEndpoint.sessionListAg.size() == 0);
        assertEquals("tom", chatEndpoint.usern);
        assertEquals("client", chatEndpoint.role);

    }

    @DisplayName("check name parsing and adding agent session to collection")
    @Test
    void onOpenAgent() throws IOException, EncodeException {
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.getBasicRemote()).thenThrow(new NotNowException());
        ChatEndpoint.sessionListCl.clear();
        chatEndpoint.OnOpen(session, "tom_agent");

        assertTrue(ChatEndpoint.sessionListAg.size() == 1);
        assertTrue(ChatEndpoint.sessionListCl.size() == 0);
        assertEquals("tom", chatEndpoint.usern);
        assertEquals("agent", chatEndpoint.role);

    }

    @DisplayName("sending message when agent adding if there was client")
    @Test
    void onOpenAgentClient() throws IOException, EncodeException {
        Session session = Mockito.mock(Session.class);
        List<Message> msg = new ArrayList<>();
        Message mess = Mockito.mock(Message.class);
        msg.add(mess);
        Mockito.when(mess.getText()).thenReturn("tom");
        Mockito.when(session.getBasicRemote()).thenThrow(new NotNowException());
        ChatEndpoint.mapOfMsg.put(session, msg);
        ChatEndpoint.sessionListCl.add(session);
        assertThrows(NotNowException.class, () -> chatEndpoint.OnOpen(session, "tom_agent"));

    }

//    @DisplayName("check null collections after initialisation")
//    @Test
//    void onMessage() throws IOException, EncodeException {
//        Session session = Mockito.mock(Session.class);
//        Mockito.when(session.getBasicRemote()).thenThrow(new NotNowException());
//        Message message = Mockito.mock(Message.class);
//        chatEndpoint.role = "agent";
//        Map<Session,Session> map = Mockito.mock(HashMap.class);
//        Mockito.when(map.get(Mockito.anyObject())).thenReturn(session);
//        ChatEndpoint.agentClient = map;
//        assertThrows(NotNowException.class, () -> chatEndpoint.OnMessage(session, message));
//    }

    @DisplayName("check /leave option")
    @Test
    void onMessageLeave() throws IOException, EncodeException {
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.getBasicRemote()).thenThrow(new NotNowException());
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getText()).thenReturn("/leave");
        chatEndpoint.OnMessage(session, message);
        assertTrue(chatEndpoint.session == null);

    }

}



