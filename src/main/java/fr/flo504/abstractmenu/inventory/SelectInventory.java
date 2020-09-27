package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.item.Clickable;
import fr.flo504.abstractmenu.item.InventorySlot;
import fr.flo504.abstractmenu.item.ItemClickEvent;
import fr.flo504.abstractmenu.item.ToggleItem;
import fr.flo504.abstractmenu.item.defaults.GroupItemClickEvent;
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
import java.util.function.BiPredicate;

public abstract class SelectInventory<T, K> extends BaseInventory {

    private final int line;

    private final Map<Integer, InventorySlot> slots = new HashMap<>();
    private BiPredicate<ToggleItem, T> groupPredicate;
    private final List<Integer> groupSlots = new ArrayList<>();

    private final Map<K, SelectData<T>> sessions = new HashMap<>();
    private final Map<String, K> players = new HashMap<>();

    public SelectInventory(String name, int line, MenuFactory factory) {
        super(name, factory);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public BiPredicate<ToggleItem, T> getGroupPredicate() {
        return groupPredicate;
    }

    public void setGroupPredicate(BiPredicate<ToggleItem, T> groupPredicate) {
        this.groupPredicate = groupPredicate;
    }

    public List<Integer> getGroupSlots() {
        return groupSlots;
    }

    @Override
    public void open(Player player) {
        throw new RuntimeException("Can not open a select inventory without session, please use open(Player, K, T) method");
    }

    public void open(Player player, K key, T initData){
        SelectData<T> session = sessions.get(key);
        if(session == null) {
            final ToggleGroup<T> group = new ToggleGroup<>(groupPredicate);
            session = new SelectData<>(group);
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
                if(groupSlots.contains(entrySlot.getKey()) && slot instanceof ToggleItem)
                    group.getItems().add((ToggleItem) slot);
            }

            GroupItemClickEvent.linkOther(group.getItems());
            if(group.getPredicate() != null)
                GroupItemClickEvent.toggleCorrect(item -> group.getPredicate().test(item, initData), group.getItems());
        }
        session.getActives().add(player);

        sessions.put(key, session);
        players.put(player.getName(), key);

        final Inventory inventory = Bukkit.createInventory(null, line*9, name);

        session.slots.forEach((slotKey, value) -> inventory.setItem(slotKey, value.getItem(player)));

        getFactory().registerInventory(this, inventory);
        player.openInventory(inventory);
    }

    public void update(Player player) {
        if(player == null)
            return;

        final K key = players.get(player.getName());
        if(key == null)
            return;

        final SelectData<T> selectData = sessions.get(key);
        if(selectData == null)
            return;

        final InventoryView openInventory = player.getOpenInventory();
        final Inventory inventory = openInventory.getTopInventory();

        if(!openInventory.getTitle().equals(name) || inventory.getSize()/9 != line)
            return;

        selectData.slots.forEach((entryKey, value) -> {
            final ItemStack itemStack = value.getItem(player);
            final ItemStack current = openInventory.getItem(entryKey);
            if(current == null || !current.equals(itemStack))
                inventory.setItem(entryKey, itemStack);
        });
    }

    public void update(K key){
        if(key == null)
            return;

        final SelectData<T> selectData = sessions.get(key);
        if(selectData == null)
            return;

        final int slot = line*9;

        for(Player player : selectData.getActives()){
            final InventoryView openInventory = player.getOpenInventory();
            final Inventory inventory = openInventory.getTopInventory();

            if(!openInventory.getTitle().equals(name) || inventory.getSize() != slot)
                return;

            selectData.slots.forEach((entryKey, value) -> {
                final ItemStack itemStack = value.getItem(player);
                final ItemStack current = openInventory.getItem(entryKey);
                if(current == null || !current.equals(itemStack))
                    inventory.setItem(entryKey, itemStack);
            });

        }
    }

