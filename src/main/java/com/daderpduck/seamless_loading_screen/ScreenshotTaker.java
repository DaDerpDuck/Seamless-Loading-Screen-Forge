package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import com.daderpduck.seamless_loading_screen.mixin.WindowAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Screen that's displayed when disconnecting
 * Upon render, takes a screenshot
 */
public class ScreenshotTaker extends Screen {
    private static boolean takingScreenshot = false;
    private static boolean saveScreenshot = true;
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

    public static void shouldSaveScreenshot(boolean b) {
        saveScreenshot = b;
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

        writeScreenshot();

        mc.gameSettings.hideGUI = hideGUI;
        mc.gameSettings.chatScale = chatScale;
        takingScreenshot = false;
        resizeScreen(mc, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());

        for (Consumer<Minecraft> consumer : consumers) {
            consumer.accept(mc);
        }
        consumers.clear();
    }

    private void writeScreenshot() {
        if (!saveScreenshot) return;
        if (ScreenshotLoader.getCurrentScreenshotPath() == null) {
            SeamlessLoadingScreen.LOGGER.error("Screenshot path is null!");
            return;
        }

        Minecraft mc = this.minecraft;
        if (mc == null) return;

        try (NativeImage screenshotImage = ScreenShotHelper.createScreenshot(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), mc.getFramebuffer())) {
            File screenshotPath = ScreenshotLoader.getCurrentScreenshotPath();

            screenshotImage.write(screenshotPath);
            SeamlessLoadingScreen.LOGGER.info("Saved screenshot at " + screenshotPath.getPath());

            if (Config.ArchiveScreenshots.get()) {
                String fileName = FilenameUtils.removeExtension(screenshotPath.getName());
                screenshotImage.write(new File("screenshots/worlds/archive/" + fileName + "_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".png"));
            }
        } catch (IOException e) {
            SeamlessLoadingScreen.LOGGER.error("Failed to save screenshot", e);
        }
    }
}
