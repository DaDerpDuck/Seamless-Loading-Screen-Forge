package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    private static boolean takenScreenshot = false;

    @Shadow public abstract void unloadWorld(Screen screenIn);

    @Shadow @Nullable public ClientWorld world;

    @Shadow protected abstract void runGameLoop(boolean renderWorldIn);

    @Inject(method = "createWorld(Ljava/lang/String;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/gen/settings/DimensionGeneratorSettings;)V", at = @At("HEAD"))
    private void createWorld(String worldName, WorldSettings worldSettings, DynamicRegistries.Impl dynamicRegistriesIn, DimensionGeneratorSettings dimensionGeneratorSettings, CallbackInfo ci) {
        ScreenshotLoader.setScreenshotWorld(worldName);
    }

    @Inject(method = "unloadWorld(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
    private void unloadWorldScreenshot(Screen screenIn, CallbackInfo ci) {
        if (!takenScreenshot && world != null) {
            ScreenshotTaker.takeScreenshot(mc -> {
                System.out.println("took screenshot!");
                takenScreenshot = true;
                unloadWorld(screenIn);
            });
            runGameLoop(true);
            ci.cancel();
        } else {
            takenScreenshot = false;
        }
    }
}
