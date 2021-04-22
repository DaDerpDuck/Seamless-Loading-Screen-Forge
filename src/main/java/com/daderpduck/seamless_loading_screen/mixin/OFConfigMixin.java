package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.events.OFFpsDrawEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.optifine.Config", remap = false)
public class OFConfigMixin {
    @Inject(method = "drawFps", at = @At("HEAD"), cancellable = true)
    private static void drawFps(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new OFFpsDrawEvent())) ci.cancel();
    }
}
