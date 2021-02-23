package com.daderpduck.seamless_loading_screen.config;

import com.daderpduck.seamless_loading_screen.SeamlessLoadingScreen;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class Config {
    public static final ForgeConfigSpec CONFIG;
    public static final ForgeConfigSpec.ConfigValue<Integer> HoldTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> FadeTime;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DisableCamera;
    public static final ForgeConfigSpec.ConfigValue<ScreenshotResolution> Resolution;

    private static CommentedFileConfig fileConfig;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        HoldTime = builder
            .comment("Delay before fade for chunks to load")
            .translation("seamless_loading_screen.config.holdTime.desc")
            .defineInRange("HoldTime", 80, 0, Integer.MAX_VALUE);
        FadeTime = builder
            .comment("Fade duration")
            .translation("seamless_loading_screen.config.fadeTime.desc")
            .defineInRange("FadeTime", 20, 0, Integer.MAX_VALUE);
        DisableCamera = builder
            .comment("Disables camera movement until fade is complete")
            .translation("seamless_loading_screen.config.disableCamera.desc")
            .define("DisableCamera", true);
        Resolution = builder
            .comment("Screenshot resolution")
            .translation("seamless_loading_screen.config.resolution.desc")
            .defineEnum("Resolution", ScreenshotResolution.NORMAL);

        CONFIG = builder.build();
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

    public static void init(String pathName) {
        SeamlessLoadingScreen.LOGGER.info("Loading config: " + pathName);
        fileConfig = CommentedFileConfig.builder(new File(pathName)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        fileConfig.load();
        SeamlessLoadingScreen.LOGGER.info("Loaded config: " + pathName);
    }

    public static void save() {
        fileConfig.save();
    }
}
