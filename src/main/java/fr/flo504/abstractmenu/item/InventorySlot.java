package fr.flo504.abstractmenu.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface InventorySlot {

    String getId();

    ItemStack getItem(Player player);

}
