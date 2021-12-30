package ClassWatcher.discordbot;

import ClassWatcher.App;
import ClassWatcher.Checker;
import ClassWatcher.NotificationMethod;
import ClassWatcher.discordbot.command.base.CommandUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;

public class NotificationUtil implements NotificationMethod {
    private HashMap<String, String> notifToGuildMap;
    private HashMap<String, String> notifToUserMap;
    private HashMap<String, Checker> checkerMap;
    public NotificationUtil() {
        notifToGuildMap = new HashMap<>();
        notifToUserMap = new HashMap<>();
        checkerMap = new HashMap<>();
    }
    public void saveRequest(String userID, String guildID, Checker request) {
        String hash = request.getNotificationID();
        notifToGuildMap.put(hash, guildID);
        notifToUserMap.put(hash, userID);
        checkerMap.put(hash, request);
        App.checkRequests.add(request);
    }

    @Override
    public void sendNotification(String notifID) {

        String guildID = notifToGuildMap.get(notifID);
        notifToGuildMap.remove(notifID);
        String userID = notifToUserMap.get(notifID);
        notifToUserMap.remove(notifID);
        Checker request = checkerMap.get(notifID);
        checkerMap.remove(notifID);
        JDA jdaInstance = App.DISCORD_BOT.getJda();
        Guild g = jdaInstance.getGuildById(guildID);
        String mention = g.getMemberById(userID).getAsMention();
        String channelID = App.DISCORD_BOT.getSettingsManager().getSettingsFromGuildID(guildID).getCommandChannelID();;
        TextChannel channel = (TextChannel) g.getGuildChannelById(channelID);
        channel.sendMessage(mention).queue();
        channel.sendMessage(new EmbedBuilder(CommandUtil.BASE_EMBED).addField("Class Name", request.getCourseCat() + " " + request.getCourseID(), true).addField("Status", "Open", true).addField("Section", String.valueOf(request.getSectionNum()), true).build()).queue();
    }
}
