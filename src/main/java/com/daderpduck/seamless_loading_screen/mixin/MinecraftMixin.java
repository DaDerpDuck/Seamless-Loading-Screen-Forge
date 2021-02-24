package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    private volatile boolean running;

    @Inject(method = "shutdown()V", at = @At("HEAD"), cancellable = true)
    private void shutdown(CallbackInfo ci) {
        if (Minecraft.getInstance().world != null) {
            ScreenshotTaker.takeScreenshot(mc -> running = false);

            ci.cancel();
        }
    }
}
