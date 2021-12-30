package ClassWatcher.discordbot.command.base;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashSet;

@Getter
@Setter
public abstract class Command {
    private String commandName;
    private String shortHelpDesc;
    private String longHelpDesc;
    private HashSet<Integer> validArgNums;

    public Command() {
        validArgNums = Sets.newHashSet();
        shortHelpDesc = "There is no help description set for this command";
        longHelpDesc = "There is no help description set for this command";
    }

    public void executeCommand(CommandEvent e) {
        TextChannel origChannel = e.getOrigEvent().getChannel();
        if (!hasValidArgs(e.getWords().size() - 1)) {
            EmbedBuilder builder = new EmbedBuilder(CommandUtil.BASE_EMBED).addField("You have entered the wrong amount of arguments for " + e.getCommandCalled().commandName, "", false);
            origChannel.sendMessage(builder.build()).queue();
            return;
        }
    }

    public boolean hasValidArgs(int numArgs) {
        if (validArgNums.size() == 0) {
            return true;
        } else {
            return validArgNums.contains(numArgs);
        }
    }

    public void addValidArgNum(int i) {
        validArgNums.add(i);
    }

}
