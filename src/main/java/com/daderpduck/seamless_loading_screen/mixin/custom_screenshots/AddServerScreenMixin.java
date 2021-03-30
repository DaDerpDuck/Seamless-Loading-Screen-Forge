package com.daderpduck.seamless_loading_screen.mixin.custom_screenshots;

import com.daderpduck.seamless_loading_screen.ServerDataExtras;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public class AddServerScreenMixin extends Screen {
    @Shadow
    @Final
    private ServerData serverData;

    protected AddServerScreenMixin(ITextComponent titleIn) {
        super(titleIn);
    }

    @Redirect(method = "init()V", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/button/Button", ordinal = 1))
    private Button buttonAdd(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction) {
        return new Button(x, y + 24, width-103, height, title, pressedAction);
    }

    @Redirect(method = "init()V", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/button/Button", ordinal = 2))
    private Button buttonCancel(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction) {
        return new Button(x + 103, y, width-103, height, title, pressedAction);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void buttonAllowCustomScreenshot(CallbackInfo ci) {
        addButton(new Button(width / 2 - 100, height /4 + 72 + 24, 200, 20, getText(), button -> {
            ServerDataExtras server = (ServerDataExtras) serverData;
            server.setAllowCustomScreenshots(!server.getAllowCustomScreenshot());
            button.setMessage(getText());
        }));
    }

    @Unique
    private ITextComponent getText() {
        return DialogTexts.getComposedOptionMessage(
                new TranslationTextComponent("seamless_loading_screen.server.allow_custom_screenshot"),
                ((ServerDataExtras) serverData).getAllowCustomScreenshot()
        );
    }
}
