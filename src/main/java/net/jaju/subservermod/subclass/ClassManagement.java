package net.jaju.subservermod.subclass;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ClassManagement {
    private static final String FILE_PATH = "config/class_data.json";
    private static final Map<String, Map<String, BaseClass>> classMap = new HashMap<>();

    static {
        loadClassData();
    }

    public static void addClass(String playerName, BaseClass playerClass) {
        classMap.computeIfAbsent(playerName, k -> new HashMap<>()).put(playerClass.getName(), playerClass);
        saveClassData();
    }

    public static void setClass(String playerName, BaseClass playerClass) {
        if (classMap.containsKey(playerName)) {
            classMap.get(playerName).put(playerClass.getName(), playerClass);
            saveClassData();
        }
    }

    public static Map<String, BaseClass> getClasses(String playerName) {
        return classMap.getOrDefault(playerName, new HashMap<>());
    }

    public static void saveClassData() {
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseClass.class, new BaseClassTypeAdapter()).setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(classMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadClassData() {
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseClass.class, new BaseClassTypeAdapter()).create();
        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            Type type = new TypeToken<Map<String, Map<String, BaseClass>>>() {}.getType();
            Map<String, Map<String, BaseClass>> data = gson.fromJson(reader, type);
            if (data != null) {
                classMap.putAll(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Map<String, BaseClass>> getClassMap() {
        return classMap;
    }
}