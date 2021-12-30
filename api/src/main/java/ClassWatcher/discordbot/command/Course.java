package ClassWatcher.discordbot.command;

import ClassWatcher.App;
import ClassWatcher.Checker;
import ClassWatcher.RequestFactory;
import ClassWatcher.discordbot.command.base.Command;
import ClassWatcher.discordbot.command.base.CommandEvent;
import ClassWatcher.discordbot.command.base.CommandUtil;
import ClassWatcher.discordbot.command.base.reaction.ReactionCommand;
import ClassWatcher.discordbot.command.base.reaction.ReactionEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class Course extends Command {
    private static final String NAME = "course";
    public Course() {
        super();
        this.addValidArgNum(5);
        this.setCommandName(NAME);
    }

    @Override
    public void executeCommand(CommandEvent e) {
        super.executeCommand(e);
        String userID = e.getOrigEvent().getAuthor().getId();
        String guildId = e.getOrigEvent().getGuild().getId();
        ArrayList<String> words = e.getWords();
        String toHash = words.get(0) + words.get(1) + userID;
        Checker request = new Checker(toHash.hashCode() + "", words.get(1), words.get(2), Integer.valueOf(words.get(3)), words.get(4) + " " + words.get(5), App.DISCORD_BOT.getNotificationUtil());
        App.DISCORD_BOT.getNotificationUtil().saveRequest(userID, guildId, request);
        log.info(userID + " requested " + words.get(1) + " " + words.get(2));
    }
}
