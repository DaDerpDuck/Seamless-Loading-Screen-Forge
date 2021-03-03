package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
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

    @Inject(method = "createWorld(Ljava/lang/String;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/gen/settings/DimensionGeneratorSettings;)V", at = @At("HEAD"))
    private void createWorld(String worldName, WorldSettings worldSettings, DynamicRegistries.Impl dynamicRegistriesIn, DimensionGeneratorSettings dimensionGeneratorSettings, CallbackInfo ci) {
        ScreenshotLoader.setScreenshotWorld(worldName);
    }
}
