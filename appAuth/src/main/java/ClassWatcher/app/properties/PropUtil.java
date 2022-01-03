package ClassWatcher.app.properties;

import ClassWatcher.app.file.FileManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Properties;

@Slf4j
public class PropUtil {
    public static final String PROP_FILE_PATH = "properties/auth.properties";

    //Reads/creates a new properties files and checks that it has all values that are needed.
    @SneakyThrows
    public static AppProperties loadPropertyFile() {
        Path propFile = FileManager.forceReadFile(PROP_FILE_PATH);
        FileReader propReader = new FileReader(propFile.toFile());
        AppProperties properties = new AppProperties();
        properties.load(propReader);
        properties.updateProperties();
        saveProperties(properties, propFile);
        return properties;
    }

    //There isn't actually a way for IOExceptions to be thrown here, they are handled when all property files are read
    //No real reason to use sneakythrows here though, this could be easily wrapped in a try/catch
    @SneakyThrows
    public static void saveProperties(Properties properties, Path propFile) {
        properties.store(new FileWriter(propFile.toFile()), "");
    }
}
