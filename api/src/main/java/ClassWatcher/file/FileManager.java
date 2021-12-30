package ClassWatcher.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileManager {
    public static final FileSystem FS = FileSystems.getDefault();
    public static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().create();

    public static Path openFile(String fileName) throws FileNotFoundException {
        final Path path = FS.getPath(fileName);
        log.debug("Checking " + path + " for " + fileName);
        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }
        return path;
    }

    public static Path createFile(String fileName) throws IOException {
        final Path path = FileSystems.getDefault().getPath(fileName);
        Files.createFile(path);
        return path;
    }

    public static Path forceReadFile(String fileName) {
        Path filePath = FS.getPath(fileName);
        if (Files.isDirectory(filePath)) {
            log.warn(fileName + " is a directory and it shouldn't be! Renaming it to " + fileName + "_dir");
            try {
                Files.move(filePath, openFile(fileName + "_old"));
            } catch (IOException e) {
                log.error("Error moving " + fileName + "! Things may not function properly!");
                log.trace(e.toString());
            }
        }
        if (!Files.exists(filePath)) {
            log.warn(fileName + " doesn't exist! Attempting to create it now!");
            try {
                Files.createDirectories(filePath.subpath(0, filePath.getNameCount() - 1));
                Files.createFile(filePath);
            } catch (IOException e) {
                log.error("Could not create " + fileName + "! This may cause issues!");
                log.debug(e.toString());
            }
        }
        return filePath;
    }
}
