package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public abstract class BaseInventory {

    protected String name;
    protected final MenuFactory factory;

    public BaseInventory(String name, MenuFactory factory) {
        Objects.requireNonNull(factory);
        this.name = formatTitle(name);
        this.factory = factory;
    }

    protected String formatTitle(String title){
        return ChatColor.RESET + ChatColor.WHITE.toString() + title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = formatTitle(name);
    }

    public MenuFactory getFactory() {
        return factory;
    }

    public void onClick(Player player, ItemStack item, ClickType clickType, int slot, boolean customInventory, InventoryClickEvent e) {
        if (!customInventory){
            if (!(clickType.equals(ClickType.DOUBLE_CLICK) || e.isShiftClick()))
                return;
        }

        e.setCancelled(true);
    }

    public abstract void onClose(Player player);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseInventory that = (BaseInventory) o;
        return Objects.equals(name, that.name) &&
                factory.equals(that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, factory);
    }

    @Override
    public String toString() {
        return "BaseInventory{" +
                "name='" + name + '\'' +
                ", factory=" + factory +
                '}';
    }
}
