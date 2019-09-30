package endpoints;

import coders.MessageDecoder;
import coders.MessageEncoder;
import entities.Message;
import lombok.extern.log4j.Log4j;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j
@ServerEndpoint(value = "/chat/{user}", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEndpoint {

    static Queue<Session> sessionListAg = new ConcurrentLinkedQueue<>();
    static Queue<Session> sessionListCl = new ConcurrentLinkedQueue();
    static Map<Session, List<Message>> mapOfMsg = new ConcurrentHashMap<>();
    static Map<Session, Session> agentClient = new ConcurrentHashMap<>();

    Boolean leave = false;
    String usern = "anonymous";
    Session session = null;
    String role = "anonymous";


    @OnOpen
    public void OnOpen(Session session, @PathParam("user") String username) {
        this.usern = username.split("_")[0];
        this.role = username.split("_")[1];
        this.session = session;
        log.debug(usern + " " + role + " connected");
        if (role.equals("agent")) {
            agentProcess(session);
        } else if (role.equals("client")) {
            clientProcess(session);
        }

    }

    private void clientProcess(Session session) {
        sessionListCl.add(session);
        if (sessionListAg.size() > 0) {
            agentClient.put(sessionListAg.poll(), session);
            sessionListCl.remove(session);
        }
    }

    private void agentProcess(Session session) {
        sessionListAg.add(session);
        if (sessionListCl.size() > 0) {
            Session sessionClient = sessionListCl.poll();
            agentClient.put(session, sessionClient);
            sessionListAg.remove(session);
            try {
                if (mapOfMsg.containsKey(sessionClient)) {
                    List<Message> listOfMess = mapOfMsg.get(sessionClient);
                    for (int i = 0; i < listOfMess.size(); i++) {
                        session.getBasicRemote().sendObject(listOfMess.get(i));
                        mapOfMsg.remove(sessionClient);
                    }
                }
            } catch (IOException | EncodeException e) {
                log.error(e + " while resolving agent`s session and sending messages to this session");
            }


        }
    }


    @OnClose
    public void OnClose(Session session) {
        mapOfMsg.remove(session);
        if (role.equals("agent")) {
            agentClose(session);
        } else if (role.equals("client")) {
            clientClose(session);
        }
        this.session = null;
        log.debug(usern + " " + role + " close connection");

    }

    private void clientClose(Session session) {
        Set<Map.Entry<Session, Session>> entrySet = agentClient.entrySet();
        for (Map.Entry<Session, Session> map : entrySet) {
            if (session.equals(map.getValue())) {
                Session sessionAgent = map.getKey();
                if (sessionAgent != null) {
                    sessionListAg.add(sessionAgent);
                }
                agentClient.remove(sessionAgent);
            }
        }
    }

    private void agentClose(Session session) {
        Session sessionClient = agentClient.get(session);
        if (sessionClient != null) {
            sessionListCl.add(sessionClient);
        }
        agentClient.remove(session);
    }

    @OnError
    public void OnError(Session session, Throwable throwable) {
        log.error(throwable.getCause() + " cause " + throwable.getMessage() + " Exception in WS");
    }

    @OnMessage
    public void OnMessage(Session session, Message msg) {
        log.info(msg + " from " + usern);
        try {
            if (leave) {
                leave = false;
                if (role.equals("agent")) {
                    agentProcess(session);
                } else if (role.equals("client")) {
                    clientProcess(session);
                }
            }

            if (msg.getText().contains("/leave")) {
                leave = true;
                if (role.equals("agent")) {
                    agentClose(session);
                } else if (role.equals("client")) {
                    clientClose(session);
                }
                return;
            }

            if (msg.getText().contains("/exit")) {
                OnClose(session);
                return;

            }

            if (role.equals("agent")) {
                agentClient.get(session).getBasicRemote().sendObject(msg);

            } else if (role.equals("client")) {
                if (sendClientMessage(session, msg)) return;
                addMessageToQueue(session, msg);

            }
        } catch (Exception e) {
            log.error(e + " while getting messages or adding it in map");
        }

    }

    private void addMessageToQueue(Session session, Message msg) {
        if (mapOfMsg.containsKey(session)) {
            mapOfMsg.get(session).add(msg);
        } else {
            List<Message> list = new ArrayList<>();
            list.add(msg);
            mapOfMsg.put(session, list);
        }
    }

    private boolean sendClientMessage(Session session, Message msg) throws IOException, EncodeException {
        Set<Map.Entry<Session, Session>> entrySet = agentClient.entrySet();
        for (Map.Entry<Session, Session> map : entrySet) {
            if (session.equals(map.getValue())) {
                map.getKey().getBasicRemote().sendObject(msg);
                return true;
            }
        }
        return false;
    }
}
