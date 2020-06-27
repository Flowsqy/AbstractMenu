package fr.flo504.abstractmenu.parser.item.defaults;

import fr.flo504.abstractmenu.item.InventorySlot;
import fr.flo504.abstractmenu.item.MultipleItem;
import fr.flo504.abstractmenu.parser.item.InventorySlotParser;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MultipleItemParser implements InventorySlotParser {
    @Override
    public InventorySlot parse(ConfigurationSection section, Map<String, InventorySlotParser> parserData) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(parserData);

        final ConfigurationSection linksSection = section.getConfigurationSection("links");

        if(linksSection == null)
            return null;

        final Map<String, String> links = new HashMap<>();

        for(String key : linksSection.getKeys(false)){
            final String value = linksSection.getString(key);
            if(value != null)
                links.put(key, value);
        }

        final MultipleItem multipleItem = new MultipleItem();

        final Map<String, InventorySlot> items = new HashMap<>();

        for(String key : links.keySet()){
            final ConfigurationSection itemSection = section.getConfigurationSection(key);
            if(itemSection == null)
                continue;

            final String itemParserId = itemSection.getString("parser");
            final InventorySlotParser itemParser = parserData.get(itemParserId);
            if(itemParser == null)
                continue;

            final InventorySlot item = itemParser.parse(itemSection, parserData);
            if(item == null)
                continue;

            items.put(key, item);
        }

        return null;
    }
}
