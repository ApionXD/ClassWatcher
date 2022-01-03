package ClassWatcher;

import ClassWatcher.deserializedobjects.RegBlocks;
import ClassWatcher.deserializedobjects.Section;
import ClassWatcher.deserializedobjects.Term;
import com.google.common.collect.Lists;
import com.google.gson.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
@Slf4j
public class RequestFactory {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String authURL;
    public String apiURL;

    private ArrayList<String> users;
    private HashMap<String, JsonObject> cookieMap;
    private HashMap<String, String> passwordMap;
    @Getter
    private HashSet<String> termNames;

    private OkHttpClient client;

    public RequestFactory(String authLocation, String schedulerURL) {
        apiURL = schedulerURL;
        authURL = authLocation;
        client = new OkHttpClient();
        passwordMap = new HashMap<String, String>();
        cookieMap = new HashMap<>();
        termNames = new HashSet<>();
        users = Lists.newArrayList();
    }
    public void addUser(String username, String pass) {
        log.info("Added " + username);
        users.add(username);
        passwordMap.put(username, pass);

        log.info("Getting cookies for " + username);
        JsonObject requestBody = new JsonObject();
        requestBody.add("username", new JsonPrimitive(username));
        requestBody.add("password", new JsonPrimitive(pass));

        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(authURL + "/login").post(body).build();
        try {
            Response response = client.newCall(request).execute();
            String id = "";
            while (response.code() != 200) {
                log.info("Checking request...");;
                JsonObject obj = GSON.fromJson(response.body().string(), JsonObject.class);
                if (response.code() == 202 && obj.has("id")) {
                    id = obj.get("id").getAsString();
                }
                requestBody = new JsonObject();
                requestBody.add("id", new JsonPrimitive(id));
                body = RequestBody.create(requestBody.toString(), MediaType.get("application/json; charset=utf-8"));
                request = new Request.Builder().url(authURL + "/status").post(body).build();
                response = client.newCall(request).execute();
                Thread.sleep(5000);
            }
            log.info("Response obtained for user " + username);
            cookieMap.put(username, GSON.fromJson(response.body().string(), JsonObject.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshTermNames() throws IOException, ExecutionException {
        termNames = new HashSet<String>();
        String randomUser = "";
        randomUser = users.get((int) (Math.random() * (users.size() - 1)));
        URL url = new URL(apiURL + "/terms");
        StringBuilder cookieText = new StringBuilder();
        cookieMap.get(randomUser).entrySet().forEach(e -> {
            if (!e.getKey().equals("code")) {
                cookieText.append(e.getKey()).append("=").append(e.getValue().getAsString()).append(";");
            }
        });
        log.info(String.format("Cookie: %s", cookieText.toString()));
        Request termRequest = new Request.Builder().url(url).header("Cookie", cookieText.toString()).get().build();
        String jsonString = client.newCall(termRequest).execute().body().string();
        Term[] terms = GSON.fromJson(jsonString, Term[].class);
        for (Term t : terms) {
            String title = t.getTitle();
            termNames.add(title);
        }

    }
    public ArrayList<Section> getSections(String term, String courseInitials, String courseCode) throws IOException, ExecutionException {
        if (termNames.isEmpty()) {
            refreshTermNames();
        }
        if (!termNames.contains(term)) {
            log.error("Term " + term + " is not in the terms list");
            log.error("Valid terms are: ");
            for (String s : termNames) {
                log.error(s);
            }
        }
        String randomUser = "";
        randomUser = users.get((int) (Math.random() * (users.size() - 1)));
        URL url = new URL(apiURL + "/terms/" + term.replaceAll(" ", "%20") + "/subjects/" + courseInitials + "/courses/" + courseCode + "/regblocks");
        StringBuilder cookieText = new StringBuilder();
        cookieMap.get(randomUser).entrySet().forEach(e -> {
            if (!e.getKey().equals("code")) {
                cookieText.append(e.getKey()).append("=").append(e.getValue().getAsString()).append(";");
            }
        });
        Request request = new Request.Builder().url(url).header("Cookie", cookieText.toString()).get().build();
        String jsonString = client.newCall(request).execute().body().string();
        RegBlocks blocks = GSON.fromJson(jsonString, RegBlocks.class);
        return Lists.newArrayList(blocks.getSections());
    }
    public ArrayList<Section> getOpenSections(String term, String courseInitials, String courseCode) throws IOException, ExecutionException {
        ArrayList<Section> sections = getSections(term, courseInitials, courseCode);
        ArrayList<Section> result = Lists.newArrayList();
        for (Section s : sections) {
            if (s.getOpenSeats() != 0) {
                result.add(s);
            }
        }
        return result;
    }
    public boolean isSectionOpen(String term, String courseInitials, String courseCode, int sectionNum) throws IOException, ExecutionException {
        ArrayList<Section> sections = getSections(term, courseInitials, courseCode);
        return sections.get(sectionNum - 1).getOpenSeats() != 0;
    }

}
