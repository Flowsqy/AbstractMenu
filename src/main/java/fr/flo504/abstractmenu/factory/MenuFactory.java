package fr.flo504.abstractmenu.factory;

import fr.flo504.abstractmenu.inventory.AbstractInventory;
import fr.flo504.abstractmenu.inventory.BaseInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MenuFactory implements Listener {

    public MenuFactory(Plugin plugin) {
        inventories = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final Map<Inventory, BaseInventory> inventories;

    public final void registerInventory(BaseInventory abstractInventory, Inventory inventory) {
        this.inventories.put(inventory, abstractInventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onClick(InventoryClickEvent e) {

        if(!inventories.containsKey(e.getInventory()))
            return;

        final BaseInventory inventory = inventories.get(e.getInventory());

        final boolean customInventory = e.getClickedInventory() != null && !e.getClickedInventory().equals(e.getWhoClicked().getInventory());

        inventory.onClick((Player) e.getWhoClicked(), e.getCurrentItem(), e.getClick(), e.getSlot(), customInventory, e);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDrag(InventoryDragEvent e) {

        final InventoryView view = e.getView();

        final Inventory inv = view.getTopInventory();

        if(!this.inventories.containsKey(inv))
            return;

        for(int slot : e.getRawSlots()) {
            if(Objects.equals(view.getInventory(slot), inv)) {
                e.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onClose(InventoryCloseEvent e) {
        final Inventory inventory = e.getInventory();

        final BaseInventory baseInventory = inventories.remove(inventory);

        if(baseInventory != null)
            baseInventory.onClose(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent e) {
        e.getPlayer().closeInventory();
    }

}
