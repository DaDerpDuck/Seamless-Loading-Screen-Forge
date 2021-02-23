package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import net.minecraft.client.gui.screen.ConnectingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectingScreen.class)
public class ConnectingScreenMixin {
    @Inject(method = "connect(Ljava/lang/String;I)V", at = @At("HEAD"))
    private void connect(final String ip, final int port, CallbackInfo ci) {
        ScreenshotLoader.setScreenshotServer(ip, port);
    }
}
