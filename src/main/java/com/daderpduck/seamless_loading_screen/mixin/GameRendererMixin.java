package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.daderpduck.seamless_loading_screen.config.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft mc;

    private static float timePassed = 0;

    @Inject(method = "updateCameraAndRender(FJZ)V", at = @At("TAIL"))
    private void doFade(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo ci) {
        if (ScreenshotLoader.isLoaded() && mc.currentScreen == null) {
            float fadeTime = Config.FadeTime.get();
            float holdTime = Config.HoldTime.get();

            float alpha = Math.min(1F - (timePassed - holdTime)/fadeTime, 1F);

            if (alpha > 0) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                ScreenshotRenderer.renderScreenshot(mc.getMainWindow().getScaledHeight(), mc.getMainWindow().getScaledWidth(), (int)(alpha*255));
                RenderSystem.disableBlend();

                timePassed += partialTicks;
            } else {
                timePassed = 0;
                ScreenshotLoader.resetState();
            }
        }
    }
}
