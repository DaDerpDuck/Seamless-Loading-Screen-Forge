package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameMenuScreen.class)
public class IngameMenuScreenMixin {
    // Lambda syntax when
    @Inject(method = "lambda$addButtons$9(Lnet/minecraft/client/gui/widget/button/Button;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isIntegratedServerRunning()Z"), cancellable = true)
    private void disconnect(Button button, CallbackInfo ci) {
        button.active = false;
        ScreenshotTaker.takeScreenshot();
        ci.cancel();
    }
}
