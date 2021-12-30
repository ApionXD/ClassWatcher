package ClassWatcher.discordbot.command.base.reaction;

import ClassWatcher.discordbot.command.base.CommandUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ReactionUtil extends CommandUtil {
    //List of Message IDs whose reactions we are listening for
    private final ArrayList<String> reactionMessages;
    //Maps message IDs to ReactionCommands
    private final LoadingCache<String, ReactionCommand> reactionCommandCache;

    public ReactionUtil() {
        super();
        reactionMessages = Lists.newArrayList();
        reactionCommandCache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build(new CacheLoader<String, ReactionCommand>() {
                    @Override
                    public ReactionCommand load(String key) throws Exception {
                        return null;
                    }
                });
    }

    public void addReactionMessage(String messageID, ReactionCommand c) {
        reactionMessages.add(messageID);
        reactionCommandCache.put(messageID, c);
    }

    public boolean isListening(String messageID) {
        return reactionMessages.contains(messageID);
    }

    public ReactionCommand getReactionCommandFromID(String messageID) {
        try {
            return reactionCommandCache.get(messageID);
        } catch (ExecutionException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void removeReactionMessage(String messageID) {
        reactionMessages.remove(messageID);
        reactionCommandCache.invalidate(messageID);
    }
}
