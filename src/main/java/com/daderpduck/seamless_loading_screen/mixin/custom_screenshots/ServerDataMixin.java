package com.daderpduck.seamless_loading_screen.mixin.custom_screenshots;

import com.daderpduck.seamless_loading_screen.ServerDataExtras;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerData.class)
public class ServerDataMixin implements ServerDataExtras {
    @Unique
    private boolean allowCustomScreenshots = false;

    @Inject(method = "getNBTCompound()Lnet/minecraft/nbt/CompoundNBT;", at = @At("RETURN"), cancellable = true)
    private void getNBTCompound(CallbackInfoReturnable<CompoundNBT> cir) {
        CompoundNBT nbtCompound = cir.getReturnValue();
        nbtCompound.putBoolean("allowCustomScreenshots", allowCustomScreenshots);
        cir.setReturnValue(nbtCompound);
    }

    @Inject(method = "getServerDataFromNBTCompound(Lnet/minecraft/nbt/CompoundNBT;)Lnet/minecraft/client/multiplayer/ServerData;", at = @At("RETURN"), cancellable = true)
    private static void getServerDataFromNBTCompound(CompoundNBT nbtCompound, CallbackInfoReturnable<ServerData> cir) {
        if (!nbtCompound.contains("allowCustomScreenshots", 1)) return;

        ServerData serverData = cir.getReturnValue();
        //noinspection ConstantConditions
        ((ServerDataMixin)(Object) serverData).allowCustomScreenshots = nbtCompound.getBoolean("allowCustomScreenshots");
        cir.setReturnValue(serverData);
    }

    @Inject(method = "copyFrom(Lnet/minecraft/client/multiplayer/ServerData;)V", at = @At("TAIL"))
    private void copyFrom(ServerData serverDataIn, CallbackInfo ci) {
        //noinspection ConstantConditions
        allowCustomScreenshots = ((ServerDataMixin)(Object) serverDataIn).allowCustomScreenshots;
    }

    @Override
    public void setAllowCustomScreenshots(boolean b) {
        allowCustomScreenshots = b;
    }

    @Override
    public boolean getAllowCustomScreenshot() {
        return allowCustomScreenshots;
    }
}
