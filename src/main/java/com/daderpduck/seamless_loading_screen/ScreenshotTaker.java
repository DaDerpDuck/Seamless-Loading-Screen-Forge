package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import com.daderpduck.seamless_loading_screen.events.OFFpsDrawEvent;
import com.daderpduck.seamless_loading_screen.events.OFLagometerEvent;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    private final Consumer<OFFpsDrawEvent> drawFpsListener = this::cancelFpsDraw;
    private final Consumer<OFLagometerEvent> lagometerListener = this::cancelLagometer;

    protected ScreenshotTaker() {
        super(new TranslationTextComponent("connect.joining"));
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, cancelOverlayListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, drawFpsListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, lagometerListener);
    }

    public static void takeScreenshot() {
        SeamlessLoadingScreen.LOGGER.info("Taking screenshot (takingScreenshot: {}, saveScreenshot: {})", takingScreenshot, saveScreenshot);
        Minecraft mc = Minecraft.getInstance();

        if (!takingScreenshot && mc.world != null) {
            takingScreenshot = true;
            hideGUI = mc.gameSettings.hideGUI;

            mc.gameSettings.hideGUI = true;
            Config.ScreenshotResolution resolution = Config.Resolution.get();
            if (resolution != Config.ScreenshotResolution.NATIVE) resizeScreen(mc, resolution.width, resolution.height);
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
        if (Config.UpdateWorldIcon.get()) tryCreateWorldIcon(mc);

        mc.gameSettings.hideGUI = hideGUI;
        takingScreenshot = false;
        if (Config.Resolution.get() != Config.ScreenshotResolution.NATIVE) resizeScreen(mc, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());
        MinecraftForge.EVENT_BUS.unregister(cancelOverlayListener);
        MinecraftForge.EVENT_BUS.unregister(drawFpsListener);
        MinecraftForge.EVENT_BUS.unregister(lagometerListener);

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
            Path screenshotPath = ScreenshotLoader.getCurrentScreenshotPath();
            SeamlessLoadingScreen.LOGGER.info("Saving screenshot at {}", screenshotPath);

            AsynchronousFileChannel channel = AsynchronousFileChannel.open(screenshotPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            channel.write(ByteBuffer.wrap(screenshotImage.getBytes()), 0);

            if (Config.ArchiveScreenshots.get()) {
                String fileName = FilenameUtils.removeExtension(screenshotPath.getFileName().toString());
                Path archivePath = Paths.get(Minecraft.getInstance().gameDir.getPath(), "screenshots/worlds/archive/" + fileName + "_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".png");
                AsynchronousFileChannel archiveChannel = AsynchronousFileChannel.open(archivePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                archiveChannel.write(ByteBuffer.wrap(screenshotImage.getBytes()), 0);
            }
        } catch (IOException e) {
            SeamlessLoadingScreen.LOGGER.error("Failed to save screenshot", e);
        }
    }

    private static void tryCreateWorldIcon(Minecraft mc) {
        if (mc.isSingleplayer() && Objects.requireNonNull(mc.getIntegratedServer()).isWorldIconSet()) {
            NativeImage nativeimage = ScreenShotHelper.createScreenshot(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), mc.getFramebuffer());
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
                k = (i - j) / 2;
                i = j;
            } else {
                l = (j - i) / 2;
                j = i;
            }

            try (NativeImage nativeimage1 = new NativeImage(64, 64, false)) {
                nativeimage.resizeSubRectTo(k, l, i, j, nativeimage1);
                nativeimage1.write(mc.getIntegratedServer().getWorldIconFile());
            } catch (IOException ioexception) {
                SeamlessLoadingScreen.LOGGER.warn("Couldn't save auto screenshot", ioexception);
            } finally {
                nativeimage.close();
            }
        }
    }

    private void cancelGuiOverlay(RenderGameOverlayEvent.Pre event) {
        if (takingScreenshot) event.setCanceled(true);
    }
    private void cancelFpsDraw(OFFpsDrawEvent event) {
        if (takingScreenshot) event.setCanceled(true);
    }
    private void cancelLagometer(OFLagometerEvent event) {
        if (takingScreenshot) event.setCanceled(true);
    }
}
