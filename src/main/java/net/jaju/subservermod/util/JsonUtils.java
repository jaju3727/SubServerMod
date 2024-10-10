package net.jaju.subservermod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_PATH = "config/villages.json";

    public static void saveVillages(Map<String, Village> villages) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            GSON.toJson(villages, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Village> loadVillages() {
        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            Type type = new TypeToken<Map<String, Village>>() {}.getType();
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}