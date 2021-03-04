package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameMenuScreen.class)
public class IngameMenuScreenMixin extends Screen {

    protected IngameMenuScreenMixin(ITextComponent titleIn) {
        super(titleIn);
    }

    @Redirect(method = "addButtons()V", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/button/Button", ordinal = 7))
    private Button disconnect(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction) {
        return new Button(x, y, width, height, title, (button2) -> {
            assert minecraft != null && minecraft.world != null;

            button2.active = false;
            ScreenshotTaker.takeScreenshot(mc -> {
                boolean flag = minecraft.isIntegratedServerRunning();
                boolean flag1 = minecraft.isConnectedToRealms();
                //button2.active = false;
                minecraft.world.sendQuittingDisconnectingPacket();
                if (flag) {
                    minecraft.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
                } else {
                    minecraft.unloadWorld();
                }

                if (flag) {
                    minecraft.displayGuiScreen(new MainMenuScreen());
                } else if (flag1) {
                    RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                    realmsbridgescreen.func_231394_a_(new MainMenuScreen());
                } else {
                    minecraft.displayGuiScreen(new MultiplayerScreen(new MainMenuScreen()));
                }
            });
        });
    }
}
