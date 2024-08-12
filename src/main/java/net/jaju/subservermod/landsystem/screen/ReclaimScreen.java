package net.jaju.subservermod.landsystem.screen;

import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.ModNetworking;
import net.jaju.subservermod.item.ModItem;
import net.jaju.subservermod.landsystem.network.packet.LandManagerMethodPacket;
import net.jaju.subservermod.mailbox.network.AddItemToMailboxPacket;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.jaju.subservermod.util.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReclaimScreen extends Screen {

    private final Player player;
    private final String chunkKey;

    public ReclaimScreen(Component title, Player player, String chunkKey) {
        super(title);
        this.player = player;
        this.chunkKey = chunkKey;
    }

    @Override
    protected void init() {
        initializedWidgets();
    }

    private void initializedWidgets() {
        int standardX = 120;
        int standardY = 60;

        this.addRenderableWidget(new CustomPlainTextButton(
                standardX+50, standardY+46,
                0,
                0,
                Component.literal("정말 회수하시겠습니까?"),
                button -> {},
                minecraft.font,
                1.5f, 0XFFFFFF
        ));

        this.addRenderableWidget(new ImageButton(standardX+70, standardY+70,
                30, 30, 0, 0, 0,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/allow.png"),
                30, 30, button -> {
            ModNetworking.INSTANCE.sendToServer(new LandManagerMethodPacket(chunkKey,"removeChunkOwner"));
            player.sendSystemMessage(Component.literal("청크 소유권이 성공적으로 회수되었습니다."));

            ItemStack coinItem = new ItemStack(ModItem.SUB_COIN.get());

            MutableComponent lore = Component.literal("75원");
            ListTag loreList = new ListTag();
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(lore)));

            coinItem.getOrCreateTagElement("display").put("Lore", loreList);

            ModNetworking.INSTANCE.sendToServer(new AddItemToMailboxPacket(player.getUUID(), coinItem));
            
            this.onClose();
        }));
        this.addRenderableWidget(new ImageButton(standardX+120, standardY+70,
                30, 30, 0, 0, 0,
                new ResourceLocation(Subservermod.MOD_ID, "textures/gui/landsystem/cancel.png"),
                30, 30, button -> {
            player.sendSystemMessage(Component.literal("청크 소유권 회수가 취소되었습니다."));
            Minecraft.getInstance().setScreen(new LandManagerScreen(Component.empty(), player));
        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }


}