    public void refresh(K key){
        if(key == null)
            return;

        final SelectData<T> selectData = sessions.get(key);
        if(selectData == null)
            return;

        final T data = selectData.getData();
        final ToggleGroup<T> group = selectData.getGroup();

        GroupItemClickEvent.toggleCorrect(item -> group.getPredicate().test(item, data), group.getItems());
    }

    public ToggleGroup<T> getGroup(K key){
        if(key == null)
            return null;
        final SelectData<T> data = sessions.get(key);
        if(data == null)
            return null;
        return data.getGroup();
    }

    public void registerGroup(BiPredicate<ToggleItem, T> predicate, Integer... slots){
        registerGroup(predicate, Arrays.asList(slots));
    }

    public void registerGroup(BiPredicate<ToggleItem, T> predicate, List<Integer> slots){
        groupPredicate = predicate;
        groupSlots.addAll(slots);
    }

    public SelectData<T> get(K key){
        if(key == null)
            return null;
        return sessions.get(key);
    }

    public void set(K key, T value){
        if(key == null)
            return;

        final SelectData<T> data = sessions.get(key);
        if(data == null)
            return;

        data.setData(value);
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
        final SelectData<T> data = sessions.get(key);
        if(data == null)
            return;

        final ItemClickEvent event = data.events.get(slot);

        if(event == null)
            return;

        event.onClick(clickType, player);
    }

    @Override
    public void onClose(Player player) {
        final K key = players.remove(player.getName());
        if(key == null)
            return;

        final SelectData<T> data = sessions.get(key);
        if(data == null)
            return;
        data.getActives().remove(player);

        sessions.put(key, data);
    }

    public void clearSession(K key){
        clearSession(key, false);
    }

    public void clearSession(K key, boolean force){
        if(key == null)
            return;
        final SelectData<T> data = sessions.get(key);
        if(data == null)
            return;

        if(!data.getActives().isEmpty() && !force)
            return;

        for(Player player : data.getActives())
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

    public final static class SelectData<T> {

        private T data;
        private final List<Player> actives = new ArrayList<>();
        private final ToggleGroup<T> group;

        private final Map<Integer, InventorySlot> slots = new HashMap<>();
        private final Map<Integer, ItemClickEvent> events = new HashMap<>();

        public SelectData(ToggleGroup<T> group) {
            this.group = group;
        }

        public SelectData(T data, ToggleGroup<T> group) {
            this.data = data;
            this.group = group;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public List<Player> getActives() {
            return actives;
        }

        public ToggleGroup<T> getGroup() {
            return group;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SelectData<?> that = (SelectData<?>) o;
            return Objects.equals(data, that.data) &&
                    actives.equals(that.actives) &&
                    slots.equals(that.slots) &&
                    events.equals(that.events);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, actives, slots, events);
        }

        @Override
        public String toString() {
            return "SelectData{" +
                    "data=" + data +
                    ", actives=" + actives +
                    ", group=" + group +
                    ", slots=" + slots +
                    ", events=" + events +
                    '}';
        }
    }

    public final static class ToggleGroup<T> implements Cloneable{

        private final List<ToggleItem> items = new ArrayList<>();
        private final BiPredicate<ToggleItem, T> predicate;

        public ToggleGroup(BiPredicate<ToggleItem, T> predicate) {
            this.predicate = predicate;
        }

        private ToggleGroup(ToggleGroup<T> group){
            group.items.stream().map(ToggleItem::clone).forEach(items::add);
            this.predicate = group.predicate;
        }

        public List<ToggleItem> getItems() {
            return items;
        }

        public BiPredicate<ToggleItem, T> getPredicate() {
            return predicate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ToggleGroup<?> group = (ToggleGroup<?>) o;
            return items.equals(group.items) &&
                    Objects.equals(predicate, group.predicate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(items, predicate);
        }

        @Override
        public String toString() {
            return "ToggleGroup{" +
                    "items=" + items +
                    ", predicate=" + predicate +
                    '}';
        }

        @Override
        public ToggleGroup<T> clone() {
            return new ToggleGroup<>(this);
        }
    }

}