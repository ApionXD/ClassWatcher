package ClassWatcher.discordbot.command.base.reaction;

import ClassWatcher.discordbot.command.base.Command;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.HashMap;

@Setter
@Getter
public abstract class ReactionCommand extends Command {
    private String resultMessageID;
    //Maps Emote String to number of reactions of that emote received
    //Uses codepoints for now
    //Will be an issue if custom emotes are used
    private HashMap<String, Integer> reactionCount;

    protected ReactionCommand() {
        reactionCount = Maps.newHashMap();
    }

    public void handleReactionEvent(ReactionEvent e) {
        MessageReaction.ReactionEmote emote = e.getReaction().getReactionEmote();
        String key = emote.getAsCodepoints();
        if (reactionCount.containsKey(key)) {
            int newCount = reactionCount.get(key) + 1;
            reactionCount.put(key, newCount);
        } else {
            reactionCount.put(key, 1);
        }
    }

}
