package com.daderpduck.seamless_loading_screen.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


/**
 * Gives the chunk colormap a transparent black background
 */
@Mixin(WorldLoadProgressScreen.class)
public class WorldLoadProgressScreenMixin extends Screen {
    protected WorldLoadProgressScreenMixin(ITextComponent titleIn) {
        super(titleIn);
    }

    @Redirect(method = "func_238625_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/world/chunk/listener/TrackingChunkStatusListener;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/WorldLoadProgressScreen;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"))
    private static void makeBackgroundTranslucent(MatrixStack matrixStack, int minX, int minY, int maxX, int maxY, int color) {
        fill(matrixStack, minX, minY, maxX, maxY, color == 0xFF000000 ? 0xAA000000 : color);
    }
}
