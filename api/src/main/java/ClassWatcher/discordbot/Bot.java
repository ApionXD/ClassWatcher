package ClassWatcher.discordbot;

import ClassWatcher.discordbot.command.Section;
import ClassWatcher.discordbot.command.Help;
import ClassWatcher.discordbot.command.SetPrefix;
import ClassWatcher.discordbot.command.base.CommandUtil;
import ClassWatcher.discordbot.command.base.reaction.ReactionUtil;
import ClassWatcher.discordbot.listener.MessageListener;
import ClassWatcher.discordbot.listener.ReactionListener;
import ClassWatcher.discordbot.settings.SettingsManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

@Slf4j @Getter
/*
 * An instance of this class represents a single bot account
 * Will eventually be used for sharding
 */
public class Bot {
    /*
     * The settings manager for this bot instance
     * Manages all settings for registered guilds, including caching and reading/saving to disk
     */
    private SettingsManager settingsManager;
    /*
     * The command utility for this bot instance
     * Caches and registers commands
     */
    private CommandUtil commandUtil;
    /*
     * Stores message IDs of commands we want to listen for reactions
     */
    private ReactionUtil reactionUtil;
    /*
     *
     */
    private NotificationUtil notificationUtil;
    /*
     * The Discord Bot Account token for this bot instance
     * Needed to init JDA
     */
    private final String botToken;
    /*
     * A boolean representing whether or not to construct a "light" JDA instance
     */
    private final boolean setLight;
    /*
     * The JDA instance for this bot
     * TODO: Maybe put this in main class so that when sharding is implemented there is still only one JDA instance
     */
    private JDA jda;

    public Bot(String token, boolean slowMode) {
        botToken = token;
        //Reads whether ot not to construct a "light" JDA
        setLight = slowMode;
        //Builds the JDA, exits if token invalid.
        try {
            if (setLight) {
                log.info("slowMode flag set in properties, launching in slow mode");
                jda = JDABuilder.createLight(botToken).build();
            }
            else {
                jda = JDABuilder.createDefault(botToken).build();
            }
        }
        catch (LoginException e) {
            log.error("Token not valid! Please check your bot.properties file!");
            System.exit(1);
        }
        //Waits for JDA to be ready, exits on error
        try {
            jda.awaitReady();
            log.info("JDA is ready!");
        }
        catch (InterruptedException e) {
            log.error(e.toString());
            log.debug("Error waiting for JDA to be ready.");
            System.exit(1);
        }
    }
    /*
     * Adds all utilities, listeners, managers, and commands.
     * This method is necessary as constructors in some managers require access to a JDA instance to retrieve data
     */
    public void addUtil(){
        jda.addEventListener(new MessageListener());
        jda.addEventListener(new ReactionListener());
        settingsManager = new SettingsManager();
        commandUtil = new CommandUtil();
        reactionUtil = new ReactionUtil();
        notificationUtil = new NotificationUtil();

        commandUtil.addCommands(new SetPrefix());
        commandUtil.addCommands(new Section());

        reactionUtil.addCommands(new Help());
    }
}
