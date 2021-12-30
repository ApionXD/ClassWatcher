package ClassWatcher;

import ClassWatcher.discordbot.Bot;
import ClassWatcher.properties.AppProperties;
import ClassWatcher.properties.PropUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

@Slf4j
public class App {
    public static Bot DISCORD_BOT;
    public static RequestFactory REQUEST_FACTORY;
    public static ArrayList<Checker> checkRequests;
    public static void main(String[] args) {

        AppProperties properties = PropUtil.loadPropertyFile();
        REQUEST_FACTORY = new RequestFactory(properties.getProperty("auth_url"), properties.getProperty("api_url"));

        REQUEST_FACTORY.addUser(properties.getProperty("username"), properties.getProperty("password"));
        if (properties.get("enable_discord").equals("true")) {
            log.info("Enabling Discord Bot");
            DISCORD_BOT = new Bot(properties.getProperty("discord_bot_token"), Boolean.parseBoolean(properties.getProperty("discord_slow_mode")));
            DISCORD_BOT.addUtil();
        }
        checkRequests = Lists.newArrayList();
        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                log.info("Running checks");
                for (Checker c : checkRequests) {
                    try {
                        if (REQUEST_FACTORY.isSectionOpen(c.getTerm(), c.getCourseCat(), c.getCourseID(), c.getSectionNum())) {
                            log.info(c.getCourseCat() + " " + c.getCourseID() + " Section " + c.getSectionNum() + " is open");
                            c.getNotificationMethod().sendNotification(c.getNotificationID());
                        }
                    } catch (IOException | ExecutionException e) {
                        log.error(e.toString());
                    }
                }
            }
        };
        t.schedule(task, 30000, 30000);
    }

}
