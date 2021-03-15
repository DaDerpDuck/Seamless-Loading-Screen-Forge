package com.daderpduck.seamless_loading_screen.mixin.custom_screenshots;

import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import com.daderpduck.seamless_loading_screen.ServerDataExtras;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Inject(method = "connectToServer(Lnet/minecraft/client/multiplayer/ServerData;)V", at = @At("HEAD"))
    private void getAllowCustomScreenshot(ServerData server, CallbackInfo ci) {
        ScreenshotTaker.shouldSaveScreenshot(!((ServerDataExtras) server).getAllowCustomScreenshot());
    }
}
