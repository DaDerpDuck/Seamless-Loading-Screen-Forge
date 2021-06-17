package com.daderpduck.seamless_loading_screen.events;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.ScreenshotRenderer;
import com.daderpduck.seamless_loading_screen.ScreenshotTaker;
import com.daderpduck.seamless_loading_screen.SeamlessLoadingScreen;
import com.daderpduck.seamless_loading_screen.config.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SeamlessLoadingScreen.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void initGuiEvent(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        Minecraft mc = Minecraft.getInstance();

        if (screen instanceof ConnectingScreen) {
            ServerData serverData = mc.getCurrentServerData();
            if (serverData == null) return;
            Minecraft.getInstance().mouseHelper.ignoreFirstMove();
            ServerAddress serveraddress = ServerAddress.fromString(serverData.serverIP);
            ScreenshotLoader.setScreenshotServer(serveraddress.getIP(), serveraddress.getPort());
        } else if (screen instanceof DisconnectedScreen) {
            ScreenshotLoader.resetState();
        } else if (screen instanceof RealmsGenericErrorScreen) {
            ScreenshotLoader.resetState();
        }
    }

    @SubscribeEvent
    public static void onWorldJoin(PreLoadWorldEvent event) {
        Minecraft.getInstance().mouseHelper.ignoreFirstMove();
        ScreenshotLoader.setScreenshotWorld(event.worldName);
        ScreenshotTaker.shouldSaveScreenshot(true);
    }

    @SubscribeEvent
    public static void onRealmsJoin(RealmsJoinEvent event) {
        Minecraft.getInstance().mouseHelper.ignoreFirstMove();
        ScreenshotLoader.setScreenshotRealm(event.realmsServer.field_230584_c_); // realm name
        ScreenshotTaker.shouldSaveScreenshot(true);
    }

    private static boolean takenScreenshot = false;
    @SubscribeEvent
    public static void onUnloadWorld(PreUnloadWorldEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (!takenScreenshot && mc.world != null) {
            ScreenshotTaker.takeScreenshot(ignored -> {
                takenScreenshot = true;
                mc.unloadWorld(event.nextScreen);
            });
            ScreenshotLoader.resetState();
            event.setCanceled(true);
        } else {
            takenScreenshot = false;
        }
    }

    @SubscribeEvent
    public static void onMouseMove(UpdatePlayerLookEvent event) {
        if (ScreenshotRenderer.Fader.isFading() && Config.DisableCamera.get()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderBackground(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (ScreenshotLoader.isLoaded()) {
            ScreenshotRenderer.renderScreenshot(event.getGui().height, event.getGui().width, 255);
        }
    }

    @SubscribeEvent
    public static void onSaveDelete(DeleteSaveEvent event) {
        ScreenshotLoader.setScreenshotWorld(event.saveDir.getFileName().toString());
        Path screenshotPath = ScreenshotLoader.getCurrentScreenshotPath();

        try {
            SeamlessLoadingScreen.LOGGER.info("Deleting screenshot at {}", screenshotPath);
            Files.deleteIfExists(screenshotPath);
        } catch (IOException e) {
            SeamlessLoadingScreen.LOGGER.error("Failed to delete screenshot", e);
        }

        ScreenshotLoader.resetState();
    }

    @SubscribeEvent
    public static void onRenderTickEnd(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (ScreenshotLoader.isLoaded() && mc.currentScreen == null) {
            float alpha = ScreenshotRenderer.Fader.getAlpha();

            if (alpha > 0) {
                int scaledHeight = mc.getMainWindow().getScaledHeight();
                int scaledWidth = mc.getMainWindow().getScaledWidth();

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableAlphaTest();
                ScreenshotRenderer.renderScreenshot(scaledHeight, scaledWidth, (int)(alpha*255));
                RenderSystem.enableAlphaTest();
                RenderSystem.disableBlend();

                if (ScreenshotRenderer.Fader.isHolding() && mc.currentScreen == null)
                    AbstractGui.drawCenteredString(new MatrixStack(), mc.fontRenderer, new TranslationTextComponent("multiplayer.downloadingTerrain"), scaledWidth/2,70,0xFFFFFF);

                ScreenshotRenderer.Fader.tick(event.renderTickTime);
            } else {
                ScreenshotRenderer.Fader.reset();
                ScreenshotLoader.resetState();
            }
        }
    }
}
