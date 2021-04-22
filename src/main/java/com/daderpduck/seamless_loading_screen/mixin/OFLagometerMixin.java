package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.events.OFLagometerEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.optifine.Lagometer", remap = false)
public class OFLagometerMixin {
    @Inject(method = "showLagometer", at = @At("HEAD"), cancellable = true)
    private static void showLagometer(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new OFLagometerEvent())) ci.cancel();
    }
}
