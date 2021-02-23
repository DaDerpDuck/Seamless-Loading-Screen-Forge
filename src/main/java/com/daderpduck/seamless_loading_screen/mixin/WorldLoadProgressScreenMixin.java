package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldLoadProgressScreen.class)
public class WorldLoadProgressScreenMixin extends Screen {
    protected WorldLoadProgressScreenMixin(ITextComponent titleIn) {
        super(titleIn);
    }

    @Redirect(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/WorldLoadProgressScreen;renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;)V"))
    private void render(WorldLoadProgressScreen screen, MatrixStack stack) {
        ScreenshotRenderer.renderScreenshot(screen, stack);
    }

    @Redirect(method = "func_238625_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/world/chunk/listener/TrackingChunkStatusListener;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/WorldLoadProgressScreen;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"))
    private static void makeBackgroundTranslucent(MatrixStack matrixStack, int minX, int minY, int maxX, int maxY, int color) {
        fill(matrixStack, minX, minY, maxX, maxY, color == 0xFF000000 ? 0xAA000000 : color);
    }
}
