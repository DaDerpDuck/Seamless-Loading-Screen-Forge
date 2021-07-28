package com.daderpduck.seamless_loading_screen;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Holds where screenshots are loaded/saved
 */
public class ScreenshotLoader {
    public static final ResourceLocation SCREENSHOT = new ResourceLocation(SeamlessLoadingScreen.MOD_ID, "screenshot");
    private static float imageRatio = 1;
    private static boolean loaded = false;
    private static Path filePath;
    private static final Pattern RESERVED_FILENAMES_PATTERN = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", Pattern.CASE_INSENSITIVE);

    public static void setScreenshotWorld(String worldName) {
        setScreenshot("screenshots/worlds/singleplayer/" + worldName + ".png");
    }

    public static void setScreenshotServer(String address, int port) {
        setScreenshot("screenshots/worlds/servers/" + address + "_" + port + ".png");
    }

    public static void setScreenshotRealm(String realmName) {
        setScreenshot("screenshots/worlds/realms/" + cleanFileName(realmName) + ".png");
    }

    private static void setScreenshot(String screenshotPath) {
        loaded = false;
        filePath = Paths.get(Minecraft.getInstance().gameDirectory.getPath(), screenshotPath);

        if (Files.isRegularFile(filePath)) {
            try (InputStream in = new FileInputStream(filePath.toFile())) {
                NativeImage image = NativeImage.read(in);
                imageRatio = image.getWidth() / (float) image.getHeight();
                Minecraft.getInstance().getTextureManager().register(SCREENSHOT, new DynamicTexture(image));
                loaded = true;
                SeamlessLoadingScreen.LOGGER.info("Screenshot loaded at {}", filePath);
            } catch (IOException e) {
                SeamlessLoadingScreen.LOGGER.error("Failed to read screenshot", e);
            }
        } else {
            SeamlessLoadingScreen.LOGGER.warn("Screenshot path doesn't exist or is not a file {}", filePath);
        }
    }

    private static String cleanFileName(String fileName) {
        for (char c : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            fileName = fileName.replace(c, '_');
        }

        fileName = fileName.replaceAll("[./\"]", "_");
        if (RESERVED_FILENAMES_PATTERN.matcher(fileName).matches()) {
            fileName = "_" + fileName + "_";
        }

        if (fileName.length() > 255 - 4) {
            fileName = fileName.substring(0, 255 - 4);
        }

        return fileName;
    }

    public static float getImageRatio() {
        return imageRatio;
    }

    public static Path getCurrentScreenshotPath() {
        return filePath;
    }

    /**
     * Indicates whether a screenshot can be rendered
     */
    public static boolean isLoaded() {
        return loaded;
    }

    public static void resetState() {
        loaded = false;
    }
}
