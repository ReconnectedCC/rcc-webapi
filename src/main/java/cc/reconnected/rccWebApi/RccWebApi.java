package cc.reconnected.rccWebApi;

import cc.reconnected.library.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RccWebApi implements ModInitializer {
    public static final String MOD_ID = "rcc-webapi";
    public static RccWebApiConfig CONFIG;
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final ConcurrentLinkedQueue<Runnable> tickQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void onInitialize() {
        try {
            loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpApiServer.register();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickQueue.forEach(Runnable::run);
            tickQueue.clear();
        });
    }

    public static void loadConfig() throws IOException {
        CONFIG = ConfigManager.load(RccWebApiConfig.class);
    }

    public static void schedule(Runnable runnable) {
        tickQueue.add(runnable);
    }
}
