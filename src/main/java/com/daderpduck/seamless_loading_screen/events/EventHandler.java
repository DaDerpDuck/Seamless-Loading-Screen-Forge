package com.daderpduck.seamless_loading_screen.events;

import com.daderpduck.seamless_loading_screen.ScreenshotLoader;
import com.daderpduck.seamless_loading_screen.SeamlessLoadingScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SeamlessLoadingScreen.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void event(GuiScreenEvent.InitGuiEvent event) {
        Screen screen = event.getGui();
        Minecraft mc = Minecraft.getInstance();

        if (screen instanceof ConnectingScreen) {
            ServerData serverData = mc.getCurrentServerData();
            if (serverData == null) return;
            ServerAddress serveraddress = ServerAddress.fromString(serverData.serverIP);
            ScreenshotLoader.setScreenshotServer(serveraddress.getIP(), serveraddress.getPort());
        } else if (screen instanceof DisconnectedScreen) {
            ScreenshotLoader.resetState();
        } else if (screen instanceof RealmsGenericErrorScreen) {
            ScreenshotLoader.resetState();
        }
    }
}
