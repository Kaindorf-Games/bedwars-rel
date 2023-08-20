package at.kaindorf.games;

import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketThread implements Runnable {

    private MinecraftWebSocketServer ws;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public WebSocketThread(MinecraftWebSocketServer ws) {
        this.ws = ws;
    }

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        try {
            running.set(true);
            ws.start();
            while (running.get()) {
                Thread.sleep(1000);
            }
            ws.stop();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
