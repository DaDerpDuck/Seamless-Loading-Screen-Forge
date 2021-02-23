package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import com.daderpduck.seamless_loading_screen.mixin.WindowAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Screen that's displayed when disconnect button is clicked
 * Upon render, takes a screenshot, then resumes disconnecting
 */
public class ScreenshotTaker extends Screen {
    private static boolean takingScreenshot = false;
    private static boolean hideGUI = Minecraft.getInstance().gameSettings.hideGUI;

    protected ScreenshotTaker() {
        super(new TranslationTextComponent("connect.joining"));
    }

    public static void takeScreenshot() {
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

        try (NativeImage screenshotImage = ScreenShotHelper.createScreenshot(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), mc.getFramebuffer())) {
            screenshotImage.write(ScreenshotLoader.getCurrentScreenshotPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mc.gameSettings.hideGUI = hideGUI;
        takingScreenshot = false;
        resizeScreen(mc, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight());

        disconnect();
    }

    private void disconnect() {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.world == null) return;

        boolean flag = mc.isIntegratedServerRunning();
        boolean flag1 = mc.isConnectedToRealms();
        //button2.active = false;
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
    }
}
