package cc.reconnected.rccWebApi;

import cc.reconnected.library.RccLibConfig;
import cc.reconnected.library.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RccWebApi implements ModInitializer {
    public static final String MOD_ID = "rcc-webapi";
    public static RccWebApiConfig CONFIG;
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        try {
            loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpApiServer.register();
    }

    public static void loadConfig() throws IOException {
        CONFIG = ConfigManager.load(RccWebApiConfig.class);
    }
}
