package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import com.daderpduck.seamless_loading_screen.mixin.WindowAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    private static final List<Consumer<Minecraft>> consumers = new ArrayList<>();
    private final Consumer<RenderGameOverlayEvent.Pre> cancelOverlayListener = this::cancelGuiOverlay;

    protected ScreenshotTaker() {
        super(new TranslationTextComponent("connect.joining"));
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, cancelOverlayListener);
    }

    public static void takeScreenshot() {
        SeamlessLoadingScreen.LOGGER.info("Taking screenshot (takingScreenshot: {}, saveScreenshot: {})", takingScreenshot, saveScreenshot);
        Minecraft mc = Minecraft.getInstance();

        if (!takingScreenshot && mc.world != null) {
            takingScreenshot = true;
            hideGUI = mc.gameSettings.hideGUI;

            mc.gameSettings.hideGUI = true;
            Config.ScreenshotResolution resolution = Config.Resolution.get();
            resizeScreen(mc, resolution.width, resolution.height);
            mc.displayGuiScreen(new ScreenshotTaker());
        }
    }

    public static void takeScreenshot(Consumer<Minecraft> consumer) {
        consumers.add(consumer);
        takeScreenshot();
    }

    /**
     * Sets whether screenshots should be saved on exit - used for custom screenshots
     */
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
        takingScreenshot = false;
        resizeScreen(mc, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());
        MinecraftForge.EVENT_BUS.unregister(cancelOverlayListener);

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
            SeamlessLoadingScreen.LOGGER.info("Saving screenshot at {}", screenshotPath.getPath());

            File tempFile = File.createTempFile("slsscreenshot", ".png");
            tempFile.deleteOnExit();
            screenshotImage.write(tempFile);
            Files.move(tempFile.toPath(), screenshotPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(tempFile.toPath());

            if (Config.ArchiveScreenshots.get()) {
                String fileName = FilenameUtils.removeExtension(screenshotPath.getName());
                File archivePath = new File("screenshots/worlds/archive/" + fileName + "_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".png");
                SeamlessLoadingScreen.LOGGER.info("Archived screenshot at {}", archivePath.getPath());
                screenshotImage.write(archivePath);
            }
        } catch (IOException e) {
            SeamlessLoadingScreen.LOGGER.error("Failed to save screenshot", e);
        }
    }

    private void cancelGuiOverlay(RenderGameOverlayEvent.Pre event) {
        if (takingScreenshot) event.setCanceled(true);
    }
}
