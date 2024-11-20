package cc.reconnected.rccWebApi;

import cc.reconnected.library.config.Config;

@Config(RccWebApi.MOD_ID)
public class RccWebApiConfig {
    public boolean enableHttpApi = true;
    public int httpPort = 25581;
}
