package net.jaju.subservermod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBox {
    private final String name;
    private final List<RandomBoxItem> items;
    private final Random random;

    public RandomBox(String name) {
        this.name = name;
        this.items = new ArrayList<>();
        this.random = new Random();
    }

    public void addItem(ItemStack item, int chance) {
        items.add(new RandomBoxItem(item, chance));
    }

    public ItemStack getRandomItem() {
        int totalWeight = items.stream().mapToInt(RandomBoxItem::getChance).sum();
        int randomValue = random.nextInt(totalWeight);

        for (RandomBoxItem item : items) {
            randomValue -= item.getChance();
            if (randomValue < 0) {
                return item.getItem();
            }
        }
        return ItemStack.EMPTY;
    }

    public String getName() {
        return name;
    }

    public List<RandomBoxItem> getItems() {
        return items;
    }

    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        JsonArray jsonArray = new JsonArray();
        for (RandomBoxItem item : items) {
            jsonArray.add(item.serialize());
        }
        jsonObject.add("items", jsonArray);
        return jsonObject;
    }

    public static RandomBox deserialize(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        RandomBox box = new RandomBox(name);
        JsonArray jsonArray = jsonObject.getAsJsonArray("items");
        for (JsonElement element : jsonArray) {
            box.items.add(RandomBoxItem.deserialize(element.getAsJsonObject()));
        }
        return box;
    }

    public static class RandomBoxItem {
        private final ItemStack item;
        private final int chance;

        public RandomBoxItem(ItemStack item, int chance) {
            this.item = item;
            this.chance = chance;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getChance() {
            return chance;
        }

        public JsonObject serialize() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", ItemStackSerializer.serialize(item));
            jsonObject.addProperty("chance", chance);
            return jsonObject;
        }

        public static RandomBoxItem deserialize(JsonObject jsonObject) {
            ItemStack item = ItemStackSerializer.deserialize(jsonObject.getAsJsonObject("item"));
            int chance = jsonObject.get("chance").getAsInt();
            return new RandomBoxItem(item, chance);
        }
    }
}
