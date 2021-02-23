package com.daderpduck.seamless_loading_screen.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class ConfigScreen extends Screen {
    private final Minecraft minecraft;
    private final Screen parentScreen;

    public ConfigScreen(Minecraft mc, Screen parentScreen) {
        super(new TranslationTextComponent("seamless_loading_screen.config.title"));
        this.minecraft = mc;
        this.parentScreen = parentScreen;
    }

    @Override
    public void init() {
        addButton(new Button((width - 200)/2, height - 26, 200, 20, new TranslationTextComponent("gui.done"), button -> onClose()));
    }


    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, title, width/2, 8, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void closeScreen() {
        minecraft.displayGuiScreen(parentScreen);
    }
}
