package net.jaju.subservermod.subclass.skill.woodcutter;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.Woodcutter;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcutterinventory.WoodcutterInventoryCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class WoodcutterSkill {
    private transient Woodcutter woodcutter;
    private static final int EXTRA_SLOTS = 9;

    public WoodcutterSkill(Woodcutter woodcutter) {
        this.woodcutter = woodcutter;
        MinecraftForge.EVENT_BUS.register(this);
    }

    private boolean check = false;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderScreen(ScreenEvent.Render.Post event) {
        if (woodcutter == null || woodcutter.getLevel() < 2) {
            return;
        }

        Player player = woodcutter.getPlayer();
        if (player == null) {
            return;
        }

        player.getCapability(WoodcutterInventoryCapability.WOODCUTTER_INVENTORY_CAPABILITY).ifPresent(cap -> {
            ItemStackHandler extraInventory = cap.getInventory();
            if (event.getScreen() instanceof InventoryScreen) {
                GuiGraphics guiGraphics = event.getGuiGraphics();
                int width = event.getScreen().width;
                int height = event.getScreen().height;
                int relX = width / 10 * 7;
                int relY = height / 10 * 3;
                guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/chest.png"),
                        relX, relY - 18, 0, 0, 0, 18, 18, 18, 18);
                if (check) {
                    for (int i = 0; i < 3; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            int x = relX + j * 18;
                            int y = relY + i * 18;
                            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventoryslot.png"),
                                    x, y, 0, 0, 0, 18, 18, 18, 18);
                            ItemStack stack = extraInventory.getStackInSlot(i * 3 + j);
                            if (!stack.isEmpty()) {
                                guiGraphics.renderItem(stack, x, y);
                                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, null);
                            }
                        }
                    }

                    if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu != null) {
                        for (int i = 0; i < 3; ++i) {
                            for (int j = 0; j < 3; ++j) {
                                int x = relX + j * 18;
                                int y = relY + i * 18;
                                ItemStack stack = extraInventory.getStackInSlot(i * 3 + j);
                                if (!stack.isEmpty() && isMouseOverSlot(x, y, 18, 18, event.getMouseX(), event.getMouseY())) {
                                    renderTooltip(guiGraphics, stack, event.getMouseX(), event.getMouseY());
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        if (woodcutter == null || woodcutter.getLevel() < 2) {
            return;
        }

        Player player = woodcutter.getPlayer();
        if (player == null) {
            return;
        }

        player.getCapability(WoodcutterInventoryCapability.WOODCUTTER_INVENTORY_CAPABILITY).ifPresent(cap -> {
            ItemStackHandler extraInventory = cap.getInventory();
            if (event.getScreen() instanceof InventoryScreen) {
                double mouseX = event.getMouseX();
                double mouseY = event.getMouseY();
                int width = event.getScreen().width;
                int height = event.getScreen().height;
                int relX = width / 10 * 7;
                int relY = height / 10 * 3;

                if (isMouseOverSlot(relX, relY - 18, 18, 18, mouseX, mouseY)) {
                    check = !check;
                    event.setCanceled(true);
                }

                if (check) {
                    for (int i = 0; i < 3; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            int x = relX + j * 18;
                            int y = relY + i * 18;
                            if (isMouseOverSlot(x, y, 18, 18, mouseX, mouseY)) {
                                ItemStack cursorStack = player.containerMenu.getCarried();
                                ItemStack slotStack = extraInventory.getStackInSlot(i * 3 + j);

                                if (cursorStack.isEmpty() && !slotStack.isEmpty()) {
                                    player.containerMenu.setCarried(slotStack.copy());
                                    extraInventory.setStackInSlot(i * 3 + j, ItemStack.EMPTY);
                                } else if (!cursorStack.isEmpty() && slotStack.isEmpty()) {
                                    extraInventory.setStackInSlot(i * 3 + j, cursorStack.copy());
                                    player.containerMenu.setCarried(ItemStack.EMPTY);
                                } else if (!cursorStack.isEmpty() && !slotStack.isEmpty()) {
                                    ItemStack temp = slotStack.copy();
                                    extraInventory.setStackInSlot(i * 3 + j, cursorStack.copy());
                                    player.containerMenu.setCarried(temp);
                                }
                                event.setCanceled(true);
                                return;
                            }
                        }
                    }
                }
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (woodcutter == null || woodcutter.getLevel() < 2) {
            return;
        }

        Player player = woodcutter.getPlayer();
        if (player == null) {
            return;
        }

        player.getCapability(WoodcutterInventoryCapability.WOODCUTTER_INVENTORY_CAPABILITY).ifPresent(cap -> {
            ItemStackHandler extraInventory = cap.getInventory();
            if (event.getScreen() instanceof InventoryScreen) {
                double mouseX = event.getMouseX();
                double mouseY = event.getMouseY();
                int width = event.getScreen().width;
                int height = event.getScreen().height;
                int relX = width / 10 * 7;
                int relY = height / 10 * 3;

                if (check && isMouseOverSlot(relX, relY - 18, 18, 18, mouseX, mouseY)) {
                    event.setCanceled(true);
                }

                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        int x = relX + j * 18;
                        int y = relY + i * 18;
                        if (isMouseOverSlot(x, y, 18, 18, mouseX, mouseY)) {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        });
    }

    private boolean isMouseOverSlot(int slotX, int slotY, int width, int height, double mouseX, double mouseY) {
        return mouseX >= slotX && mouseX < slotX + width && mouseY >= slotY && mouseY < slotY + height;
    }

    private void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        List<Component> tooltip = Screen.getTooltipFromItem(minecraft, stack);
        guiGraphics.renderComponentTooltip(minecraft.font, tooltip, (int) mouseX, (int) mouseY, stack);
    }
}
