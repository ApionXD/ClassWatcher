package ClassWatcher.discordbot.command.base.reaction.paged;

import ClassWatcher.App;
import ClassWatcher.discordbot.command.base.CommandEvent;
import ClassWatcher.discordbot.command.base.reaction.ReactionCommand;
import ClassWatcher.discordbot.command.base.reaction.ReactionEvent;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class PaginatedCommand extends ReactionCommand {
    protected static String LEFT_ARROW = "U+2b05";
    protected static String RIGHT_ARROW = "U+27a1";
    @Getter
    private List<MessageEmbed> pages;
    int curPage = 0;

    public PaginatedCommand() {
        pages = Lists.newArrayList();
    }

    public void addPage(MessageEmbed page) {
        pages.add(page);
    }

    @Override
    public void executeCommand(CommandEvent e) {
        super.executeCommand(e);
        pages = Lists.newArrayList();
    }

    @Override
    public void handleReactionEvent(ReactionEvent e) {
        String reaction = e.getReaction().getReactionEmote().getAsCodepoints();
        if (reaction.equals(LEFT_ARROW) && curPage != 0) {
            curPage--;
        }
        if (reaction.equals(RIGHT_ARROW) && curPage < pages.size()) {
            curPage++;
        }
        Message message = e.getOrigEvent().retrieveMessage().complete();
        message.clearReactions().complete();
        message.editMessage(pages.get(curPage)).complete();
        if (curPage != 0) {
            message.addReaction(LEFT_ARROW).queue();
        }
        if (curPage != pages.size() - 1) {
            message.addReaction(RIGHT_ARROW).queue();
        }
    }

    public void printFirstPage(CommandEvent e) {
        Message message = e.getOrigEvent().getChannel().sendMessage(getPages().get(0)).complete();
        if (getPages().size() > 1) {
            message.addReaction(RIGHT_ARROW).queue();
        }
        String messageID = message.getId();
        App.DISCORD_BOT.getReactionUtil().addReactionMessage(messageID, (ReactionCommand) e.getCommandCalled());
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                App.DISCORD_BOT.getReactionUtil().removeReactionMessage(messageID);
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task, 30000);
    }
}
