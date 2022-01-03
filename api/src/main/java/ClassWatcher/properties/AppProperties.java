package ClassWatcher.properties;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class AppProperties extends Properties {
    private final HashMap<String, String> propertyDefaults;

    public AppProperties() {
        super();
        propertyDefaults = Maps.newHashMap();
        propertyDefaults.put("enable_discord", "false");
        propertyDefaults.put("discord_bot_token", "");
        propertyDefaults.put("discord_slow_mode", "false");
        propertyDefaults.put("username", "");
        propertyDefaults.put("password", "");
        propertyDefaults.put("auth_url", "http://127.0.0.1:8080/api");
        propertyDefaults.put("api_url", "https://fsu.collegescheduler.com/api");
    }

    public void updateProperties() {
        this.propertyDefaults.keySet().forEach(s -> {
            if (!this.containsKey(s)) {
                log.warn("Adding " + s + " to app.properties");
                this.put(s, propertyDefaults.get(s));
            }
        });
    }


}
