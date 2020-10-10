package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.item.Clickable;
import fr.flo504.abstractmenu.item.InventorySlot;
import fr.flo504.abstractmenu.item.ItemClickEvent;
import fr.flo504.abstractmenu.utils.Cloneable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SessionInventory<K> extends BaseInventory {

    private final int line;

    private final Map<Integer, InventorySlot> slots = new HashMap<>();

    private final Map<K, SessionInventory.Session> sessions = new HashMap<>();
    private final Map<String, K> players = new HashMap<>();

    public SessionInventory(String name, int line, MenuFactory factory) {
        super(name, factory);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public void open(Player player, K key){
        SessionInventory.Session session = sessions.get(key);
        if(session == null) {
            session = new SessionInventory.Session();
            for(Map.Entry<Integer, InventorySlot> entrySlot : slots.entrySet()){
                final int position = entrySlot.getKey();
                InventorySlot slot = entrySlot.getValue();
                if(slot instanceof Cloneable)
                    slot = (InventorySlot) ((Cloneable) slot).clone();
                session.slots.put(position, slot);
            }
            for(Map.Entry<Integer, InventorySlot> entrySlot : session.slots.entrySet()){
                final int position = entrySlot.getKey();
                InventorySlot slot = entrySlot.getValue();
                if(slot instanceof Clickable){
                    final ItemClickEvent event = ((Clickable) slot).getEvent();
                    session.events.put(position, event);
                }
            }
        }
        session.getActives().add(player);

        sessions.put(key, session);
        players.put(player.getName(), key);

        final Inventory inventory = Bukkit.createInventory(null, line*9, name);

        session.slots.forEach((slotKey, value) -> inventory.setItem(slotKey, value.getItem(player)));

        factory.registerInventory(this, inventory);
        player.openInventory(inventory);
    }

    public void update(Player player) {
        if(player == null)
            return;

        final K key = players.get(player.getName());
        if(key == null)
            return;

        final SessionInventory.Session session = sessions.get(key);
        if(session == null)
            return;

        final InventoryView openInventory = player.getOpenInventory();
        final Inventory inventory = openInventory.getTopInventory();

        if(!openInventory.getTitle().equals(name) || inventory.getSize()/9 != line)
            return;

        session.slots.forEach((entryKey, value) -> {
            final ItemStack itemStack = value.getItem(player);
            final ItemStack current = openInventory.getItem(entryKey);
            if(current == null || !current.equals(itemStack))
                inventory.setItem(entryKey, itemStack);
        });
    }

    public void update(K key){
        if(key == null)
            return;

        final SessionInventory.Session session = sessions.get(key);
        if(session == null)
            return;

        final int slot = line*9;

        for(Player player : session.getActives()){
            final InventoryView openInventory = player.getOpenInventory();
            final Inventory inventory = openInventory.getTopInventory();

            if(!openInventory.getTitle().equals(name) || inventory.getSize() != slot)
                return;

            session.slots.forEach((entryKey, value) -> {
                final ItemStack itemStack = value.getItem(player);
                final ItemStack current = openInventory.getItem(entryKey);
                if(current == null || !current.equals(itemStack))
                    inventory.setItem(entryKey, itemStack);
            });

        }
    }

    public SessionInventory.Session get(K key){
        if(key == null)
            return null;
        return sessions.get(key);
    }

    @Override
    public void onClick(Player player, ItemStack item, ClickType clickType, int slot, boolean customInventory, InventoryClickEvent e) {
        super.onClick(player, item, clickType, slot, customInventory, e);

        if(item == null)
            return;

        if(item.getType() == Material.AIR)
            return;

        final K key = players.get(player.getName());
        if(key == null)
            return;
        final SessionInventory.Session session = sessions.get(key);
        if(session == null)
            return;

        final ItemClickEvent event = session.events.get(slot);

        if(event == null)
            return;

        event.onClick(clickType, player);
    }

    @Override
    public void onClose(Player player) {
        final K key = players.remove(player.getName());
        if(key == null)
            return;

        final SessionInventory.Session session = sessions.get(key);
        if(session == null)
            return;
        session.getActives().remove(player);

        sessions.put(key, session);
    }

    public void clearSession(K key){
        clearSession(key, false);
    }

    public void clearSession(K key, boolean force){
        if(key == null)
            return;
        final SessionInventory.Session session = sessions.get(key);
        if(session == null)
            return;

        if(!session.getActives().isEmpty() && !force)
            return;

        for(Player player : session.getActives())
            onClose(player);

        sessions.remove(key);
    }

    public final void registerSlot(InventorySlot inventorySlot, int position) {

        if(position >= (this.line*9))
            throw new UnsupportedOperationException("The inventory doesn't contain the slot " + position);

        this.slots.put(position, inventorySlot);
    }

    public final void registerSlot(InventorySlot inventorySlot, List<Integer> positions) {
        for(int position : positions)
            registerSlot(inventorySlot, position);
    }

    public final void registerIndependentSlot(InventorySlot inventorySlot, List<Integer> positions) {
        if(!(inventorySlot instanceof Cloneable)) {
            registerSlot(inventorySlot, positions);
            return;
        }
        final Cloneable cloneable = (Cloneable)inventorySlot;
        for(int position : positions) {
            registerSlot((InventorySlot) cloneable.clone(), position);
        }
    }

    public final static class Session {

        private final List<Player> actives = new ArrayList<>();

        private final Map<Integer, InventorySlot> slots = new HashMap<>();
        private final Map<Integer, ItemClickEvent> events = new HashMap<>();

        public List<Player> getActives() {
            return actives;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Session that = (Session) o;
            return actives.equals(that.actives) &&
                    slots.equals(that.slots) &&
                    events.equals(that.events);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actives, slots, events);
        }

        @Override
        public String toString() {
            return "Session{" +
                    "actives=" + actives +
                    ", slots=" + slots +
                    ", events=" + events +
                    '}';
        }
    }

}
