package ClassWatcher.discordbot.command.base;

import ClassWatcher.App;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

@Slf4j
public class CommandUtil {
    private static final Command ERROR_COMMAND = new BasicError();
    public static final MessageEmbed BASE_EMBED = new EmbedBuilder().setColor(255).setFooter("A bot to check course availability").setAuthor("CourseBot").build();
    @Getter
    private final HashSet<Command> commands;
    private final LoadingCache<String, Command> commandCache;

    public CommandUtil() {
        commands = Sets.newHashSet();
        commandCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Command>() {
            @Override
            public Command load(String key) {
                Command result = null;
                Iterator<Command> commandIterator = commands.iterator();
                while (commandIterator.hasNext() && result == null) {
                    Command nextCommand = commandIterator.next();
                    if (nextCommand.getCommandName().equals(key)) {
                        result = nextCommand;
                    }
                }
                if (result == null) {
                    result = App.DISCORD_BOT.getReactionUtil().getCommandFromName(key);
                }
                if (result == null) {
                    result = ERROR_COMMAND;
                }
                return result;
            }
        });
        log.debug("CommandUtil done initializing!");
    }

    public void addCommands(Command c) {
        log.debug("Registered " + c.getCommandName());
        commands.add(c);
    }

    public Command getCommandFromName(String name) {
        try {
            return commandCache.get(name);
        } catch (ExecutionException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
