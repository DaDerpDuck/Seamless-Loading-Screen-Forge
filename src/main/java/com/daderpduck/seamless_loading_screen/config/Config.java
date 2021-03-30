package com.daderpduck.seamless_loading_screen.config;

import com.daderpduck.seamless_loading_screen.SeamlessLoadingScreen;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue HoldTime;
    public static final ForgeConfigSpec.IntValue FadeTime;
    public static final ForgeConfigSpec.BooleanValue DisableCamera;
    public static final ForgeConfigSpec.EnumValue<ScreenshotResolution> Resolution;
    public static final ForgeConfigSpec.BooleanValue UpdateWorldIcon;
    public static final ForgeConfigSpec.BooleanValue ArchiveScreenshots;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        HoldTime = builder
            .comment("Delay before fade for chunks to load")
            .translation("seamless_loading_screen.config.hold_time.desc")
            .defineInRange("HoldTime", 80, 0, Integer.MAX_VALUE);
        FadeTime = builder
            .comment("Fade duration")
            .translation("seamless_loading_screen.config.fade_time.desc")
            .defineInRange("FadeTime", 20, 0, Integer.MAX_VALUE);
        DisableCamera = builder
            .comment("Disables camera movement until fade is complete")
            .translation("seamless_loading_screen.config.disable_camera.desc")
            .define("DisableCamera", true);
        Resolution = builder
            .comment("Screenshot resolution")
            .translation("seamless_loading_screen.config.resolution.desc")
            .defineEnum("Resolution", ScreenshotResolution.NORMAL);
        UpdateWorldIcon = builder
            .comment("Updates the world icon periodically")
            .translation("seamless_loading_screen.config.update_world_icon.desc")
            .define("UpdateWorldIcon", false);
        ArchiveScreenshots = builder
            .comment("Archives previous screenshots to the archive folder")
            .translation("seamless_loading_screen.config.archive_screenshots.desc")
            .define("ArchiveScreenshots", false);


        SPEC = builder.build();
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
        SeamlessLoadingScreen.LOGGER.debug("Loading config: {}", pathName);

        CommentedFileConfig config = CommentedFileConfig.builder(new File(pathName))
                .sync()
                .autoreload()
                .writingMode(WritingMode.REPLACE)
                .build();
        config.load();
        config.save();

        SPEC.setConfig(config);

        SeamlessLoadingScreen.LOGGER.debug("Loaded config: {}", pathName);
    }

    public static void save() {
        SPEC.save();
    }
}
