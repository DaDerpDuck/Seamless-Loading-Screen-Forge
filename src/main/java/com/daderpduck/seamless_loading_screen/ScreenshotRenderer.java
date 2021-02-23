package com.daderpduck.seamless_loading_screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ScreenshotRenderer {
    public static void renderScreenBackground(Screen screen, MatrixStack stack) {
        renderScreenBackground(screen, stack, 255);
    }

    public static void renderScreenBackground(Screen screen, MatrixStack stack, int alpha) {
        if (!ScreenshotLoader.isLoaded()) {
            screen.renderBackground(stack);
            return;
        }

        renderScreenshot(screen.height, screen.width, alpha);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(screen, new MatrixStack()));
    }

    public static void renderScreenshot(double screenHeight, double screenWidth, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        Minecraft.getInstance().getTextureManager().bindTexture(ScreenshotLoader.SCREENSHOT);
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, screenHeight, 0.0D).tex(0.0F, 1.0F).color(255, 255, 255, alpha).endVertex();
        bufferbuilder.pos(screenWidth, screenHeight, 0.0D).tex(1.0F, 1.0F).color(255, 255, alpha, alpha).endVertex();
        bufferbuilder.pos(screenWidth, 0.0D, 0.0D).tex(1.0F, 0).color(255, 255, 255, alpha).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
        tessellator.draw();
    }
}
