package ClassWatcher.discordbot.listener;

import ClassWatcher.App;
import ClassWatcher.discordbot.command.base.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MessageListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        String guildID = event.getGuild().getId();
        log.debug("Got message in guild: " + guildID);
        //log.debug("Settings: " + MusicBot.musicBot.getSettingsManager().toString());
        String guildPrefix = App.DISCORD_BOT.getSettingsManager().getSettingsFromGuildID(guildID).getPrefix();
        String messageContents = event.getMessage().toString();

        if (event.getMessage().getContentRaw().startsWith(guildPrefix)) {
            new CommandEvent(event, guildPrefix).runCommand();
        }

    }
}
