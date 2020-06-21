package fr.flo504.abstractmenu.item;

import fr.flo504.abstractmenu.inventory.InventorySlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractItem extends BaseItem implements InventorySlot {


    public AbstractItem(ItemStack item) {
        super(item);
    }

    public AbstractItem(String name, Material material, List<String> lore, boolean glow, int amount) {
        super(name, material, lore, glow, amount);
    }

    public AbstractItem(BaseItem item) {
        super(item);
    }

}
