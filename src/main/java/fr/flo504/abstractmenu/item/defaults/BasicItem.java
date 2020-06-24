package fr.flo504.abstractmenu.item.defaults;

import fr.flo504.abstractmenu.item.AbstractItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BasicItem extends AbstractItem {
    public BasicItem(ItemStack item) {
        super(item);
    }

    public BasicItem(String name, Material material, List<String> lore, boolean enchanted, int amount) {
        super(name, material, lore, enchanted, amount);
    }

    public BasicItem(AbstractItem item) {
        super(item);
    }

    @Override
    public ItemStack getItem(Player player) {
        return super.getItem();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {}
}
