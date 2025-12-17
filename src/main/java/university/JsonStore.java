package university;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

final class JsonStore {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public void save(Path path, Snapshot snap) {
        Snapshot safe = Snapshot.normalizedCopyOf(snap); // defensive copy on boundary
        try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(safe, w);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save JSON: " + e.getMessage(), e);
        }
    }

    public Snapshot load(Path path) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Snapshot snap = gson.fromJson(r, Snapshot.class);
            if (snap == null) snap = new Snapshot();
            return snap.normalizedCopy(); // normalize + defensive copy
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON: " + e.getMessage(), e);
        }
    }
}
