package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.events.RealmsJoinEvent;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsMainScreen.class)
public class RealmsMainScreenMixin {
    @Inject(method = "func_223911_a(Lcom/mojang/realmsclient/dto/RealmsServer;Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void getRealmName(RealmsServer realmsServer, Screen parent, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RealmsJoinEvent(realmsServer));
    }
}
