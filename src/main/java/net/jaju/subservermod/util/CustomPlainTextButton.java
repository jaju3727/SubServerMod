package net.jaju.subservermod.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;

public class CustomPlainTextButton extends Button {
    private final Font font;
    private Component message;
    private final float scale;
    private int x;
    private int y;

    public CustomPlainTextButton(int x, int y, int width, int height, Component text, Button.OnPress onPress, Font font, float scale) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION);
        this.x = x;
        this.y = y;
        this.font = font;
        this.message = text;
        this.scale = scale;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(this.scale, this.scale, this.scale);

        guiGraphics.drawString(this.font, this.message, (int) (x / this.scale), (int) (y / this.scale), 0x000000);

        guiGraphics.pose().popPose();
    }
}
