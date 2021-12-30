package ClassWatcher.app;

import ClassWatcher.app.properties.AppProperties;
import ClassWatcher.app.properties.PropUtil;
import com.beust.jcommander.internal.Sets;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class LoginService {
    public static volatile HashMap<String, Set<Cookie>> cookieMap = new HashMap<>();
    private static volatile AppProperties properties;
    @Autowired
    public LoginService() {
        properties = PropUtil.loadPropertyFile();
    }
    @Async
    public CompletableFuture<Set<Cookie>> logIn(String username, String password) {
        CompletableFuture<Set<Cookie>> result = CompletableFuture.supplyAsync(() -> {
            if (cookieMap.containsKey(username)) {
                return cookieMap.get(username);
            }
            WebDriver driver = null;
            switch (properties.getProperty("browser")) {
                case "firefox":
                    System.setProperty("webdriver.gecko.driver", properties.getProperty("driver_path"));
                    driver = new FirefoxDriver();
                    break;
                case "headless_chrome":
                    break;
                case "chrome":
                    break;
                default:
                    log.error("Invalid browser set in properties!");
                    System.exit(1);
            }
            driver.get("https://fsu.collegescheduler.com");
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.id("fsu-login-button")).click();
            while (!driver.getCurrentUrl().equals("https://fsu.collegescheduler.com/entry")) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Set<Cookie> cookies = (HashSet<Cookie>) driver.manage().getCookies();
            driver.close();
            cookieMap.put(username, cookies);
            return cookies;
        });
        return result;
    }
}
