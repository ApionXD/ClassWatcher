package ClassWatcher.discordbot.command.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BasicError extends Command {

    @Override
    public void executeCommand(CommandEvent e) {
        EmbedBuilder errorMsgBuilder = new EmbedBuilder();
        MessageEmbed errorMsg = errorMsgBuilder.setDescription("The command you tried to run doesn't seem to exist. Contact the developer if you believe this is an error.")
                .build();
        e.getOrigEvent().getChannel().sendMessage(errorMsg).queue();
    }
}
