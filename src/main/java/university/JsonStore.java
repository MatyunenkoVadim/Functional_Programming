package university;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

final class JsonStore {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public void save(Path path, Snapshot snap) {
        try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(snap, w);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save JSON: " + e.getMessage(), e);
        }
    }

    public Snapshot load(Path path) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Snapshot snap = gson.fromJson(r, Snapshot.class);
            if (snap == null) snap = new Snapshot();
            // страховка от null-коллекций
            if (snap.students == null) snap.students = new java.util.ArrayList<>();
            if (snap.professors == null) snap.professors = new java.util.ArrayList<>();
            if (snap.courses == null) snap.courses = new java.util.ArrayList<>();
            if (snap.enrollments == null) snap.enrollments = new java.util.ArrayList<>();
            return snap;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON: " + e.getMessage(), e);
        }
    }
}
