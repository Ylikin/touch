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

    String usern = "anonymous";
    Session session = null;
    String role = "anonymous";


    @OnOpen
    public void OnOpen( Session session, @PathParam("user") String username) {
        this.usern = username.split("_")[0];
        this.role = username.split("_")[1];
        this.session = session;
        log.debug(usern + " " + role + " connected");
        if (role.equals("agent")) {
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

                        }
                    }
                } catch (IOException | EncodeException e) {
                    log.error(e + " while resolving agent`s session and sending messages to this session");
                }


            }
        } else if (role.equals("client")) {
            sessionListCl.add(session);
            if (sessionListAg.size() > 0) {
                agentClient.put(sessionListAg.poll(), session);
                sessionListCl.remove(session);
            }
        }

    }



    @OnClose
    public void OnClose(Session session) {
        sessionListAg.remove(session);
        sessionListCl.remove(session);
        mapOfMsg.clear();
        this.session = null;
        log.debug(usern + " " + role + " close connection");

    }

    @OnError
    public void OnError(Session session, Throwable throwable) {
        log.error(throwable.getCause() + "cause" + throwable.getMessage() + " Exception in WS");
    }

    @OnMessage
    public void OnMessage(Session session, Message msg) {
        log.info(msg + " from " + usern);
        try {
            if (msg.getText().equals("/leave")) {
                OnClose(session);
            }

            if (role.equals("agent")) {
                agentClient.get(session).getBasicRemote().sendObject(msg);

            } else if (role.equals("client")) {
                Set<Map.Entry<Session, Session>> entrySet = agentClient.entrySet();
                for (Map.Entry<Session, Session> map : entrySet) {
                    if (session.equals(map.getValue())) {
                        map.getKey().getBasicRemote().sendObject(msg);
                        return;
                    }
                }
                if (mapOfMsg.containsKey(session)) {
                    mapOfMsg.get(session).add(msg);
                } else {
                    List<Message> list = new ArrayList<>();
                    list.add(msg);
                    mapOfMsg.put(session, list);
                }

            }
        } catch (Exception e) {
            log.error(e + " while getting messages or adding it in map");
        }

    }
}
