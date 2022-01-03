package ClassWatcher.discordbot.command;

import ClassWatcher.App;
import ClassWatcher.Checker;
import ClassWatcher.discordbot.command.base.Command;
import ClassWatcher.discordbot.command.base.CommandEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;

@Slf4j
public class Section extends Command {
    private static final String NAME = "section";
    public static final String SHORT_HELP_DESC = "Pings user when a specific section has open seats";
    public static final String LONG_HELP_DESC = "Usage: section <Course Initials> <Course Number> <Section Number> <Semester Name>";

    public Section() {
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
        String termName = words.get(4) + " " + words.get(5);
        HashSet<String> termNames = App.REQUEST_FACTORY.getTermNames();
        if (termNames.contains(termName)) {
            Checker request = new Checker(toHash.hashCode() + "", words.get(1), words.get(2), Integer.valueOf(words.get(3)), termName, App.DISCORD_BOT.getNotificationUtil());
            App.DISCORD_BOT.getNotificationUtil().saveRequest(userID, guildId, request);
            log.info(userID + " requested " + words.get(1) + " " + words.get(2));
            e.getOrigEvent().getChannel().sendMessage("Request successful").queue();
        }
        else {
            StringBuilder errorMsg = new StringBuilder("Invalid term name! Valid ones are: \n");
            termNames.forEach(t -> {
                errorMsg.append(t + '\n');
            });
            e.getOrigEvent().getChannel().sendMessage(errorMsg.toString()).queue();
        }
    }
}
