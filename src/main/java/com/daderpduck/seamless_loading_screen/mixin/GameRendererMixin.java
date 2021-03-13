package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.integrated.IntegratedServer;
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

    @Redirect(method = "updateCameraAndRender(FJZ)V", at = @At(value = "NEW", target = "com/mojang/blaze3d/matrix/MatrixStack"))
    private MatrixStack getMatrixStack() {
        matrixStack = new MatrixStack();
        return matrixStack;
    }

    @Inject(method = "updateCameraAndRender(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/IProfiler;endSection()V"))
    private void doFade(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo ci) {
        if (ScreenshotLoader.isLoaded()) {
            float alpha = ScreenshotRenderer.Fader.getAlpha();

            if (alpha > 0) {
                int scaledHeight = mc.getMainWindow().getScaledHeight();
                int scaledWidth = mc.getMainWindow().getScaledWidth();

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableAlphaTest();
                ScreenshotRenderer.renderScreenshot(scaledHeight, scaledWidth, (int)(alpha*255));
                RenderSystem.enableAlphaTest();
                RenderSystem.disableBlend();

                if (ScreenshotRenderer.Fader.isHolding() && mc.currentScreen == null)
                    AbstractGui.drawCenteredString(matrixStack, mc.fontRenderer, new TranslationTextComponent("multiplayer.downloadingTerrain"), scaledWidth/2,70,0xFFFFFF);

                ScreenshotRenderer.Fader.tick(partialTicks);
            } else {
                ScreenshotRenderer.Fader.reset();
                ScreenshotLoader.resetState();
            }
        }
    }

    @Redirect(method = "createWorldIcon()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z"))
    private boolean updateWorldIcon1(IntegratedServer integratedServer) {
        return false;
    }

    @Redirect(method = "updateCameraAndRender(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z"))
    private boolean updateWorldIcon2(IntegratedServer integratedServer) {
        return false;
    }
}
