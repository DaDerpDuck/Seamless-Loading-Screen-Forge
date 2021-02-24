package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.text.TranslationTextComponent;
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

        ScreenshotTaker.takeScreenshot(mc -> {
            if (mc.world == null) return;

            boolean flag = mc.isIntegratedServerRunning();
            boolean flag1 = mc.isConnectedToRealms();

            mc.world.sendQuittingDisconnectingPacket();
            if (flag) {
                mc.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
            } else {
                mc.unloadWorld();
            }

            if (flag) {
                mc.displayGuiScreen(new MainMenuScreen());
            } else if (flag1) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                realmsbridgescreen.func_231394_a_(new MainMenuScreen());
            } else {
                mc.displayGuiScreen(new MultiplayerScreen(new MainMenuScreen()));
            }
        });

        ci.cancel();
    }
}
