package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Mod(SeamlessLoadingScreen.MOD_ID)
public class SeamlessLoadingScreen
{
    public static final String MOD_ID = "seamless_loading_screen";
    public static final Logger LOGGER = LogManager.getLogger();

    public SeamlessLoadingScreen() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        Config.init(FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml").toString());

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(
                () -> null,
                (version, network) -> network)
        );
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        try {
            String gameDirPath = Minecraft.getInstance().gameDirectory.getPath();
            Files.createDirectories(Paths.get(gameDirPath, "screenshots/worlds/singleplayer"));
            Files.createDirectories(Paths.get(gameDirPath, "screenshots/worlds/servers"));
            Files.createDirectories(Paths.get(gameDirPath, "screenshots/worlds/realms"));
            Files.createDirectories(Paths.get(gameDirPath, "screenshots/worlds/archive"));
        } catch (IOException e) {
            LOGGER.error("Failed to create screenshot directories", e);
        }
    }
}
