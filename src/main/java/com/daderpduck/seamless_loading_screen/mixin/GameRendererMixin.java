package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.config.Config;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "createWorldIcon()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z"))
    private boolean updateWorldIcon1(IntegratedServer integratedServer) {
        return !Config.UpdateWorldIcon.get();
    }

    @Redirect(method = "updateCameraAndRender(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z"))
    private boolean updateWorldIcon2(IntegratedServer integratedServer) {
        return !Config.UpdateWorldIcon.get();
    }
}
