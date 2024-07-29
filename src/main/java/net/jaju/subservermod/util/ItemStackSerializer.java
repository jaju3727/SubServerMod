package net.jaju.subservermod.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ItemStackSerializer {
    public static JsonObject serialize(ItemStack itemStack) {
        CompoundTag tag = itemStack.serializeNBT();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString());
        jsonObject.addProperty("count", itemStack.getCount());
        jsonObject.add("tag", JsonParser.parseString(tag.toString()));
        itemStack.getEnchantmentTags();
        return jsonObject;
    }

    public static ItemStack deserialize(JsonObject jsonObject) {
        ResourceLocation itemResourceLocation = new ResourceLocation(jsonObject.get("id").getAsString());
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)), jsonObject.get("count").getAsInt());

        if (jsonObject.has("tag")) {
            try {
                CompoundTag tag = TagParser.parseTag(jsonObject.get("tag").toString());
                itemStack.deserializeNBT(tag);

                if (tag.contains("tag")) {
                    CompoundTag innerTag = tag.getCompound("tag");
                    if (itemStack.getItem() == Items.ENCHANTED_BOOK && innerTag.contains("StoredEnchantments")) {
                        ListTag storedEnchantments = innerTag.getList("StoredEnchantments", 10);
                        for (int i = 0; i < storedEnchantments.size(); i++) {
                            CompoundTag enchantmentTag = storedEnchantments.getCompound(i);
                            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentTag.getString("id")));
                            short level = Short.parseShort(enchantmentTag.getString("lvl").replace("s", ""));
                            if (enchantment != null) {
                                EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, level));
                            }
                        }
                    }
                    if (innerTag.contains("Enchantments")) {
                        ListTag enchantments = innerTag.getList("Enchantments", 10);
                        removeAllEnchantments(itemStack);
                        for (int i = 0; i < enchantments.size(); i++) {
                            CompoundTag enchantmentTag = enchantments.getCompound(i);
                            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentTag.getString("id")));
                            short level = Short.parseShort(enchantmentTag.getString("lvl").replace("s", ""));
                            if (enchantment != null) {
                                itemStack.enchant(enchantment, level);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return itemStack;
    }

    public static void removeAllEnchantments(ItemStack itemStack) {
        // 아이템 스택의 태그를 가져옵니다.
        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            // 인첸트 태그가 존재하면 이를 제거합니다.
            if (tag.contains("Enchantments")) {
                tag.remove("Enchantments");
            }
            // 특정 아이템의 경우 추가적으로 내부 태그에서도 제거합니다.
            if (itemStack.getItem() == Items.ENCHANTED_BOOK) {
                if (tag.contains("StoredEnchantments")) {
                    tag.remove("StoredEnchantments");
                }
            }
            // 수정된 태그를 아이템 스택에 설정합니다.
            itemStack.setTag(tag);
        }
    }
}