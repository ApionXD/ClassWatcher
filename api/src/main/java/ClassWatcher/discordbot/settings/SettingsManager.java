package ClassWatcher.discordbot.settings;

import ClassWatcher.App;
import ClassWatcher.file.FileManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Getter
@Slf4j
public class SettingsManager {
    /*
     * Maps guildIDs to settings
     */
    private LoadingCache<String, Settings> settingMap;


    public SettingsManager() {
        initSettings();
        genCache();
        log.debug("Finished initializing SettingsManager!");
    }

    /*
     * Initializes a new cache with 100 maximum entries
     * Maybe reduce number of settings cached?
     *
     */
    private void genCache() {
        log.debug("Generating cache!");
        settingMap = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(new CacheLoader<String, Settings>() {
                    @Override
                    /*
                     * TODO: Write a custom deserializer if needed
                     *  Maybe also check that settings file isn't empty (Should only happen if file is deleted while bot is running)
                     * Fetches Settings from disk
                     * Uses Gson to deserialize json
                     * Settings are saved to the current working directory under the settings folder with the name <Guild Name>.json
                     */
                    public Settings load(String key) throws Exception {
                        log.debug("Guild " + key + " settings is not in cache! Attempting to fetch it.");
                        String fileName = "settings\\" + App.DISCORD_BOT.getJda().getGuildById(key).getName() + ".json";
                        Path filePath = FileManager.forceReadFile(fileName);
                        FileReader reader = new FileReader(fileName);
                        Settings result = FileManager.GSON_INSTANCE.fromJson(reader, Settings.class);
                        return result;
                    }
                });
    }

    //Loops through all guilds, generates missing settings files.
    private void initSettings() {
        //List of guilds visible to the JDA
        List<Guild> guildList = App.DISCORD_BOT.getJda().getGuilds();
        log.info("Found " + guildList.size() + " guilds");
        /*
         * Loops through all guilds and either reads the existing settings file or generates a new default one.
         * TODO: Check if all Settings fields are populated, if not, add defaults
         *  Maybe make a list of fields and check if they exist
         */
        guildList.forEach(g -> {
            Path settingFile = FileManager.forceReadFile("settings\\" + g.getName() + ".json");
            log.info("Settings file for " + g.getName() + "at " + settingFile);
            FileWriter settingsWriter = null;
            try {
                if (Files.readString(settingFile).length() == 0) {
                    try {
                        settingsWriter = new FileWriter(settingFile.toFile());
                        log.warn("Settings file for " + g.getName() + " is empty, generating a default config.");
                        log.warn("Settings file will be at: " + settingFile);
                        log.warn(Files.readString(settingFile));
                        Settings defaultSettings = new Settings();
                        log.debug("Generating complex default settings.");
                        genDefaultComplexSettings(defaultSettings, g.getId());
                        String outputJson = FileManager.GSON_INSTANCE.toJson(defaultSettings);
                        settingsWriter.write(outputJson);
                        settingsWriter.close();
                    } catch (IOException e) {
                        log.error(e.toString());
                    }
                }
            } catch (IOException e) {
                log.error(e.toString());
            }
        });
    }

    /*
     * Gets Settings object from cache
     */
    public Settings getSettingsFromGuildID(String id) {
        try {
            return settingMap.get(id);
        } catch (ExecutionException e) {
            log.error(e.toString());
        }
        return null;
    }

    /*
     * Sets all Settings that need extra initialization (e.g. if it needs values specific to each guild)
     * Right now the only setting set is the channel that the bot listens in
     */
    private void genDefaultComplexSettings(Settings s, String guildID) {
        final String channelID = App.DISCORD_BOT.getJda().getGuildById(guildID).getDefaultChannel().getId();
        log.debug("Default command channel is: " + channelID);
        s.setCommandChannelID(channelID);
    }

    /*
     * Saves settings to disk
     * Should be called whenever settings are updated (e.g. when a command that changes settings is called)
     */
    public void saveSettingsForGuild(String guildID) {
        Settings s;
        StringBuilder fileName = new StringBuilder("settings\\");

        try {
            fileName.append(App.DISCORD_BOT.getJda().getGuildById(guildID).getName());
            fileName.append(".json");
            Path file = FileManager.openFile(fileName.toString());

            s = settingMap.get(guildID);
            log.info("Writing new settings to " + fileName);
            FileWriter writer = new FileWriter(file.toFile());
            //Serializes Settings and writes to file
            FileManager.GSON_INSTANCE.toJson(s, writer);
            writer.close();
            //log.info("Old settings file: " +  Files.readString(file));
        } catch (ExecutionException | IOException exception) {
            log.error(exception.getMessage());
        }
    }
}
