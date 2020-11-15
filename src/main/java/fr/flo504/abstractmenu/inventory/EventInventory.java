package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.function.Consumer;

public class EventInventory {

    private static final String RESET_PATTERN = ChatColor.WHITE.toString();

    private final MenuFactory factory;
    private String name;
    private int line;

    private final Map<Integer, ItemBuilder> slots;
    private final Map<Integer, Consumer<InventoryClickEvent>> events;

    public EventInventory(MenuFactory factory, String name, int line) {
        this.factory = factory;
        this.name = name;
        setLine(line);
        this.slots = new HashMap<>();
        this.events = new HashMap<>();
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
        if(line > 6)
            line = 6;
        if(line < 1)
            line = 1;
        this.line = line;
    }

    public void register(ItemBuilder builder, Integer... slots){
        register(builder, null, slots);
    }

    public void register(ItemBuilder builder, Consumer<InventoryClickEvent> event, Integer... slots){
        register(builder, event, Arrays.asList(slots));
    }

    public void register(ItemBuilder builder, List<Integer> slots){
        register(builder, null, slots);
    }

    public void register(ItemBuilder builder, Consumer<InventoryClickEvent> event, List<Integer> slots){
        for(int slot : slots){
            if(builder != null)
                this.slots.put(slot, builder);
            if(event != null)
                this.events.put(slot, event);
        }
    }

    public void open(Player player){
        final Inventory inventory = Bukkit.createInventory(null, line*9, RESET_PATTERN + name);

        slots.forEach((key, value) -> inventory.setItem(key, value.create()));

        factory.register(inventory, this);
        player.openInventory(inventory);
    }

    public void onClose(Player player){}

    public void onClick(int rawSlot, InventoryClickEvent event){
        event.setCancelled(true);
        final Consumer<InventoryClickEvent> eventHandler = events.get(rawSlot);
        if(eventHandler != null)
            eventHandler.accept(event);
    }

    public static EventInventory deserialize(ConfigurationSection section, MenuFactory factory, RegisterHandler registerHandler) {
        if(section == null)
            return null;

        String name = section.getString("name");
        if(name != null)
            name = ChatColor.translateAlternateColorCodes('&', name);
        final int line = section.getInt("line");

        final EventInventory eventInventory = new EventInventory(factory, name, line);

        final ConfigurationSection slotsSection = section.getConfigurationSection("items");

        if(slotsSection != null){
            for(String keySubSection : slotsSection.getKeys(false)){
                final ConfigurationSection slotSection = slotsSection.getConfigurationSection(keySubSection);
                if(slotSection == null) // Normally impossible
                    continue;
                final List<Integer> rawSlots = slotSection.getIntegerList("slots");
                final ConfigurationSection itemSection = slotSection.getConfigurationSection("item");
                if(itemSection == null)
                    continue;
                final ItemBuilder builder = ItemBuilder.deserialize(itemSection);

                registerHandler.handle(eventInventory, keySubSection, builder, rawSlots);
            }
        }

        return eventInventory;
    }

    public static void serialize(ConfigurationSection section, EventInventory eventInventory){
        Objects.requireNonNull(section);
        Objects.requireNonNull(eventInventory);

        section.set("name", eventInventory.getName().replace(ChatColor.COLOR_CHAR, '&'));
        section.set("line", eventInventory.getLine());

        final Map<ItemBuilder, List<Integer>> slots = new HashMap<>();
        for(Map.Entry<Integer, ItemBuilder> entry : eventInventory.slots.entrySet()){
            List<Integer> rawSlots = slots.get(entry.getValue());
            if(rawSlots == null)
                rawSlots = new ArrayList<>();
            rawSlots.add(entry.getKey());
            slots.put(entry.getValue(), rawSlots);
        }

        final ConfigurationSection slotsSection = section.createSection("items");

        int index = 0;
        for(Map.Entry<ItemBuilder, List<Integer>> entry : slots.entrySet()){
            final ConfigurationSection slotSection = slotsSection.createSection(String.valueOf(index));
            ItemBuilder.serialize(slotSection.createSection("item"), entry.getKey());
            slotSection.set("slots", entry.getValue());
            index++;
        }
    }

    public final static RegisterHandler REGISTER = (eventInventory, key, builder, slots) -> eventInventory.register(builder, slots);
    public final static RegisterHandler NOTHING = (eventInventory, key, builder, slots) -> {};

    public interface RegisterHandler {

        void handle(EventInventory eventInventory, String key, ItemBuilder builder, List<Integer> slots);

    }

}
