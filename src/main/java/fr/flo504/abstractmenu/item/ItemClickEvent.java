package fr.flo504.abstractmenu.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface ItemClickEvent {

    void onClick(ClickType type, Player player);

}
