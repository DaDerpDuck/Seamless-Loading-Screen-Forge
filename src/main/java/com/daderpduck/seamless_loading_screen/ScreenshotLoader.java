package com.daderpduck.seamless_loading_screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Holds where screenshots are loaded/saved
 */
public class ScreenshotLoader {
    public static final ResourceLocation SCREENSHOT = new ResourceLocation(SeamlessLoadingScreen.MOD_ID, "screenshot");
    private static float imageRatio = 1;
    private static boolean loaded = false;
    private static File filePath;

    public static void setScreenshotWorld(String worldName) {
        setScreenshot("screenshots/worlds/singleplayer/" + worldName + ".png");
    }

    public static void setScreenshotServer(String address, int port) {
        setScreenshot("screenshots/worlds/servers/" + address + "_" + port + ".png");
    }

    // TODO: Implement realm screenshot functionality
    public static void setScreenshotRealm(String realmName) {
        setScreenshot("screenshots/worlds/realms" + realmName + ".png");
    }

    private static void setScreenshot(String screenshotPath) {
        loaded = false;
        filePath = new File(Minecraft.getInstance().gameDir, screenshotPath);

        if (filePath.exists()) {
            try (InputStream in = new FileInputStream(filePath)) {
                NativeImage image = NativeImage.read(in);
                imageRatio = (float) image.getWidth()/image.getHeight();
                Minecraft.getInstance().getTextureManager().loadTexture(SCREENSHOT, new DynamicTexture(image));
                loaded = true;
            } catch (IOException e) {
                SeamlessLoadingScreen.LOGGER.error("Failed to read screenshot", e);
            }
        } else {
            SeamlessLoadingScreen.LOGGER.warn("Screenshot path doesn't exist: " + filePath.getPath());
        }
    }

    public static float getImageRatio() {
        return imageRatio;
    }

    public static File getCurrentScreenshotPath() {
        return filePath;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void resetState() {
        loaded = false;
    }
}
