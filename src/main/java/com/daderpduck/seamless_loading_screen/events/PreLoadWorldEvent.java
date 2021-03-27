package com.daderpduck.seamless_loading_screen.events;

import net.minecraftforge.eventbus.api.Event;

public class PreLoadWorldEvent extends Event {
    public final String worldName;

    public PreLoadWorldEvent(String worldName) {
        this.worldName = worldName;
    }
}
