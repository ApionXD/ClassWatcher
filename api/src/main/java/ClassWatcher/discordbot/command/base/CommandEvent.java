package ClassWatcher.discordbot.command.base;

import ClassWatcher.App;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.StringTokenizer;

@Getter
@Slf4j
public class CommandEvent {
    private final ArrayList<String> words;
    private final GuildMessageReceivedEvent origEvent;
    private final Command commandCalled;


    public CommandEvent(GuildMessageReceivedEvent event, String prefix) {
        String messageContents = event.getMessage().getContentRaw();
        String commandName = "";

        origEvent = event;
        messageContents = messageContents.substring(prefix.length());
        words = Lists.newArrayList();

        new StringTokenizer(messageContents).asIterator().forEachRemaining(t -> words.add((String) t));
        commandName = words.get(0).toLowerCase();
        log.info("Running " + commandName);
        commandCalled = App.DISCORD_BOT.getCommandUtil().getCommandFromName(commandName);
    }

    public void runCommand() {
        commandCalled.executeCommand(this);
    }
}
