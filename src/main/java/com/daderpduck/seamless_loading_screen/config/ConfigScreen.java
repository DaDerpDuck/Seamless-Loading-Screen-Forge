package com.daderpduck.seamless_loading_screen.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

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
                "seamless_loading_screen.config.hold_time.title",
                0,
                100,
                1,
                gameSettings -> (double) Config.HoldTime.get(),
                (gameSettings, aDouble) -> Config.HoldTime.set(aDouble.intValue()),
                (gameSettings, option) -> {
                    option.setOptionValues(minecraft.fontRenderer.trimStringToWidth(
                            new TranslationTextComponent("seamless_loading_screen.config.hold_time.tooltip"),
                            200));

                    return new StringTextComponent(
                            I18n.format("seamless_loading_screen.config.hold_time.title") + ": " + option.get(gameSettings));
                }
        ));
        optionsRowList.addOption(new SliderPercentageOption(
                "seamless_loading_screen.config.fade_time.title",
                0,
                100,
                1,
                gameSettings -> (double) Config.FadeTime.get(),
                (gameSettings, aDouble) -> Config.FadeTime.set(aDouble.intValue()),
                (gameSettings, option) -> new StringTextComponent(
                        I18n.format("seamless_loading_screen.config.fade_time.title") + ": " + option.get(gameSettings))
        ));
        optionsRowList.addOption(new BooleanOption(
                "seamless_loading_screen.config.disable_camera.title",
                gameSettings -> Config.DisableCamera.get(),
                (gameSettings, aBoolean) -> Config.DisableCamera.set(aBoolean)
        ));
        optionsRowList.addOption(new IteratableOption(
                "seamless_loading_screen.config.resolution.title",
                (gameSettings, integer) -> Config.Resolution.set(
                        Config.ScreenshotResolution.values()[ (Config.Resolution.get().ordinal() + integer)%Config.ScreenshotResolution.values().length ]),
                (gameSettings, iteratableOption) -> {
                    iteratableOption.setOptionValues(
                            minecraft.fontRenderer.trimStringToWidth(
                                    new TranslationTextComponent("seamless_loading_screen.config.resolution.tooltip" + Config.Resolution.get().ordinal()),
                                    200));

                    return new StringTextComponent(
                            I18n.format("seamless_loading_screen.config.resolution.title") + ": " + Config.Resolution.get().name());
                }
        ));
        optionsRowList.addOption(new BooleanOption(
                "seamless_loading_screen.config.update_world_icon.title",
                gameSettings -> Config.UpdateWorldIcon.get(),
                (gameSettings, aBoolean) -> Config.UpdateWorldIcon.set(aBoolean)
        ));
        optionsRowList.addOption(new BooleanOption(
                "seamless_loading_screen.config.archive_screenshots.title",
                gameSettings -> Config.ArchiveScreenshots.get(),
                (gameSettings, aBoolean) -> Config.ArchiveScreenshots.set(aBoolean)
        ));

        children.add(optionsRowList);

        addButton(new Button((width - 200)/2, height - 26, 200, 20, new TranslationTextComponent("gui.done"), button -> closeScreen()));
    }


    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        optionsRowList.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, font, title, width/2, 8, 0xFFFFFF);

        Optional<Widget> optional = optionsRowList.func_238518_c_(mouseX, mouseY);
        if (optional.isPresent() && optional.get() instanceof IBidiTooltip) {
            Optional<List<IReorderingProcessor>> optional1 = ((IBidiTooltip)optional.get()).func_241867_d();
            optional1.ifPresent(list -> renderToolTip(matrixStack, list, mouseX, mouseY, minecraft.fontRenderer));
        }

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
