package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.item.AbstractItem;
import fr.flo504.abstractmenu.parser.inventory.InventoryInfo;
import fr.flo504.abstractmenu.parser.inventory.SlotInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class AbstractInventory {

    private String name;
    private int line;

    private final MenuFactory factory;

    private final Map<Integer, InventorySlot> slots = new HashMap<>();

    public AbstractInventory(String name, int line, MenuFactory factory) {
        this(name, line, factory, Collections.emptyList());
    }

    public AbstractInventory(String name, int line, MenuFactory factory, List<SlotInfo> slots) {
        this.name = formatTitle(name);
        this.line = line;
        this.factory = factory;

        setupItems(slots);
    }

    protected String formatTitle(String title){
        return ChatColor.RESET + title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = formatTitle(name);
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public MenuFactory getFactory() {
        return factory;
    }

    public final void open(Player player) {
        final Inventory inventory = Bukkit.createInventory(null, line*9, name);

        slots.forEach((key, value) -> inventory.setItem(key, value.getItem(player)));

        factory.registerInventory(this, inventory);
        player.openInventory(inventory);

    }

    public void onClick(Player player, ItemStack item, ClickType clickType, int slot, boolean customInventory, InventoryClickEvent e) {

        if (!customInventory){
            if (!(clickType.equals(ClickType.DOUBLE_CLICK) || e.isShiftClick()))
                return;
        }

        e.setCancelled(true);

        if(item == null)
            return;

        if(!slots.containsKey(slot))
            return;

        final InventorySlot inventorySlot = slots.get(slot);

        inventorySlot.onClick(player, clickType);

    }

    protected final Map<Integer, InventorySlot> getItems(){
        return this.slots;
    }

    protected final Set<Integer> getSlots(){
        return this.slots.keySet();
    }

    protected final void registerSlot(InventorySlot inventorySlot, int position) {

        if(position >= (this.line*9))
            throw new UnsupportedOperationException("The inventory doesn't contain the slot " + position);

        this.slots.put(position, inventorySlot);

    }

    protected final void registerItem(InventorySlot inventorySlot, List<Integer> positions) {
        for(int position : positions)
            this.registerItem(inventorySlot, positions);
    }

    protected abstract void setupItems(List<SlotInfo> info);

}
