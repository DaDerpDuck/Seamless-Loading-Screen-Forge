package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow public int height;
    @Shadow public int width;

    @Inject(method = "renderDirtBackground(I)V", at = @At("HEAD"), cancellable = true)
    private void renderDirtBackground(int vOffset, CallbackInfo ci) {
        if (ScreenshotLoader.isLoaded()) {
            Screen screen = (Screen) (Object) this;
            ScreenshotRenderer.renderScreenshot(this.height, this.width, 255);

            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(screen, new MatrixStack()));
            ci.cancel();
        }
    }
}
