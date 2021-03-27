package com.daderpduck.seamless_loading_screen.mixin;

import com.daderpduck.seamless_loading_screen.events.DeleteSaveEvent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(SaveFormat.LevelSave.class)
public class SaveFormatMixin {
    @Shadow @Final private Path saveDir;

    @Inject(at = @At("TAIL"), method = "deleteSave()V")
    private void deleteScreenshot(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DeleteSaveEvent(saveDir));
    }
}
