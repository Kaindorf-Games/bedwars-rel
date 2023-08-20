package at.kaindorf.games;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MinecraftWebSocketServer extends WebSocketServer {

    public MinecraftWebSocketServer(InetSocketAddress address) {
        super(address);
        setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        webSocket.send("Hi cool");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.err.println(e.getMessage());
    }

    @Override
    public void onStart() {

    }

    private static WebSocketThread wsThread;

    public static void startServer() {
        startServer("0.0.0.0", 8025);
    }

    public static void startServer(String host, int port) {
        MinecraftWebSocketServer ws = new MinecraftWebSocketServer(new InetSocketAddress(host, port));
        wsThread = new WebSocketThread(ws);
        wsThread.start();
    }

    public static void stopServer() {
        if(wsThread != null) {
            wsThread.stop();
        }
    }
}
