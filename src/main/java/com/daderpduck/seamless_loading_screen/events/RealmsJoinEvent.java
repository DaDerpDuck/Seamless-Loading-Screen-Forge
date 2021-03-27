package com.daderpduck.seamless_loading_screen.events;

import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraftforge.eventbus.api.Event;

public class RealmsJoinEvent extends Event {
    public final RealmsServer realmsServer;

    public RealmsJoinEvent(RealmsServer realmsServer) {
        this.realmsServer = realmsServer;
    }
}
