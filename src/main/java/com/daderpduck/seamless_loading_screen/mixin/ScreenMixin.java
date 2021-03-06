package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow public int height;
    @Shadow public int width;

    @Shadow public abstract void renderDirtBackground(int vOffset);

    @Redirect(method = "renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderDirtBackground(I)V"))
    private void renderDirtBackground(Screen screen, int vOffset) {
        if (ScreenshotLoader.isLoaded()) {
            ScreenshotRenderer.renderScreenshot(this.height, this.width, 255);

            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(screen, new MatrixStack()));
        } else {
            this.renderDirtBackground(vOffset);
        }
    }
}
