package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class ScreenshotRenderer {
    public static void renderScreenshot(double screenHeight, double screenWidth, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        float imageRatio = ScreenshotLoader.getImageRatio();
        float windowRatio = (float)mc.getWindow().getWidth()/mc.getWindow().getHeight();
        float offset = 1 - windowRatio/imageRatio;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ScreenshotLoader.SCREENSHOT);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(0, screenHeight, 0).uv(offset/2, 1F).endVertex();
        bufferBuilder.vertex(screenWidth, screenHeight, 0).uv(1F - offset/2, 1F).endVertex();
        bufferBuilder.vertex(screenWidth, 0, 0).uv(1F - offset/2, 0).endVertex();
        bufferBuilder.vertex(0, 0, 0).uv(offset/2, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public static class Fader {
        private static float elapsedTime = 0;

        public static void tick(float partialTicks) {
            elapsedTime += partialTicks;
        }

        public static float getFadeTime() {
            return Config.FadeTime.get();
        }

        public static float getHoldTime() {
            return Config.HoldTime.get();
        }

        public static float getAlpha() {
            if (getFadeTime() == 0) {
                return elapsedTime < getHoldTime() ? 1F : 0F;
            } else {
                return Math.min(1F - (elapsedTime - getHoldTime())/getFadeTime(), 1F);
            }
        }

        public static boolean isHolding() {
            return elapsedTime <= getHoldTime();
        }

        public static boolean isFading() {
            return elapsedTime != 0 && elapsedTime < getFadeTime() + getHoldTime();
        }

        public static void reset() {
            elapsedTime = 0;
        }
    }
}

