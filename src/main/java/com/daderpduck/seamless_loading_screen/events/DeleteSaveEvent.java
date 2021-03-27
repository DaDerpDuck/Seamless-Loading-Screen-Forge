package com.daderpduck.seamless_loading_screen.events;

import net.minecraftforge.eventbus.api.Event;

import java.nio.file.Path;

public class DeleteSaveEvent extends Event {
    public final Path saveDir;

    public DeleteSaveEvent(Path saveDir) {
        this.saveDir = saveDir;
    }
}
