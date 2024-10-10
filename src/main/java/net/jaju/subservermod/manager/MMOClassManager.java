package net.jaju.subservermod.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class MMOClassManager {
    private static final Map<UUID, PlayerClassInfo> classDataMap = new HashMap<>();
    private static final String DATA_FILE_PATH = "config/mmoclass_data.json";
    private static final Gson gson = new Gson();
    private static MMOClassManager instance;

    public static MMOClassManager getInstance() {
        if (instance == null) {
            instance = new MMOClassManager();
            instance.loadClassData();
        }
        return instance;
    }

    private MMOClassManager() {}

    public String getPlayerClass(UUID playerUUID) {
        PlayerClassInfo classInfo = classDataMap.getOrDefault(playerUUID, new PlayerClassInfo("None", 0));
        return classInfo.getClassName();
    }

    public int getPlayerLevel(UUID playerUUID) {
        PlayerClassInfo classInfo = classDataMap.getOrDefault(playerUUID, new PlayerClassInfo("None", 0));
        return classInfo.getLevel();
    }

    public void setPlayerClass(UUID playerUUID, String className) {
        PlayerClassInfo classInfo = classDataMap.getOrDefault(playerUUID, new PlayerClassInfo(className, 1));
        classInfo.setClassName(className);
        classDataMap.put(playerUUID, classInfo);
        saveClassData();
    }

    public void setPlayerLevel(UUID playerUUID, int level) {
        PlayerClassInfo classInfo = classDataMap.getOrDefault(playerUUID, new PlayerClassInfo("None", 0));
        classInfo.setLevel(level);
        classDataMap.put(playerUUID, classInfo);
        saveClassData();
    }

    private void saveClassData() {
        try (FileWriter writer = new FileWriter(DATA_FILE_PATH)) {
            gson.toJson(classDataMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClassData() {
        try (FileReader reader = new FileReader(DATA_FILE_PATH)) {
            Type type = new TypeToken<Map<UUID, PlayerClassInfo>>() {}.getType();
            Map<UUID, PlayerClassInfo> data = gson.fromJson(reader, type);
            if (data != null) {
                classDataMap.clear();
                classDataMap.putAll(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        getInstance().saveClassData();
    }
}

class PlayerClassInfo {
    private String className;
    private int level;

    public PlayerClassInfo(String className, int level) {
        this.className = className;
        this.level = level;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
