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

    public final static RegisterHandler REGISTER = (eventInventory, key, builder, slots) -> eventInventory.register(builder, slots);
    public final static RegisterHandler NOTHING = (eventInventory, key, builder, slots) -> {
    };
    private static final String RESET_PATTERN = ChatColor.WHITE.toString();
    private final UUID uuid;
    private final MenuFactory factory;
    private final Map<Integer, ItemBuilder> slotToItem;
    private final Map<Integer, Consumer<InventoryClickEvent>> slotToEvent;
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
        this.uuid = UUID.randomUUID();
        this.factory = factory;
        this.name = name;
        setLine(line);
        this.slotToItem = new HashMap<>();
        this.slotToEvent = new HashMap<>();
        this.transaction = transaction;
    }

    /**
     * Construct a new instance of EventInventory, used to clone
     *
     * @param eventInventory The EventInventory to copy
     */
    public EventInventory(EventInventory eventInventory) {
        uuid = eventInventory.uuid;
        factory = eventInventory.factory;
        name = eventInventory.name;
        line = eventInventory.line;
        slotToItem = eventInventory.slotToItem.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().clone()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        slotToEvent = eventInventory.slotToEvent;
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
        return EventInventorySerializer.deserialize(section, factory, registerHandler);
    }

    /**
     * Serialize a EventInventory in a Configuration Section
     *
     * @param section        The configuration section where it will be written
     * @param eventInventory The EventInventory to write
     */
    public static void serialize(ConfigurationSection section, EventInventory eventInventory) {
        EventInventorySerializer.serialize(section, eventInventory);
    }

    /**
     * Get slot to item map. Use for implementation
     *
     * @return The slot to item {@link Map}
     */
    Map<Integer, ItemBuilder> getSlotToItem() {
        return slotToItem;
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
                this.slotToItem.put(slot, builder);
            if (event != null)
                this.slotToEvent.put(slot, event);
        }
    }

    /**
     * Clear all item slots
     */
    public void clearSlots() {
        this.slotToItem.clear();
    }

    /**
     * Clear all event slots
     */
    public void clearEvents() {
        this.slotToEvent.clear();
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
        return open(player, uuid);
    }

    /**
     * Open the inventory to a player in given session
     *
     * @param player    The targeted player
     * @param sessionId The session to open
     * @return The bukkit inventory involved
     */
    public Inventory open(Player player, UUID sessionId) {
        final Inventory inventory = factory.open(
                sessionId,
                this,
                line * 9,
                RESET_PATTERN + name,
                player
        );
        player.openInventory(inventory);
        return inventory;
    }

    /**
     * Refresh the targeted inventory
     *
     * @param player    The player to handle the creator listener
     * @param inventory The targeted inventory
     */
    public void refresh(Player player, Inventory inventory) {
        slotToItem.forEach((key, value) -> inventory.setItem(key, value.create(player)));
    }

    /**
     * Refresh a session
     *
     * @param sessionId The id of the session
     * @param player    The player to handle the creator listener
     */
    public void refresh(UUID sessionId, Player player) {
        factory.refresh(sessionId, this, player);
    }

    /**
     * Refresh inventories
     *
     * @param player      The player to handle the creator listener
     * @param inventories The inventories
     */
    public void refresh(Player player, Iterable<Inventory> inventories) {
        final Map<Integer, ItemStack> items = slotToItem.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().create(player)))
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
        final Consumer<InventoryClickEvent> eventHandler = slotToEvent.get(rawSlot);
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
     * An interface that handle the register process for deserialization
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
