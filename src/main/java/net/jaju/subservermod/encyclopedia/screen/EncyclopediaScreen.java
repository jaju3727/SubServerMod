package net.jaju.subservermod.encyclopedia.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.encyclopedia.network.ClientPacketHandler;
import net.jaju.subservermod.encyclopedia.network.ItemDiscoveryPacket;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public EncyclopediaScreen(Player player) {
        super(Component.literal("EncyclopediaScreen"));
        this.player = player;
        this.encyclopedia = ClientPacketHandler.getEncyclopedia();
        this.discoveries = ClientPacketHandler.getDiscoveries();
        this.maxPage = (encyclopedia.size() - 1) / numPerPage + 1;
    }

    @Override
    protected void init() {
        initializeWidgets();
    }

    private void initializeWidgets() {
        this.clearWidgets();
        int standardX = 70;
        int standardY = 30;
        int intervalX = 20;
        int intervalY = 20;
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
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/landreclaim.png"),
                    0, 0, button -> {
                        if (!flag) {
                            System.out.println(entry.getKey()+"  "+entry.getValue());
                            onClickWidget(entry.getKey(), entry.getValue());
                        }
            }));

            i++;
        }

        this.addRenderableWidget(new CustomPlainTextButton(
                standardX + 280, standardY + 140,
                0, 0,
                Component.literal(page+"/"+maxPage),
                button -> {},
                minecraft.font,
                1.0f
        ));

        if (page != minPage) {
            this.addRenderableWidget(new ImageButton(standardX - 30, standardY + 70,
                    20, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                    20, 10, button -> leftPage()));
        }
        if (page != maxPage) {
            this.addRenderableWidget(new ImageButton(standardX + 300, standardY + 70,
                    20, 10, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                    20, 10, button -> rightPage()));
        }
    }

    private void onClickWidget(String itemName, int itemCount) {
        ResourceLocation itemResourceLocation = new ResourceLocation(itemName);
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)), itemCount);
        int playerItemCount = player.getInventory().countItem(itemStack.getItem());
        System.out.println(playerItemCount);
        if (playerItemCount >= itemCount) {
            discoveries.replace(itemName, true);
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

        int standardX = 70;
        int standardY = 30;
        int intervalX = 20;
        int intervalY = 20;

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
            ItemStack itemStack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)));
            int xPosition = standardX + (restI % 15) * intervalX;
            int yPosition = standardY + (restI / 15) * intervalY;

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
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f); // 투명도를 50%로 설정

                guiGraphics.renderItem(itemStack, 0, 0);

                RenderSystem.disableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                guiGraphics.renderItem(itemStack, 0, 0);
            }

            poseStack.popPose();

            if (mouseX >= xPosition && mouseX <= xPosition + 16 * scale && mouseY >= yPosition && mouseY <= yPosition + 16 * scale) {
                renderTooltip(guiGraphics, itemStack, entry.getValue(), xPosition, yPosition);
            }

            i++;
        }
    }

    private void renderTooltip(GuiGraphics guiGraphics, ItemStack itemStack, int itemCount, int x, int y) {
        String itemName = itemStack.getHoverName().getString();
        int playerItemCount = player.getInventory().countItem(itemStack.getItem());
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (encyclopedia == null || discoveries == null) {
            return;
        }
        this.renderBackground(guiGraphics);
        int centerX = (this.width - 256) / 2;
        int centerY = (this.height - 256) / 2;
//        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/example.png"), centerX, centerY, 0, 0, 256, 256);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderShopItems(guiGraphics, mouseX, mouseY);
    }
}
