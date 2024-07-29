package net.jaju.subservermod.encyclopedia.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.encyclopedia.network.ClientPacketHandler;
import net.jaju.subservermod.encyclopedia.network.EncyclopediaPacket;
import net.jaju.subservermod.encyclopedia.network.ItemDiscoveryPacket;
import net.jaju.subservermod.encyclopedia.network.giftGetPacket;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class EncyclopediaScreen extends Screen {
    private Player player;
    private int page = 1;
    private int maxPage;
    private final int minPage = 1;
    private static final int numPerPage = 105;
    private LinkedHashMap<String, Integer> encyclopedia;
    private HashMap<String, Boolean> discoveries;
    private LinkedHashMap<Integer, List<ItemStack>> giftList;
    private LinkedHashMap<Integer, Boolean> giftGet;
    private int gauge;

    public EncyclopediaScreen(Player player) {
        super(Component.literal("EncyclopediaScreen"));
        this.player = player;
        this.encyclopedia = ClientPacketHandler.getEncyclopedia();
        this.discoveries = ClientPacketHandler.getDiscoveries();
        this.giftList = ClientPacketHandler.getGiftList();
        this.giftGet = ClientPacketHandler.getGiftGet();
        for (Boolean bool : discoveries.values()) if (bool) gauge++;
        this.maxPage = (encyclopedia.size() - 1) / numPerPage + 1;
    }

    @Override
    protected void init() {
        initializeWidgets();
    }

    private void initializeWidgets() {
        this.clearWidgets();
        int standardX = 78;
        int standardY = 103 - 15 - 70;
        int intervalX = 22;
        int intervalY = 21;
        int end = page == maxPage ? (encyclopedia.size() - 1) % numPerPage + 1 : numPerPage;
        int start = numPerPage * (page - 1);
        end += start;
        int i = 0;
        for (var entry : encyclopedia.entrySet()) {
            if (i < start) {
                i++;
                continue;
            }
            if (i >= end) break;
            int restI = i % numPerPage;

            boolean flag = discoveries.get(entry.getKey());

            this.addRenderableWidget(new ImageButton(standardX + (restI % 15) * intervalX,
                    standardY + (restI / 15) * intervalY,
                    18, 18, 0, 0, 1,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/encyclopedia/encyclopedia_slot.png"),
                    0, 0, button -> {
                        if (!flag) {
                            System.out.println(entry.getKey()+"  "+entry.getValue());
                            onClickWidget(entry.getKey(), entry.getValue());
                        }
            }));
            i++;
        }


        this.addRenderableWidget(new CustomPlainTextButton(
                standardX + 153, standardY + 155,
                0, 0,
                Component.literal(page+"/"+maxPage),
                button -> {},
                minecraft.font,
                1.0f
        ));

        this.addRenderableWidget(new CustomPlainTextButton(
                standardX + 275, standardY + 186,
                0, 0,
                Component.literal( String.format("%.2f", (float)gauge/(float)encyclopedia.size() * 100) + "% (" + gauge + "/" + encyclopedia.size() + ")"),
                button -> {},
                minecraft.font,
                0.8f
        ));

        if (page != minPage) {
            this.addRenderableWidget(new ImageButton(standardX - 24, standardY + 70,
                    20, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                    20, 10, button -> leftPage()));
        }
        if (page != maxPage) {
            this.addRenderableWidget(new ImageButton(standardX + 328, standardY + 70,
                    20, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                    20, 10, button -> rightPage()));
        }

        int previousVar = -100;
        for (var entry : giftList.entrySet()) {
            int width = 350;
            int height = 170;
            int centerX = (this.width - width) / 2;
            int centerY = (this.height - height) / 2 + 10 + 165;
            if (entry.getKey() - previousVar <= 10) {
                centerY -= 35;
            }
            width = (int) (346*(((float) entry.getKey()/ (float) encyclopedia.size()))) - 10;
            centerX += width;
            if (!giftGet.get(entry.getKey())) {
                this.addRenderableWidget(new ImageButton(centerX, centerY,
                        20, 20, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/encyclopedia/gift_box.png"),
                        20, 20, button -> {
                    onClickGiftWidget(entry.getKey());
                }));
            } else {
                this.addRenderableWidget(new ImageButton(centerX, centerY,
                        20, 20, 0, 0, 0,
                        new ResourceLocation(Subservermod.MOD_ID, "textures/gui/encyclopedia/gift_check.png"),
                        20, 20, button -> {}));
            }
            previousVar = entry.getKey();
        }
    }

    private void onClickGiftWidget(int giftNum) {
        if (giftNum <= gauge) {
            giftGet.replace(giftNum, true);
            ModNetworking.INSTANCE.sendToServer(new giftGetPacket(giftNum));
            initializeWidgets();
        }
    }

    private void onClickWidget(String itemName, int itemCount) {
        ResourceLocation itemResourceLocation = new ResourceLocation(itemName);
        Item item = ForgeRegistries.ITEMS.getValue(itemResourceLocation);
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(item));
        if (itemResourceLocation.toString().contains("goat_horn")) {
            itemStack = new ItemStack(Items.GOAT_HORN);
            CompoundTag tag = itemStack.getOrCreateTag();
            tag.putString("instrument", itemResourceLocation.toString());
            itemStack.setTag(tag);
        } else if (itemResourceLocation.toString().contains("potion")) {
            String key = itemResourceLocation.toString();
            String potionName = key.substring(key.indexOf(':') + 1).replace("_potion", "");
            Potion potionType = Potion.byName(potionName);
            if (potionType != null) {
                itemStack = PotionUtils.setPotion(new ItemStack(Items.POTION), potionType);
            }
        }
        int playerItemCount = player.getInventory().countItem(itemStack.getItem());
        if (playerItemCount >= itemCount) {
            discoveries.replace(itemName, true);
            gauge++;
            ModNetworking.INSTANCE.sendToServer(new ItemDiscoveryPacket(itemName, itemCount));
            initializeWidgets();
        }
    }

    private void rightPage() {
        if (page < maxPage) {
            page++;
            initializeWidgets();
        }
    }

    private void leftPage() {
        if (page > minPage) {
            page--;
            initializeWidgets();
        }
    }

    private void renderShopItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int i = 0;
        float scale = 1.125f;

        int standardX = 78;
        int standardY = 103 - 15 - 70;
        int intervalX = 22;
        int intervalY = 21;

        int end = page == maxPage ? (encyclopedia.size() - 1) % numPerPage + 1 : numPerPage;
        int start = numPerPage * (page - 1);
        end += start;

        for (var entry : encyclopedia.entrySet()) {
            if (i < start) {
                i++;
                continue;
            }
            if (i >= end) break;

            int restI = i % numPerPage;
            ResourceLocation itemResourceLocation = new ResourceLocation(entry.getKey());

            Item item = ForgeRegistries.ITEMS.getValue(itemResourceLocation);
            ItemStack itemStack = new ItemStack(Objects.requireNonNull(item));
            if (itemResourceLocation.toString().contains("goat_horn")) {
                itemStack = new ItemStack(Items.GOAT_HORN);
                CompoundTag tag = itemStack.getOrCreateTag();
                tag.putString("instrument", itemResourceLocation.toString());
                itemStack.setTag(tag);
            } else if (itemResourceLocation.toString().contains("potion")) {
                String key = itemResourceLocation.toString();
                String potionName = key.substring(key.indexOf(':') + 1).replace("_potion", "");
                Potion potionType = Potion.byName(potionName);
                if (potionType != null) {
                    itemStack = PotionUtils.setPotion(new ItemStack(Items.POTION), potionType);
                }
            }
            int xPosition = standardX + (restI % 15) * intervalX;
            int yPosition = standardY + (restI / 15) * intervalY;

            if (mouseX >= xPosition && mouseX <= xPosition + 16 * scale && mouseY >= yPosition && mouseY <= yPosition + 16 * scale) {
                renderTooltip(guiGraphics, itemStack, entry.getValue(), xPosition, yPosition);
            }

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            poseStack.translate(xPosition, yPosition, 0);
            poseStack.scale(scale, scale, scale);

            if (discoveries.get(entry.getKey())) {
                poseStack.pushPose();
                poseStack.scale(1.0f / scale, 1.0f / scale, 1.0f / scale);
                guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/check.png"),
                        0, 0, 0, 0, 18, 18, 18, 18);
                poseStack.popPose();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor(0.2f, 0.2f, 0.2f, 0.5f);

                guiGraphics.renderItem(itemStack, 0, 0);

                RenderSystem.disableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                guiGraphics.renderItem(itemStack, 0, 0);
            }

            poseStack.popPose();

            i++;
        }

        int previousVar = -100;
        for (var entry : giftList.entrySet()) {
            int width = 350;
            int height = 170;
            int centerX = (this.width - width) / 2;
            int centerY = (this.height - height) / 2 + 10 + 165;
            if (entry.getKey() - previousVar <= 10) {
                centerY -= 35;
            }
            width = (int) (346*(((float) entry.getKey()/ (float) encyclopedia.size()))) - 10;
            centerX += width;

            if (mouseX >= centerX && mouseX <= centerX + 20 && mouseY >= centerY && mouseY <= centerY + 20) {
                renderGiftTooltip(guiGraphics, entry.getValue(), centerX, centerY, entry.getKey());
            }

            previousVar = entry.getKey();
        }
    }

    private void renderTooltip(GuiGraphics guiGraphics, ItemStack itemStack, int itemCount, int x, int y) {
        String itemName = itemStack.getHoverName().getString();
        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains("instrument")) {
            itemName = switch (tag.getString("instrument")) {
                case "minecraft:ponder_goat_horn" -> "염소 뿔 (고민)";
                case "minecraft:sing_goat_horn" -> "염소 뿔 (노래)";
                case "minecraft:seek_goat_horn" -> "염소 뿔 (수색)";
                case "minecraft:feel_goat_horn" -> "염소 뿔 (감각)";
                case "minecraft:admire_goat_horn" -> "염소 뿔 (동경)";
                case "minecraft:call_goat_horn" -> "염소 뿔 (소집)";
                case "minecraft:yearn_goat_horn" -> "염소 뿔 (갈망)";
                case "minecraft:dream_goat_horn" -> "염소 뿔 (꿈결)";
                default -> itemStack.getHoverName().getString();
            };
        }

        int playerItemCount = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack inventoryStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(inventoryStack, itemStack)) {
                playerItemCount += inventoryStack.getCount();
            }
        }
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(itemName));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("|").withStyle(ChatFormatting.BLUE)
                .append(Component.literal(" 보유:").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(playerItemCount+"개").withStyle(ChatFormatting.BLUE))
                .append(Component.literal("/").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("필요 개수:").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(itemCount+"개").withStyle(ChatFormatting.BLUE)));

        guiGraphics.renderComponentTooltip(this.font, tooltip, x, y);
    }

    private void renderGiftTooltip(GuiGraphics guiGraphics, List<ItemStack> itemStacks, int x, int y, int itemNum) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("보상 ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal("(" + itemNum + "개)").withStyle(ChatFormatting.WHITE)));
        for (ItemStack itemStack : itemStacks) {
            Component itemName = itemStack.getHoverName();
            tooltip.add(Component.literal("| ").withStyle(ChatFormatting.BLUE)
                    .append(itemName.copy().withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" "+itemStack.getCount()+"개").withStyle(ChatFormatting.BLUE)));
        }
        guiGraphics.renderComponentTooltip(this.font, tooltip, x, y + 10);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (encyclopedia == null || discoveries == null) {
            return;
        }
        this.renderBackground(guiGraphics);
        int width = 350;
        int height = 170;
        int centerX = (this.width - width) / 2;
        int centerY = (this.height - height) / 2;
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/encyclopedia/encyclopedia_background.png"),
                centerX, centerY + 25  - 70, 0, 0, width, height, width, height);
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/encyclopedia/progress.png"),
                centerX, centerY - 10 + 170, 0, 0, width, 16, width, 16);
        width = (int) (346*(((float) gauge/ (float) encyclopedia.size())));
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/encyclopedia/progress_gauge.png"),
                centerX + 2, centerY - 3 - 6 + 170, 0, 0, width, 14, 346, 14);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderShopItems(guiGraphics, mouseX, mouseY);
    }
}
