package com.daderpduck.seamless_loading_screen.screens;

import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ScreenshotMessageScreen extends DirtMessageScreen {
    public ScreenshotMessageScreen(ITextComponent text) {
        super(text);
    }

    @Override
    public void renderBackground(@Nonnull MatrixStack matrixStack) {
        this.renderDirtBackground(0);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ScreenshotRenderer.renderScreenshot(this, matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 70, 16777215);

        // Screen.render
        for (net.minecraft.client.gui.widget.Widget button : this.buttons) {
            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }
}
