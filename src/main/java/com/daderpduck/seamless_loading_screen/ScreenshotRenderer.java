package com.daderpduck.seamless_loading_screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ScreenshotRenderer {
    public static void renderScreenshot(Screen screen, MatrixStack stack) {
        if (!ScreenshotLoader.isLoaded()) {
            screen.renderBackground(stack);
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        Minecraft.getInstance().getTextureManager().bindTexture(ScreenshotLoader.SCREENSHOT);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, screen.height, 0.0D).tex(0.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(screen.width, screen.height, 0.0D).tex(1.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(screen.width, 0.0D, 0.0D).tex(1.0F, 0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(screen, new MatrixStack()));
    }
}
