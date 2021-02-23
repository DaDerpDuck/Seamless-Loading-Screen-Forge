package com.daderpduck.seamless_loading_screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Holds where screenshots are loaded/saved
 */
public class ScreenshotLoader {
    public static final ResourceLocation SCREENSHOT = new ResourceLocation(SeamlessLoadingScreen.MOD_ID, "screenshot");
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

        try (InputStream in = new FileInputStream(filePath)) {
            if (!filePath.exists()) return;
            NativeImage image = NativeImage.read(in);
            Minecraft.getInstance().getTextureManager().loadTexture(SCREENSHOT, new DynamicTexture(image));
            loaded = true;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static File getCurrentScreenshotPath() {
        return filePath;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void resetState() {
        loaded = false;
        filePath = null;
    }
}