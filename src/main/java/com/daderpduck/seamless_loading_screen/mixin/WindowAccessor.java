package com.daderpduck.seamless_loading_screen.mixin;

import net.minecraft.client.MainWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MainWindow.class)
public interface WindowAccessor {
    @Accessor("framebufferWidth")
    void setFramebufferWidth(int width);

    @Accessor("framebufferHeight")
    void setFramebufferHeight(int height);
}
