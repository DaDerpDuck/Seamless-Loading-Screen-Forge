package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {
    @Inject(method = "func_228177_a_()V", at = @At("HEAD"), cancellable = true)
    private void disconnect(CallbackInfo ci) {
        ScreenshotTaker.takeScreenshot(mc -> {
            if (mc.world != null) {
                mc.world.sendQuittingDisconnectingPacket();
            }

            mc.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
            mc.displayGuiScreen(new MainMenuScreen());
        });

        ci.cancel();
    }
}
