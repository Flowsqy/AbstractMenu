package fr.flowsqy.abstractmenu.inventory;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EventInventory {

    public final static RegisterHandler REGISTER = (eventInventory, key, builder, slots) -> eventInventory.register(builder, slots);
    public final static RegisterHandler NOTHING = (eventInventory, key, builder, slots) -> {
    };
    private static final String RESET_PATTERN = ChatColor.WHITE.toString();
    private final MenuFactory factory;
    private final Map<Integer, ItemBuilder> slots;
    private final Map<Integer, Consumer<InventoryClickEvent>> events;
    private String name;
    private int line;
    private boolean transaction;
    private Consumer<Player> closeCallback;

    public EventInventory(MenuFactory factory, String name, int line) {
        this(factory, name, line, false);
    }

    public EventInventory(MenuFactory factory, String name, int line, boolean transaction) {
        Objects.requireNonNull(factory);
        this.factory = factory;
        this.name = name;
        setLine(line);
        this.slots = new HashMap<>();
        this.events = new HashMap<>();
        this.transaction = transaction;
    }

    public EventInventory(EventInventory eventInventory) {
        factory = eventInventory.factory;
        name = eventInventory.name;
        line = eventInventory.line;
        slots = eventInventory.slots.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().clone()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        events = eventInventory.events;
        transaction = eventInventory.transaction;
        closeCallback = eventInventory.closeCallback;
    }

    public static EventInventory deserialize(ConfigurationSection section, MenuFactory factory, RegisterHandler registerHandler) {
        if (section == null)
            return null;

        String name = section.getString("name");
        if (name != null)
            name = ChatColor.translateAlternateColorCodes('&', name);
        final int line = section.getInt("line");

        final EventInventory eventInventory = new EventInventory(factory, name, line);

        final ConfigurationSection slotsSection = section.getConfigurationSection("items");

        if (slotsSection != null && registerHandler != null) {
            for (String keySubSection : slotsSection.getKeys(false)) {
                final ConfigurationSection slotSection = slotsSection.getConfigurationSection(keySubSection);
                if (slotSection == null) // Normally impossible
                    continue;
                final List<Integer> rawSlots = slotSection.getIntegerList("slots");
                final ConfigurationSection itemSection = slotSection.getConfigurationSection("item");

                final ItemBuilder builder = itemSection == null ? null : ItemBuilder.deserialize(itemSection);

                registerHandler.handle(eventInventory, keySubSection, builder, rawSlots);
            }
        }

        return eventInventory;
    }

    public static void serialize(ConfigurationSection section, EventInventory eventInventory) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(eventInventory);

        section.set("name", eventInventory.getName().replace(ChatColor.COLOR_CHAR, '&'));
        section.set("line", eventInventory.getLine());

        final Map<ItemBuilder, List<Integer>> slots = new HashMap<>();
        for (Map.Entry<Integer, ItemBuilder> entry : eventInventory.slots.entrySet()) {
            List<Integer> rawSlots = slots.get(entry.getValue());
            if (rawSlots == null)
                rawSlots = new ArrayList<>();
            rawSlots.add(entry.getKey());
            slots.put(entry.getValue(), rawSlots);
        }

        final ConfigurationSection slotsSection = section.createSection("items");

        int index = 0;
        for (Map.Entry<ItemBuilder, List<Integer>> entry : slots.entrySet()) {
            final ConfigurationSection slotSection = slotsSection.createSection(String.valueOf(index));
            ItemBuilder.serialize(slotSection.createSection("item"), entry.getKey());
            slotSection.set("slots", entry.getValue());
            index++;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        if (line > 6)
            line = 6;
        if (line < 1)
            line = 1;
        this.line = line;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    public Consumer<Player> getCloseCallback() {
        return closeCallback;
    }

    public void setCloseCallback(Consumer<Player> closeCallback) {
        this.closeCallback = closeCallback;
    }

    public void register(ItemBuilder builder, Integer... slots) {
        register(builder, null, slots);
    }

    public void register(ItemBuilder builder, Consumer<InventoryClickEvent> event, Integer... slots) {
        register(builder, event, Arrays.asList(slots));
    }

    public void register(ItemBuilder builder, List<Integer> slots) {
        register(builder, null, slots);
    }

    public void register(ItemBuilder builder, Consumer<InventoryClickEvent> event, List<Integer> slots) {
        if (slots == null)
            return;

        if (builder == null && event == null)
            return;

        final int slotCount = line * 9;

        for (int slot : slots) {
            if (slot >= slotCount && slotCount >= 0)
                continue;
            if (builder != null)
                this.slots.put(slot, builder);
            if (event != null)
                this.events.put(slot, event);
        }
    }

    public void clearSlots() {
        this.slots.clear();
    }

    public void clearEvents() {
        this.events.clear();
    }

    public void clear() {
        clearSlots();
        clearEvents();
    }

    public void open(Player player) {
        final Inventory inventory = Bukkit.createInventory(null, line * 9, RESET_PATTERN + name);

        slots.forEach((key, value) -> inventory.setItem(key, value.create()));

        factory.register(inventory, this);
        player.openInventory(inventory);
    }

    public void refresh(Inventory inventory) {
        slots.forEach((key, value) -> inventory.setItem(key, value.create()));
    }

    public void refresh(Iterable<Inventory> inventories) {
        final Map<Integer, ItemStack> items = slots.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().create()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Inventory inventory : inventories) {
            items.forEach(inventory::setItem);
        }
    }

    public void onClose(Player player) {
        if (closeCallback != null)
            closeCallback.accept(player);
    }

    public void onClick(int rawSlot, InventoryClickEvent event) {
        event.setCancelled(!isTransaction());
        final Consumer<InventoryClickEvent> eventHandler = events.get(rawSlot);
        if (eventHandler != null)
            eventHandler.accept(event);
    }

    public EventInventory clone() {
        return new EventInventory(this);
    }

    public interface RegisterHandler {

        void handle(EventInventory eventInventory, String key, ItemBuilder builder, List<Integer> slots);

    }

}
