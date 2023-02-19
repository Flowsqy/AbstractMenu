package fr.flowsqy.abstractmenu.inventory;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class EventInventorySerializer {

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

        final Map<ItemBuilder, List<Integer>> itemToSlots = new HashMap<>();
        for (Map.Entry<Integer, ItemBuilder> entry : eventInventory.getSlotToItem().entrySet()) {
            final List<Integer> rawSlots = itemToSlots.computeIfAbsent(entry.getValue(), o -> new LinkedList<>());
            rawSlots.add(entry.getKey());
        }

        final ConfigurationSection slotsSection = section.createSection("items");

        int index = 0;
        for (Map.Entry<ItemBuilder, List<Integer>> entry : itemToSlots.entrySet()) {
            final ConfigurationSection slotSection = slotsSection.createSection(String.valueOf(index));
            ItemBuilder.serialize(slotSection.createSection("item"), entry.getKey());
            slotSection.set("slots", entry.getValue());
            index++;
        }
    }

    /**
     * Deserialize from a yaml configuration a EventInventory
     *
     * @param section         The configuration section where the inventory is stored
     * @param factory         The factory for the plugin
     * @param registerHandler The register handler to register items
     * @return An instance of an EventInventory represented by the given Configuration Section
     */
    public static EventInventory deserialize(ConfigurationSection section, MenuFactory factory, EventInventory.RegisterHandler registerHandler) {
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

}
