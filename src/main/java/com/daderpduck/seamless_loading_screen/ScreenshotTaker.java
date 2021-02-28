package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import com.daderpduck.seamless_loading_screen.mixin.WindowAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Screen that's displayed when disconnecting
 * Upon render, takes a screenshot
 */
public class ScreenshotTaker extends Screen {
    private static boolean takingScreenshot = false;
    private static boolean hideGUI;
    private static double chatScale;
    private static final List<Consumer<Minecraft>> consumers = new ArrayList<>();

    protected ScreenshotTaker() {
        super(new TranslationTextComponent("connect.joining"));
    }

    public static void takeScreenshot() {
        Minecraft mc = Minecraft.getInstance();

        if (!takingScreenshot && mc.world != null) {
            takingScreenshot = true;
            hideGUI = mc.gameSettings.hideGUI;
            chatScale = mc.gameSettings.chatScale;

            mc.gameSettings.hideGUI = true;
            mc.gameSettings.chatScale = 0;
            Config.ScreenshotResolution resolution = Config.Resolution.get();
            resizeScreen(mc, resolution.width, resolution.height);
            mc.displayGuiScreen(new ScreenshotTaker());
        }
    }

    public static void takeScreenshot(Consumer<Minecraft> consumer) {
        consumers.add(consumer);
        takeScreenshot();
    }

    private static void resizeScreen(Minecraft mc, int width, int height) {
        @SuppressWarnings("ConstantConditions")
        WindowAccessor windowAccessor = (WindowAccessor) (Object) mc.getMainWindow();

        windowAccessor.setFramebufferWidth(width);
        windowAccessor.setFramebufferHeight(height);

        mc.updateWindowSize();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!takingScreenshot) return;

        Minecraft mc = this.minecraft;
        if (mc == null) return;

        if (ScreenshotLoader.getCurrentScreenshotPath() != null) {
            try (NativeImage screenshotImage = ScreenShotHelper.createScreenshot(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), mc.getFramebuffer())) {
                screenshotImage.write(ScreenshotLoader.getCurrentScreenshotPath());
            } catch (IOException e) {
                SeamlessLoadingScreen.LOGGER.error("Failed to save screenshot", e);
            }
        } else {
            SeamlessLoadingScreen.LOGGER.error("Screenshot path is null!");
        }

        mc.gameSettings.hideGUI = hideGUI;
        mc.gameSettings.chatScale = chatScale;
        takingScreenshot = false;
        resizeScreen(mc, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());

        for (Consumer<Minecraft> consumer : consumers) {
            consumer.accept(mc);
        }
        consumers.clear();
    }
}
