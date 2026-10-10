package dev.lokspel.practice.manager.arena;

import dev.lokspel.practice.manager.arena.arenas.interfaces.DisplayArena;
import dev.lokspel.practice.manager.backend.ConfigFile;

public class ArenaFile extends ConfigFile {

    public ArenaFile(DisplayArena arena) {
        super("/arenas/", arena.getName().toLowerCase());

        saveFile();
        reloadFile();
    }

    @Override
    public void setData() {
    }

    @Override
    public void getData() {
    }

}
