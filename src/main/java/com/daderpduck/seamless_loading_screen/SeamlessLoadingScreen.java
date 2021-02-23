package com.daderpduck.seamless_loading_screen;

import com.daderpduck.seamless_loading_screen.config.Config;
import com.daderpduck.seamless_loading_screen.config.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ExtensionPoint;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        Config.init(FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-client.toml").toString());
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ConfigScreen::new);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        Minecraft minecraft = event.getMinecraftSupplier().get();

        try {
            String gameDirPath = minecraft.gameDir.getPath();
            Files.createDirectories(Paths.get(gameDirPath, "screenshots/worlds/singleplayer"));
            Files.createDirectories(Paths.get(gameDirPath, "screenshots/worlds/servers"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
