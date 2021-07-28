package com.daderpduck.seamless_loading_screen.events;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class PreUnloadWorldEvent extends Event {
    public final Screen nextScreen;

    public PreUnloadWorldEvent(Screen nextScreen) {
        this.nextScreen = nextScreen;
    }
}
