package net.jaju.subservermod.randombox;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.jaju.subservermod.Subservermod;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID)
public class RandomBoxManager {
    private static final String FILE_PATH = "config/random_boxes.json";
    private static final Gson gson = new Gson();
    private static final Map<String, RandomBox> randomBoxes = new HashMap<>();

    static  {
        loadRandomBoxes();
    }

    public static void loadRandomBoxes() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<String, List<JsonObject>>>() {}.getType();
            Map<String, List<JsonObject>> loadedRandomBoxes = gson.fromJson(reader, type);
            if (loadedRandomBoxes != null) {
                for (Map.Entry<String, List<JsonObject>> entry : loadedRandomBoxes.entrySet()) {
                    randomBoxes.put(entry.getKey(), RandomBox.fromSerialized(entry.getKey(), entry.getValue()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRandomBoxes() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Map<String, List<JsonObject>> serializedRandomBoxes = new HashMap<>();
            for (Map.Entry<String, RandomBox> entry : randomBoxes.entrySet()) {
                serializedRandomBoxes.put(entry.getKey(), entry.getValue().getSerializedItems());
            }
            gson.toJson(serializedRandomBoxes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, RandomBox> getRandomBoxes() {
        return randomBoxes;
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        saveRandomBoxes();
    }
}
