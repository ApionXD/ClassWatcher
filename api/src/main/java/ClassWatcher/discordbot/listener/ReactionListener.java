package ClassWatcher.discordbot.listener;

import ClassWatcher.App;
import ClassWatcher.discordbot.command.base.reaction.ReactionEvent;
import ClassWatcher.discordbot.command.base.reaction.ReactionUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ReactionListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        super.onGuildMessageReactionAdd(e);
        String messageID = e.getMessageId();
        ReactionUtil util = App.DISCORD_BOT.getReactionUtil();
        if (util.isListening(messageID)) {
            log.debug("Got reaction on listened message");
            MessageReaction reaction = e.getReaction();
            if (e.getUser().isBot()) {
                log.debug("Reaction is from bot, ignoring");
            } else {
                ReactionEvent event = new ReactionEvent(e);
                event.runReactionCommand();
            }
        }

    }
}
