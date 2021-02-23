package com.daderpduck.seamless_loading_screen.config;

import com.daderpduck.seamless_loading_screen.SeamlessLoadingScreen;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Client CLIENT = new Client(BUILDER);
    public static final ForgeConfigSpec CONFIG = BUILDER.build();

    public static void init(String pathName) {
        SeamlessLoadingScreen.LOGGER.info("Loading config: " + pathName);
        CommentedFileConfig file = CommentedFileConfig.builder(new File(pathName)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        SeamlessLoadingScreen.LOGGER.info("Loaded config: " + pathName);
    }

    public enum ScreenshotResolution {
        NATIVE(0,0),
        NORMAL(4000,1600),
        R4K(4000, 2160),
        R8K(7900,4320);

        public int width, height;
        ScreenshotResolution(int widthIn, int heightIn) {
            width = widthIn;
            height = heightIn;
        }
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Integer> HoldTime;
        public final ForgeConfigSpec.ConfigValue<Integer> FadeTime;
        public final ForgeConfigSpec.ConfigValue<Boolean> DisableCamera;
        public final ForgeConfigSpec.ConfigValue<ScreenshotResolution> Resolution;

        public Client(ForgeConfigSpec.Builder builder) {
            HoldTime = builder
                    .comment("Delay for Loading Chunks")
                    .defineInRange("HoldTime", 80, 0, Integer.MAX_VALUE);
            FadeTime = builder
                    .comment("Fade Duration")
                    .defineInRange("FadeTime", 20, 0, Integer.MAX_VALUE);
            DisableCamera = builder
                    .comment("Disable Camera Movement until the Fade is Done")
                    .define("DisableCamera", true);
            Resolution = builder
                    .comment("Screenshot Resolution")
                    .defineEnum("Resolution", ScreenshotResolution.NORMAL);
        }
    }
}
