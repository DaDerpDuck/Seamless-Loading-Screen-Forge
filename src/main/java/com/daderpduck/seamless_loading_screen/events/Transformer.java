package com.daderpduck.seamless_loading_screen.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;

public class Transformer {
    public static void postPreLoadLevel(String worldName) {
        MinecraftForge.EVENT_BUS.post(new PreLoadWorldEvent(worldName));
    }

    public static boolean postClearLevel(Screen screenIn) {
        return MinecraftForge.EVENT_BUS.post(new PreUnloadWorldEvent(screenIn));
    }

    public static boolean checkLockTurn() {
        return MinecraftForge.EVENT_BUS.post(new UpdatePlayerLookEvent());
    }

    public static void postRealmJoin(RealmsServer realmsServer) {
        MinecraftForge.EVENT_BUS.post(new RealmsJoinEvent(realmsServer));
    }

    public static void changeChunkLoadFill(PoseStack poseStack, int x1, int y1, int x2, int y2, int color) {
        LevelLoadingScreen.fill(poseStack, x1, y1, x2, y2, color == -16777216 ? -1442840576 : color);
    }
}
