package ClassWatcher.discordbot.command;

import ClassWatcher.App;
import ClassWatcher.discordbot.command.base.Command;
import ClassWatcher.discordbot.command.base.CommandEvent;
import ClassWatcher.discordbot.command.base.CommandUtil;
import ClassWatcher.discordbot.settings.Settings;
import ClassWatcher.discordbot.settings.SettingsManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

@Slf4j
public class SetPrefix extends Command {
    private static final String NAME = "setprefix";
    public static final String SHORT_HELP_DESC = "Sets the prefix to invoke the bot";
    public static final String LONG_HELP_DESC = "Sets the prefix to invoke the bot";
    public SetPrefix() {
        this.setCommandName(NAME);
        this.addValidArgNum(1);
        this.setShortHelpDesc(SHORT_HELP_DESC);
        this.setLongHelpDesc(LONG_HELP_DESC);
    }
    @Override
    public void executeCommand(CommandEvent e) {
        final TextChannel origChannel = e.getOrigEvent().getChannel();
        final String newPrefix = e.getWords().get(1);
        final String guildID = e.getOrigEvent().getGuild().getId();
        final Settings s = App.DISCORD_BOT.getSettingsManager().getSettingsFromGuildID(e.getOrigEvent().getGuild().getId());
        log.info("Changing prefix for guild " + guildID + " to " + newPrefix);
        s.setPrefix(newPrefix);
        App.DISCORD_BOT.getSettingsManager().saveSettingsForGuild(guildID);
        boolean success = s.getPrefix().equals(newPrefix);
        EmbedBuilder builder = new EmbedBuilder(CommandUtil.BASE_EMBED);
        if (success) {
            builder.addField("Successfully set prefix to " + newPrefix, "", false);
        }
        else {
            builder.addField("Failed to set prefix!", "", false);
        }
        origChannel.sendMessage(builder.build()).queue();
    }
}
