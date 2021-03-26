package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.SeamlessLoadingScreen;
import net.minecraft.world.storage.SaveFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Deletes screenshot when world is deleted
@Mixin(SaveFormat.LevelSave.class)
public class SaveFormatMixin {
    @Shadow @Final private Path saveDir;

    @Inject(at = @At("TAIL"), method = "deleteSave()V")
    private void deleteScreenshot(CallbackInfo ci) {
        ScreenshotLoader.setScreenshotWorld(saveDir.getFileName().toString());
        File screenshot = ScreenshotLoader.getCurrentScreenshotPath();

        try {
            SeamlessLoadingScreen.LOGGER.info("Deleting screenshot at {}", screenshot.toString());
            Files.deleteIfExists(screenshot.toPath());
        } catch (IOException e) {
            SeamlessLoadingScreen.LOGGER.error("Failed to delete screenshot", e);
        }

        ScreenshotLoader.resetState();
    }
}
