package fr.flo504.abstractmenu.parser.item.defaults;

import fr.flo504.abstractmenu.item.InventorySlot;
import fr.flo504.abstractmenu.item.ToggleItem;
import fr.flo504.abstractmenu.parser.item.InventorySlotParser;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public class ToggleItemParser implements InventorySlotParser {
    @Override
    public InventorySlot parse(ConfigurationSection section, Map<String, InventorySlotParser> parserData) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(parserData);

        final ConfigurationSection itemFirstSection = section.getConfigurationSection("first");
        if(itemFirstSection == null)
            return null;

        final String itemFirstParserId = itemFirstSection.getString("parser");
        final InventorySlotParser itemFirstParser = parserData.get(itemFirstParserId);
        if(itemFirstParser == null)
            return null;

        final InventorySlot first = itemFirstParser.parse(itemFirstSection, parserData);
        if(first == null)
            return null;
        
        final ConfigurationSection itemSecondSection = section.getConfigurationSection("second");
        if(itemSecondSection == null)
            return null;

        final String itemSecondParserId = itemSecondSection.getString("parser");
        final InventorySlotParser itemSecondParser = parserData.get(itemSecondParserId);
        if(itemSecondParser == null)
            return null;

        final InventorySlot second = itemSecondParser.parse(itemSecondSection, parserData);
        if(second == null)
            return null;
        
        final ToggleItem toggleItem = new ToggleItem();

        toggleItem.setId(section.getName());

        toggleItem.registerItems(first, second);

        return toggleItem;
    }
}
