package cc.reconnected.rccWebApi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpApiServer {
    private static HttpApiServer instance;

    public static HttpApiServer getInstance() {
        return instance;
    }

    private static float currentTps = 0;
    private static float currentMspt = 0;
    private static int currentPlayerCount = 0;

    public static void register() {
        if (!RccWebApi.CONFIG.enableHttpApi)
            return;

        try {
            instance = new HttpApiServer();
        } catch (IOException e) {
            RccWebApi.LOGGER.error("Could not start HTTP API server", e);
            return;
        }
        new Thread(() -> instance.httpServer().start());

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            RccWebApi.LOGGER.info("Stopping HTTP services");
            instance.httpServer().stop(0);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            currentMspt = server.getTickTime();
            if (currentMspt != 0) {
                currentTps = Math.min(20, 1000 / currentMspt);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            currentPlayerCount = server.getCurrentPlayerCount() + 1;
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            currentPlayerCount = server.getCurrentPlayerCount() - 1;
        });
    }

    private final HttpServer server;

    public HttpServer httpServer() {
        return this.server;
    }

    private HttpApiServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(RccWebApi.CONFIG.httpPort), 0);
        server.createContext("/tps", new TPSHandler());
        server.createContext("/mspt", new MSPTHandler());
        server.createContext("/player", new PlayerCountHandler());
        server.setExecutor(null);
    }

    static class TPSHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            var tps = String.valueOf(currentTps);
            t.sendResponseHeaders(200, tps.length());
            var body = t.getResponseBody();
            body.write(tps.getBytes());
            body.close();
        }
    }

    static class MSPTHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            var tps = String.valueOf(currentTps);
            t.sendResponseHeaders(200, tps.length());
            var body = t.getResponseBody();
            body.write(tps.getBytes());
            body.close();
        }
    }

    static class PlayerCountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            var tps = String.valueOf(currentPlayerCount);
            t.sendResponseHeaders(200, tps.length());
            var body = t.getResponseBody();
            body.write(tps.getBytes());
            body.close();
        }
    }
}
