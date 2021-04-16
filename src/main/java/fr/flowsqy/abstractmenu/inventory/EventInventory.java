package fr.flowsqy.abstractmenu.inventory;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
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

    public final static String GENERIC_SESSION_ID = "GENERIC_SESSION_ID";
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

    /**
     * Construct a new instance of EventInventory
     *
     * @param factory The factory for the plugin
     * @param name    The name of the inventory
     * @param line    The number of line in the inventory
     */
    public EventInventory(MenuFactory factory, String name, int line) {
        this(factory, name, line, false);
    }

    /**
     * Construct a new instance of EventInventory
     *
     * @param factory     The factory for the plugin
     * @param name        The name of the inventory
     * @param line        The number of line the inventory
     * @param transaction Allow or not transactions
     */
    public EventInventory(MenuFactory factory, String name, int line, boolean transaction) {
        Objects.requireNonNull(factory);
        this.factory = factory;
        this.name = name;
        setLine(line);
        this.slots = new HashMap<>();
        this.events = new HashMap<>();
        this.transaction = transaction;
    }

    /**
     * Construct a new instance of EventInventory, used to clone
     *
     * @param eventInventory The EventInventory to copy
     */
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

    /**
     * Deserialize from a yaml configuration a EventInventory
     *
     * @param section         The configuration section where the inventory is stored
     * @param factory         The factory for the plugin
     * @param registerHandler The register handler to register items
     * @return An instance of an EventInventory represented by the given Configuration Section
     */
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

    /**
     * Serialize a EventInventory in a Configuration Section
     *
     * @param section        The configuration section where it will be written
     * @param eventInventory The EventInventory to write
     */
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

    /**
     * Get the name of the inventory
     *
     * @return The name of the inventory
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the inventory
     *
     * @param name The name of the inventory
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the number of line of the inventory
     *
     * @return The number of line of the inventory
     */
    public int getLine() {
        return line;
    }

    /**
     * Set the number of line of the inventory
     *
     * @param line The number of line of the inventory
     */
    public void setLine(int line) {
        if (line > 6)
            line = 6;
        if (line < 1)
            line = 1;
        this.line = line;
    }

    /**
     * Get the transaction attribute
     *
     * @return true if allows transactions, false otherwise
     */
    public boolean isTransaction() {
        return transaction;
    }

    /**
     * Set the transaction attribute
     *
     * @param transaction true if allows transactions, false otherwise
     */
    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    /**
     * Get the close callback consumer
     *
     * @return The close callback consumer
     */
    public Consumer<Player> getCloseCallback() {
        return closeCallback;
    }

    /**
     * Set the close callback consumer
     *
     * @param closeCallback The close callback consumer
     */
    public void setCloseCallback(Consumer<Player> closeCallback) {
        this.closeCallback = closeCallback;
    }

    /**
     * Register an item
     *
     * @param builder The item to register
     * @param slots   The slots where the item must be
     */
    public void register(ItemBuilder builder, Integer... slots) {
        register(builder, null, slots);
    }

    /**
     * Register an item with an event
     *
     * @param builder The item to register
     * @param event   The event to register
     * @param slots   The slot where the item and event must be
     */
    public void register(ItemBuilder builder, Consumer<InventoryClickEvent> event, Integer... slots) {
        register(builder, event, Arrays.asList(slots));
    }

    /**
     * Register an item
     *
     * @param builder The item to register
     * @param slots   The slots where the item must be
     */
    public void register(ItemBuilder builder, List<Integer> slots) {
        register(builder, null, slots);
    }

    /**
     * Register an item with an event
     *
     * @param builder The item to register
     * @param event   The event to register
     * @param slots   The slot where the item and event must be
     */
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

    /**
     * Clear all item slots
     */
    public void clearSlots() {
        this.slots.clear();
    }

    /**
     * Clear all event slots
     */
    public void clearEvents() {
        this.events.clear();
    }

    /**
     * Clear all items and events
     */
    public void clear() {
        clearSlots();
        clearEvents();
    }

    /**
     * Open the inventory to a player in generic session
     *
     * @param player The targeted player
     * @return The bukkit inventory involved
     */
    public Inventory open(Player player) {
        return open(player, GENERIC_SESSION_ID);
    }

    /**
     * Open the inventory to a player in given session
     *
     * @param player    The targeted player
     * @param sessionId The session to open
     * @return The bukkit inventory involved
     */
    public Inventory open(Player player, String sessionId) {
        final Inventory inventory = factory.open(sessionId, this, line * 9, RESET_PATTERN + name);
        player.openInventory(inventory);
        return inventory;
    }

    /**
     * Refresh the targeted inventory
     *
     * @param inventory The targeted inventory
     */
    public void refresh(Inventory inventory) {
        slots.forEach((key, value) -> inventory.setItem(key, value.create()));
    }

    /**
     * Refresh inventories
     *
     * @param inventories The targeted inventories
     */
    public void refresh(Iterable<Inventory> inventories) {
        final Map<Integer, ItemStack> items = slots.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().create()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Inventory inventory : inventories) {
            items.forEach(inventory::setItem);
        }
    }

    /**
     * Invoke close callback
     *
     * @param player The player who close the inventory
     */
    public void onClose(Player player) {
        if (closeCallback != null)
            closeCallback.accept(player);
    }

    /**
     * Handle a click
     * The method redirect the event to the concerned event and/or cancel the click if transaction is not allowed
     *
     * @param rawSlot The clicked slot
     * @param event   The bukkit event called
     */
    public void onClick(int rawSlot, InventoryClickEvent event) {
        event.setCancelled(!isTransaction());
        final Consumer<InventoryClickEvent> eventHandler = events.get(rawSlot);
        if (eventHandler != null)
            eventHandler.accept(event);
    }

    /**
     * The clone method
     *
     * @return A new instance of EventInventory, with the sames parameters
     */
    public EventInventory clone() {
        return new EventInventory(this);
    }

    /**
     * A interface that handle the register process for deserialization
     */
    public interface RegisterHandler {

        /**
         * Handle the item deserialization
         *
         * @param eventInventory The concerned EventInventory
         * @param key            The key of the section of the item (which it used as an id)
         * @param builder        The concerned item
         * @param slots          The concerned slots
         */
        void handle(EventInventory eventInventory, String key, ItemBuilder builder, List<Integer> slots);

    }

}
