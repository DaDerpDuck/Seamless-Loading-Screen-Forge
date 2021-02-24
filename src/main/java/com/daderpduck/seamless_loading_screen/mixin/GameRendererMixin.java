package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.daderpduck.seamless_loading_screen.config.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft mc;
    private MatrixStack matrixStack;

    private static float timePassed = 0;

    @Redirect(method = "updateCameraAndRender(FJZ)V", at = @At(value = "NEW", target = "com/mojang/blaze3d/matrix/MatrixStack"))
    private MatrixStack getMatrixStack() {
        matrixStack = new MatrixStack();
        return matrixStack;
    }

    @Inject(method = "updateCameraAndRender(FJZ)V", at = @At("TAIL"))
    private void doFade(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo ci) {
        if (ScreenshotLoader.isLoaded() && mc.currentScreen == null) {
            float fadeTime = Config.FadeTime.get();
            float holdTime = Config.HoldTime.get();

            float alpha = Math.min(1F - (timePassed - holdTime)/fadeTime, 1F);

            if (alpha > 0) {
                int scaledHeight = mc.getMainWindow().getScaledHeight();
                int scaledWidth = mc.getMainWindow().getScaledWidth();

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                ScreenshotRenderer.renderScreenshot(scaledHeight, scaledWidth, (int)(alpha*255));
                RenderSystem.disableBlend();

                if (timePassed < holdTime)
                    AbstractGui.drawCenteredString(matrixStack, mc.fontRenderer, new TranslationTextComponent("multiplayer.downloadingTerrain"), scaledWidth/2,70,0xFFFFFF);

                timePassed += partialTicks;
            } else {
                timePassed = 0;
                ScreenshotLoader.resetState();
            }
        }
    }
}
