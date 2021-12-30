package ClassWatcher.app.properties;

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
        propertyDefaults.put("browser", "firefox");
        propertyDefaults.put("driver_path", "");
    }

    public void updateProperties() {
        this.propertyDefaults.keySet().forEach(s -> {
            if (!this.containsKey(s)) {
                log.warn("Adding " + s + " to bot.properties");
                this.setProperty(s, propertyDefaults.get(s));
            }
        });
    }


}
