package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.events.UpdatePlayerLookEvent;
import net.minecraft.client.MouseHelper;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHelper.class)
public class MouseHelperMixin {
    @Shadow private double xVelocity;
    @Shadow private double yVelocity;

    @Inject(method = "updatePlayerLook()V", at = @At("HEAD"), cancellable = true)
    private void updatePlayerLook(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new UpdatePlayerLookEvent())) {
            xVelocity = 0;
            yVelocity = 0;
            ci.cancel();
        }
    }
}
