package net.jaju.subservermod.randombox;

import com.google.gson.JsonObject;
import net.jaju.subservermod.util.ItemStackSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
                return item.getItem().copy();
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

    public List<JsonObject> getSerializedItems() {
        return items.stream()
                .map(item -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("item", ItemStackSerializer.serialize(item.getItem()));
                    jsonObject.addProperty("chance", item.getChance());
                    return jsonObject;
                })
                .collect(Collectors.toList());
    }

    public static RandomBox fromSerialized(String name, List<JsonObject> serializedItems) {
        RandomBox randomBox = new RandomBox(name);
        for (JsonObject jsonObject : serializedItems) {
            ItemStack itemStack = ItemStackSerializer.deserialize(jsonObject.getAsJsonObject("item"));
            int chance = jsonObject.get("chance").getAsInt();
            randomBox.addItem(itemStack, chance);
        }
        return randomBox;
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
    }
}
