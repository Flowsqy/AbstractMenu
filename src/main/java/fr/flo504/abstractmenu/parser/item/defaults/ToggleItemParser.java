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

        final ConfigurationSection itemOnSection = section.getConfigurationSection("on");
        if(itemOnSection == null)
            return null;

        final String itemOnParserId = itemOnSection.getString("parser");
        final InventorySlotParser itemOnParser = parserData.get(itemOnParserId);
        if(itemOnParser == null)
            return null;

        final InventorySlot on = itemOnParser.parse(itemOnSection, parserData);
        if(on == null)
            return null;
        
        final ConfigurationSection itemOffSection = section.getConfigurationSection("off");
        if(itemOffSection == null)
            return null;

        final String itemOffParserId = itemOffSection.getString("parser");
        final InventorySlotParser itemOffParser = parserData.get(itemOffParserId);
        if(itemOffParser == null)
            return null;

        final InventorySlot off = itemOffParser.parse(itemOffSection, parserData);
        if(off == null)
            return null;
        
        final ToggleItem toggleItem = new ToggleItem();

        toggleItem.setId(section.getName());

        toggleItem.setOn(on);
        toggleItem.setOff(off);

        return toggleItem;
    }
}
