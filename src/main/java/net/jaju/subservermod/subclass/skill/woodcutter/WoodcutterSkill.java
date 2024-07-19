package net.jaju.subservermod.subclass.skill.woodcutter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.subclass.Woodcutter;
import net.jaju.subservermod.subclass.skill.woodcutter.woodcutterinventory.WoodcutterScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Subservermod.MOD_ID, value = Dist.CLIENT)
public class WoodcutterSkill {
    private transient Woodcutter woodcutter;

    public WoodcutterSkill(Woodcutter woodcutter) {
        this.woodcutter = woodcutter;
        MinecraftForge.EVENT_BUS.register(this);
    }

    private boolean check = false;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Subservermod.MOD_ID, "textures/gui/container/extended_inventory.png");
    private static final int EXTRA_SLOTS = 9;
    private static final ItemStack[] extraInventory = new ItemStack[EXTRA_SLOTS];

    static {
        Arrays.fill(extraInventory, ItemStack.EMPTY);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderScreen(ScreenEvent.Render.Post event) {
        if (woodcutter == null || woodcutter.getLevel() < 2) {
            return;
        }
        if (event.getScreen() instanceof InventoryScreen) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int width = event.getScreen().width;
            int height = event.getScreen().height;
            int relX = width/10*7;
            int relY = height/10*3;
            // 추가 슬롯 위치 설정 및 텍스처 렌더링
            guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/chest.png"),
                    relX, relY - 18, 0, 0, 0, 18, 18, 18, 18);
            if(check) {
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        int x = relX + j * 18;
                        int y = relY + i * 18;
                        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/inventoryslot.png"),
                                x, y, 0, 0, 0, 18, 18, 18, 18);
                        ItemStack stack = extraInventory[i*3 + j];
                        if (!stack.isEmpty()) {
                            guiGraphics.renderItem(stack, x, y);
                            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, null);
                        }
                    }
                }

                Player player = Minecraft.getInstance().player;
                if (player != null && player.containerMenu != null) {
                    for (int i = 0; i < 3; ++i) {
                        for (int j = 0; j < 3; ++j) {
                            int x = relX + j * 18;
                            int y = relY + i * 18;
                            ItemStack stack = extraInventory[i*3 + j];
                            if (!stack.isEmpty() && isMouseOverSlot(x, y, 18, 18, event.getMouseX(), event.getMouseY())) {
                                renderTooltip(guiGraphics, stack, event.getMouseX(), event.getMouseY());
                            }
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        if (woodcutter == null || woodcutter.getLevel() < 2) {
            return;
        }
        if (event.getScreen() instanceof InventoryScreen) {
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            Player player = Minecraft.getInstance().player;
            int width = event.getScreen().width;
            int height = event.getScreen().height;
            int relX = width / 10 * 7;
            int relY = height / 10 * 3;

            if (isMouseOverSlot(relX, relY - 18, 18, 18, mouseX, mouseY)) {
                check = !check;
                event.setCanceled(true); // 이 라인을 추가하여 슬롯을 클릭한 경우 이벤트를 취소합니다.
            }

            if (check) {
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        int x = relX + j * 18;
                        int y = relY + i * 18;
                        if (isMouseOverSlot(x, y, 18, 18, mouseX, mouseY)) {
                            if (player != null) {
                                ItemStack cursorStack = player.containerMenu.getCarried();
                                ItemStack slotStack = extraInventory[i * 3 + j];

                                if (cursorStack.isEmpty() && !slotStack.isEmpty()) {
                                    // 슬롯에 있는 아이템을 커서로 가져오기
                                    player.containerMenu.setCarried(slotStack.copy());
                                    extraInventory[i * 3 + j] = ItemStack.EMPTY;
                                } else if (!cursorStack.isEmpty() && slotStack.isEmpty()) {
                                    // 커서에 있는 아이템을 슬롯에 넣기
                                    extraInventory[i * 3 + j] = cursorStack.copy();
                                    player.containerMenu.setCarried(ItemStack.EMPTY);
                                } else if (!cursorStack.isEmpty() && !slotStack.isEmpty()) {
                                    // 커서와 슬롯에 모두 아이템이 있는 경우 아이템을 교환합니다.
                                    ItemStack temp = slotStack.copy();
                                    extraInventory[i * 3 + j] = cursorStack.copy();
                                    player.containerMenu.setCarried(temp);
                                }
                                event.setCanceled(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (woodcutter == null || woodcutter.getLevel() < 2) {
            return;
        }
        if (event.getScreen() instanceof InventoryScreen) {
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            int width = event.getScreen().width;
            int height = event.getScreen().height;
            int relX = width / 10 * 7;
            int relY = height / 10 * 3;

            // 추가 인벤토리 슬롯 내에서 클릭하면 아이템을 버리지 않도록 설정
            if (check && isMouseOverSlot(relX, relY - 18, 18, 18, mouseX, mouseY)) {
                event.setCanceled(true);
            }

            // 추가 인벤토리 영역을 벗어난 경우 아이템을 버리지 않도록 설정
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
