package com.daderpduck.seamless_loading_screen.events;

import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;

public class Transformer {
    public static void onCreateLevel(String worldName) {
        MinecraftForge.EVENT_BUS.post(new PreLoadWorldEvent(worldName));
    }

    public static boolean clearLevelScreenshot(Screen screenIn) {
        return MinecraftForge.EVENT_BUS.post(new PreUnloadWorldEvent(screenIn));
    }

    public static boolean turnPlayer() {
        return MinecraftForge.EVENT_BUS.post(new UpdatePlayerLookEvent());
    }

    public static void getRealmName(RealmsServer realmsServer) {
        MinecraftForge.EVENT_BUS.post(new RealmsJoinEvent(realmsServer));
    }

    public static void preLoadLevel(String fileName) {
        System.out.println("HI THERE" + fileName);
        MinecraftForge.EVENT_BUS.post(new PreLoadWorldEvent(fileName));
    }
}
