package fr.flo504.abstractmenu.item.defaults;

import fr.flo504.abstractmenu.inventory.AbstractInventory;
import fr.flo504.abstractmenu.item.AbstractItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryItem extends AbstractItem {

    private AbstractInventory inventory;

    public InventoryItem(ItemStack item) {
        super(item);

    }

    public InventoryItem(InventoryItem item) {
        super(item);
    }

    public InventoryItem(String name, Material material, List<String> lore, boolean glow, int amount) {
        super(name, material, lore, glow, amount);
    }

    public InventoryItem(ItemStack item, AbstractInventory inventory) {
        super(item);
        this.inventory = inventory;
    }

    public InventoryItem(String name, Material material, List<String> lore, boolean glow, int amount, AbstractInventory inventory) {
        super(name, material, lore, glow, amount);
        this.inventory = inventory;
    }

    public InventoryItem(InventoryItem item, AbstractInventory inventory) {
        super(item);
        this.inventory = inventory;
    }

    public AbstractInventory getInventory() {
        return inventory;
    }

    public void setInventory(AbstractInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public ItemStack getItem(Player player) {
        return super.getItem();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if(inventory == null)
            player.closeInventory();
        else
            inventory.open(player);
    }
}
