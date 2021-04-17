package fr.flowsqy.abstractmenu.factory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
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
    private final BiMap<String, Inventory> sessions;

    /**
     * Construct an instance of MenuFactory which will handles all Inventory for the plugin
     *
     * @param plugin The plugin who create the factory
     */
    public MenuFactory(Plugin plugin) {
        this.inventories = new HashMap<>();
        this.sessions = HashBiMap.create();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Open an inventory
     * Create or get the inventory associated to the session
     *
     * @param sessionId      The session id
     * @param eventInventory The EventInventory related to this session
     * @param line           The number of line
     * @param name           The name of the inventory
     * @return The bukkit inventory associated with this session
     */
    public Inventory open(String sessionId, EventInventory eventInventory, int line, String name) {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(eventInventory);
        Inventory inventory = sessions.get(sessionId);
        if (inventory == null) {
            inventory = Bukkit.createInventory(null, line, name);
            eventInventory.refresh(inventory);
            inventories.put(inventory, eventInventory);
            sessions.put(sessionId, inventory);
        }
        return inventory;
    }

    /**
     * Refresh a session
     *
     * @param sessionId      The id of the session
     * @param eventInventory The EventInventory which manage items
     */
    public void refresh(String sessionId, EventInventory eventInventory) {
        Objects.requireNonNull(sessionId);
        final Inventory inventory = sessions.get(sessionId);
        if (inventory != null)
            eventInventory.refresh(inventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void handleClick(InventoryClickEvent e) {
        final Inventory inv = e.getInventory();

        final EventInventory inventory = inventories.get(inv);

        if (inventory == null)
            return;

        final ClickType type = e.getClick();

        if (type == ClickType.DOUBLE_CLICK || type.isShiftClick()) {
            e.setCancelled(!inventory.isTransaction());
            return;
        }

        final int rawSlot = e.getRawSlot();
        if (rawSlot < inv.getSize())
            inventory.onClick(rawSlot, e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void handleDrag(InventoryDragEvent e) {
        final InventoryView view = e.getView();

        final Inventory inv = view.getTopInventory();
        final EventInventory eventInventory = inventories.get(inv);

        if (eventInventory == null)
            return;

        final Set<Integer> rawSlots = e.getRawSlots();

        final int inventorySize = inv.getSize();

        if (rawSlots.size() == 1) {
            final int rawSlot = new ArrayList<>(rawSlots).get(0);
            if (rawSlot < inventorySize) {
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

        if (!eventInventory.isTransaction()) {
            for (int slot : rawSlots) {
                if (slot < inventorySize) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void handleClose(InventoryCloseEvent e) {
        final Inventory inventory = e.getInventory();
        final EventInventory eventInventory;
        if (inventory.getViewers().size() == 1 && inventory.getViewers().get(0) == e.getPlayer()) {
            eventInventory = inventories.remove(inventory);
            sessions.inverse().remove(inventory);
        } else {
            eventInventory = inventories.get(inventory);
        }

        if (eventInventory != null)
            eventInventory.onClose((Player) e.getPlayer());
    }

}
