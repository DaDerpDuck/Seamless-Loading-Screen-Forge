package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.events.PreLoadWorldEvent;
import com.daderpduck.seamless_loading_screen.events.PreUnloadWorldEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow protected abstract void runGameLoop(boolean renderWorldIn);

    @Inject(method = "createWorld(Ljava/lang/String;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/gen/settings/DimensionGeneratorSettings;)V", at = @At("HEAD"))
    private void onCreateWorld(String worldName, WorldSettings worldSettings, DynamicRegistries.Impl dynamicRegistriesIn, DimensionGeneratorSettings dimensionGeneratorSettings, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new PreLoadWorldEvent(worldName));
    }

    @Inject(method = "unloadWorld(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
    private void unloadWorldScreenshot(Screen screenIn, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PreUnloadWorldEvent(screenIn))) {
            runGameLoop(true);
            ci.cancel();
        }
    }
}
