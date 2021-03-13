package com.daderpduck.seamless_loading_screen.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class ConfigScreen extends Screen {
    private final Minecraft minecraft;
    private final Screen parentScreen;
    private OptionsRowList optionsRowList;

    public ConfigScreen(Minecraft mc, Screen parentScreen) {
        super(new TranslationTextComponent("seamless_loading_screen.config.title"));
        this.minecraft = mc;
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        optionsRowList = new OptionsRowList(minecraft, width, height, 24, height - 32, 25);
        optionsRowList.addOption(new SliderPercentageOption(
                "seamless_loading_screen.config.holdTime.title",
                0,
                100,
                1,
                gameSettings -> (double) Config.HoldTime.get(),
                (gameSettings, aDouble) -> Config.HoldTime.set(aDouble.intValue()),
                (gameSettings, option) -> new StringTextComponent(
                        I18n.format("seamless_loading_screen.config.holdTime.title") + ": " + option.get(gameSettings))
        ));
        optionsRowList.addOption(new SliderPercentageOption(
                "seamless_loading_screen.config.fadeTime.title",
                0,
                100,
                1,
                gameSettings -> (double) Config.FadeTime.get(),
                (gameSettings, aDouble) -> Config.FadeTime.set(aDouble.intValue()),
                (gameSettings, option) -> new StringTextComponent(
                        I18n.format("seamless_loading_screen.config.fadeTime.title") + ": " + option.get(gameSettings))
        ));
        optionsRowList.addOption(new BooleanOption(
                "seamless_loading_screen.config.disableCamera.title",
                gameSettings -> Config.DisableCamera.get(),
                (gameSettings, aBoolean) -> Config.DisableCamera.set(aBoolean)
        ));
        optionsRowList.addOption(new IteratableOption(
                "seamless_loading_screen.config.resolution.title",
                (gameSettings, integer) -> Config.Resolution.set(
                        Config.ScreenshotResolution.values()[ (Config.Resolution.get().ordinal() + integer)%Config.ScreenshotResolution.values().length ]),
                (gameSettings, iteratableOption) -> new StringTextComponent(
                        I18n.format("seamless_loading_screen.config.resolution.title") + ": " + Config.Resolution.get().name())
        ));
        optionsRowList.addOption(new BooleanOption(
                "seamless_loading_screen.config.updateWorldIcon.title",
                gameSettings -> Config.UpdateWorldIcon.get(),
                (gameSettings, aBoolean) -> Config.UpdateWorldIcon.set(aBoolean)
        ));

        children.add(optionsRowList);

        addButton(new Button((width - 200)/2, height - 26, 200, 20, new TranslationTextComponent("gui.done"), button -> closeScreen()));
    }


    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        optionsRowList.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, font, title, width/2, 8, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        Config.save();
    }

    @Override
    public void closeScreen() {
        minecraft.displayGuiScreen(parentScreen);
    }
}
