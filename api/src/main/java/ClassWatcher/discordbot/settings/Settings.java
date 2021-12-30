package ClassWatcher.discordbot.settings;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Settings {
    /*
     * The prefix to invoke the bot in a Discord channel
     * Commands are only run if they start with this prefix
     */
    private String prefix;
    /*
     * TODO: Add option for bot to listen in all channels
     *   When checking if a Message is in the command channel in MessageListener, also check a boolean in the guild's settings
     * The channel that the bot listens for commands in
     */
    private String commandChannelID;

    /*
     * A constructor for a default settings object
     * This is only called when a server's settings have not been initialized before
     * Initialization of commandChannelID is done in settings manager, as we have to know what guild the settings are for
     * Maybe replace this so a guild ID is passed in here and we get channels from the JDA
     */
    public Settings() {
        prefix = "~";
        commandChannelID = null;
    }
}
