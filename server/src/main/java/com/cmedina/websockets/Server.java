package com.cmedina.websockets;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server extends WebSocketServer {

    private static final int PORT = 8080;
    private final Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    public Server() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String path = handshake.getResourceDescriptor();
        if (!path.equals("/input")) {
            conn.close(1003, "Only /input is supported");
            return;
        }
        clients.add(conn);
        System.out.println("[OPEN]  " + conn.getRemoteSocketAddress() + " connected on " + path
            + "  (clients: " + clients.size() + ")");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("[CLOSE] " + conn.getRemoteSocketAddress()
            + "  code=" + code + "  reason=" + reason
            + "  (clients: " + clients.size() + ")");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("[MSG]   " + conn.getRemoteSocketAddress() + " → " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("[ERROR] " + (conn != null ? conn.getRemoteSocketAddress() : "server") + " : " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("[START] Echo server listening on ws://localhost:" + PORT + "/input");
        setConnectionLostTimeout(30);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        server.start();
    }
}