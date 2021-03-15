package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsGenericErrorScreen.class)
public class RealmsGenericErrorScreenMixin {
    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        ScreenshotLoader.resetState();
    }
}
