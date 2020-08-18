package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.item.Clickable;
import fr.flo504.abstractmenu.item.InventorySlot;
import fr.flo504.abstractmenu.item.ItemClickEvent;
import fr.flo504.abstractmenu.utils.Cloneable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomInventory extends BaseInventory{

    private int line;

    private final Map<Integer, InventorySlot> slots = new HashMap<>();
    private final Map<Integer, ItemClickEvent> events = new HashMap<>();

    public CustomInventory(String name, int line, MenuFactory factory) {
        super(name, factory);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void open(Player player) {
        final Inventory inventory = Bukkit.createInventory(null, line*9, name);

        slots.forEach((key, value) -> inventory.setItem(key, value.getItem(player)));

        getFactory().registerInventory(this, inventory);
        player.openInventory(inventory);
    }

    public final void update(Player player) {
        if(player == null)
            return;

        final InventoryView openInventory = player.getOpenInventory();
        final Inventory inventory = openInventory.getTopInventory();

        if(!openInventory.getTitle().equals(name) || inventory.getSize()/9 != line)
            return;

        slots.forEach((key, value) -> {
            final ItemStack itemStack = value.getItem(player);
            final ItemStack current = openInventory.getItem(key);
            if(current == null || !current.equals(itemStack))
                inventory.setItem(key, itemStack);
        });

    }

    public void onClick(Player player, ItemStack item, ClickType clickType, int slot, boolean customInventory, InventoryClickEvent e) {
        super.onClick(player, item, clickType, slot, customInventory, e);

        if(item == null)
            return;

        final ItemClickEvent event = events.get(slot);

        if(event == null)
            return;

        event.onClick(clickType, player);

    }

    public final Map<Integer, InventorySlot> getItems(){
        return this.slots;
    }
    public final Map<Integer, ItemClickEvent> getEvents() {
        return this.events;
    }

    public final Set<Integer> getSlots(){
        return this.slots.keySet();
    }

    public final void registerSlot(InventorySlot inventorySlot, int position) {

        if(position >= (this.line*9))
            throw new UnsupportedOperationException("The inventory doesn't contain the slot " + position);

        this.slots.put(position, inventorySlot);
        if(inventorySlot instanceof Clickable){
            final ItemClickEvent event = ((Clickable) inventorySlot).getEvent();
            if(event != null)
                this.events.put(position, event);
        }
    }

    public final void registerSlot(InventorySlot inventorySlot, List<Integer> positions) {
        for(int position : positions)
            this.registerSlot(inventorySlot, position);
    }

    public final void registerIndependentSlot(InventorySlot inventorySlot, List<Integer> positions) {
        if(!(inventorySlot instanceof Cloneable)) {
            registerSlot(inventorySlot, positions);
            return;
        }
        final Cloneable cloneable = (Cloneable)inventorySlot;
        for(int position : positions) {
            this.registerSlot((InventorySlot) cloneable.clone(), position);
        }
    }

    public void onClose(Player player) {}



}