package fr.flo504.abstractmenu.item.defaults;

import fr.flo504.abstractmenu.inventory.BaseInventory;
import fr.flo504.abstractmenu.item.ItemClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

public class InventoryItem implements ItemClickEvent {

    private BaseInventory inventory;

    public InventoryItem(BaseInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if(inventory != null)
            inventory.open(player);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Objects.equals(inventory, that.inventory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventory);
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "inventory=" + inventory +
                '}';
    }
}
