package fr.flo504.abstractmenu.factory;

import fr.flo504.abstractmenu.inventory.EventInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MenuFactory implements Listener {

    private final Map<Inventory, EventInventory> inventories;

    public MenuFactory(Plugin plugin) {
        this.inventories = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void register(Inventory inventory, EventInventory eventInventory){
        inventories.put(inventory, eventInventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void handleClick(InventoryClickEvent e){
        final Inventory inv = e.getInventory();

        final EventInventory inventory = inventories.get(inv);

        if(inventory == null)
            return;

        final int rawSlot = e.getRawSlot();
        if(rawSlot < inv.getSize())
            inventory.onClick(rawSlot, e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void handleDrag(InventoryDragEvent e) {
        final InventoryView view = e.getView();

        final Inventory inv = view.getTopInventory();
        final EventInventory eventInventory = inventories.get(inv);

        if(!this.inventories.containsKey(inv))
            return;

        final Set<Integer> rawSlots = e.getRawSlots();

        final int inventorySize = inv.getSize();

        if(rawSlots.size() == 1){
            final int rawSlot = new ArrayList<>(rawSlots).get(0);
            if(rawSlot < inventorySize){
                final InventoryClickEvent inventoryClickEvent = new InventoryClickEvent(
                        view,
                        view.getSlotType(rawSlot),
                        rawSlot,
                        ClickType.LEFT,
                        InventoryAction.UNKNOWN);
                eventInventory.onClick(rawSlot, inventoryClickEvent);
                e.setCancelled(inventoryClickEvent.isCancelled());
                return;
            }
        }

        for(int slot : rawSlots) {
            if(slot < inventorySize) {
                e.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void handleClose(InventoryCloseEvent e){
        final EventInventory inventory = inventories.get(e.getInventory());

        if(inventory != null)
            inventory.onClose((Player) e.getPlayer());
    }

}
