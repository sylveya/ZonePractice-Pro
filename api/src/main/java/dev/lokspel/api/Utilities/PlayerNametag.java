package dev.lokspel.api.Utilities;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public class PlayerNametag {

    private final Component prefix;
    private final NamedTextColor colorOfName;
    private final Component suffix;
    private final int sortPriority;

    public PlayerNametag(Component prefix, NamedTextColor colorOfName, Component suffix, int sortPriority) {
        this.prefix = prefix;
        this.colorOfName = colorOfName;
        this.suffix = suffix;
        this.sortPriority = sortPriority;
    }

}
