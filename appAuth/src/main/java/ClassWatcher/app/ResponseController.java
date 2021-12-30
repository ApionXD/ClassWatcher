package ClassWatcher.app;

import ClassWatcher.app.properties.AppProperties;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
@RestController
public class ResponseController {
    public static Logger log = LoggerFactory.getLogger(ResponseController.class);

    private final HashMap<String, Future<Set<Cookie>>> pendingRequests;
    public ResponseController() {
        pendingRequests = new HashMap<String, Future<Set<Cookie>>>();
    }
    @PostMapping(value = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCookiesForUser(@RequestBody String request, LoginService loginService, Gson gson) throws ExecutionException, InterruptedException {
        JsonObject requestObj = gson.fromJson(request, JsonObject.class);
        JsonObject responseObj = new JsonObject();

        final String username = requestObj.get("username").getAsString();
        final String password = requestObj.get("password").getAsString();

        Future<Set<Cookie>> future = loginService.logIn(username, password);
        ResponseEntity<String> result;
        if (future.isDone()) {
            log.info("Future finished very quick, was probably cached.");
            future.get().forEach(c -> {
                responseObj.addProperty(c.getName(), c.getValue());
            });
            result = new ResponseEntity<String>(responseObj.toString(), HttpStatus.OK);
        }
        else {
            String id = String.valueOf(future.hashCode());
            responseObj.add("status", new JsonPrimitive("accepted"));
            responseObj.add("id", new JsonPrimitive(id));
            log.info("Added request with id " + id);
            result = new ResponseEntity<String>(responseObj.toString(), HttpStatus.ACCEPTED);
            pendingRequests.put(id + "", future);
        }
        return result;
    }
    @PostMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkRequest(@RequestBody String request, Gson gson) throws ExecutionException, InterruptedException {
        JsonObject requestObj = gson.fromJson(request, JsonObject.class);
        gson.fromJson(request, JsonElement.class);
        JsonObject responseObj = new JsonObject();

        final String id = requestObj.get("id").getAsString();

        log.info("Checking for id " + id);
        Future<Set<Cookie>> future = pendingRequests.get(id);
        if (future == null) {
            log.info("ID " + id + " not found");
            responseObj.add("code", new JsonPrimitive(404));
            responseObj.add("message", new JsonPrimitive("404 Not Found"));
            return new ResponseEntity<String>(responseObj.toString(), HttpStatus.NOT_FOUND);
        }
        else if (future.isDone()) {
            log.info("ID " + id + " is done");
            responseObj.add("code", new JsonPrimitive(200));
            Set<Cookie> cookies = future.get();
            cookies.forEach(c -> responseObj.addProperty(c.getName(), c.getValue()));
            return new ResponseEntity<String>(responseObj.toString(), HttpStatus.OK);
        }
        else {
            log.info("ID " + id + " not done yet");
            responseObj.add("status", new JsonPrimitive("processing"));

            return new ResponseEntity<String>(responseObj.toString(), HttpStatus.ACCEPTED);
        }
    }
}
