package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.WorkingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "loadWorld(Ljava/lang/String;)V", at = @At("HEAD"))
    private void loadWorld(String worldName, CallbackInfo ci) {
        ScreenshotLoader.setScreenshotWorld(worldName);
    }
}
