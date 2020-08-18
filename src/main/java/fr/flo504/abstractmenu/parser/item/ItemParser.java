package fr.flo504.abstractmenu.parser.item;

import fr.flo504.abstractmenu.item.InventorySlot;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public class ItemParser {

    public static InventorySlot parseItem(ConfigurationSection section, InventorySlotParser parser, Map<String, InventorySlotParser> parserData){
        Objects.requireNonNull(parser);
        return parser.parse(section, parserData);
    }

}
