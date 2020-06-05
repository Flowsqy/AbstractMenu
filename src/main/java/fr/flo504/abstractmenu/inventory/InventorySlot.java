package fr.flo504.abstractmenu.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface InventorySlot{

    ItemStack getItem(Player player);

    void onClick(Player player, ClickType clickType);

}
