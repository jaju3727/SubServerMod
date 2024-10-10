package net.jaju.subservermod.screen.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jaju.subservermod.Subservermod;
import net.jaju.subservermod.util.CustomPlainTextButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GuideScreen extends Screen {
    private int page = 1;
    private final int maxPage = 61;
    private final int minPage = 1;

    public GuideScreen() {
        super(Component.literal("Guide"));
    }

    @Override
    protected void init() {
        initializeWidgets();
    }

    private void initializeWidgets() {
        this.clearWidgets();

        int x = 100;
        if (page != minPage) {
            this.addRenderableWidget(new ImageButton(this.width/2 - x - 30 + 4, (this.height)/2,
                    30, 15, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/leftarrow.png"),
                    30, 15, button -> leftPage()));
        }
        if (page != maxPage) {
            this.addRenderableWidget(new ImageButton(this.width/2 + x + 4, (this.height)/2,
                    30, 15, 0, 0, 0,
                    new ResourceLocation(Subservermod.MOD_ID, "textures/gui/rightarrow.png"),
                    30, 15, button -> rightPage()));
        }
        this.addRenderableWidget(new CustomPlainTextButton(
                (int) ((float) this.width / 2) - 10, (this.height)/2 + 110,
                0, 0,
                Component.literal( page+"/"+maxPage),
                button -> {},
                minecraft.font,
                1.0f, 0xA4A4A4
        ));
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int width = 220;
        int height = (int) (width*0.98181);
        int centerX = (this.width - width) / 2;
        int centerY = (this.height - height) / 2;
        String pageString;
        if (page < 10) {
            pageString = "00" + page;
        } else {
            pageString = "0" + page;
        }
        guiGraphics.blit(new ResourceLocation(Subservermod.MOD_ID, "textures/gui/guide/"+ pageString +".png"),
                centerX, centerY, 0, 0, width, height, width, height);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
