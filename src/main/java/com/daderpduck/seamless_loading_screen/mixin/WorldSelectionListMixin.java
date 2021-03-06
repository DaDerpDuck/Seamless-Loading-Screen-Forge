package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.Entry.class)
public class WorldSelectionListMixin {
    @Shadow
    @Final
    private WorldSummary field_214451_d;

    @Inject(method = "func_214443_e()V", at = @At("HEAD"))
    private void play(CallbackInfo ci) {
        ScreenshotLoader.setScreenshotWorld(field_214451_d.getFileName());
    }
}
