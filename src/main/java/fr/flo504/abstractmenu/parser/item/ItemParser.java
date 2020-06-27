package fr.flo504.abstractmenu.parser.item;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public class ItemParser {

    public static InventorySlotParser.ItemData parseItem(ConfigurationSection section, InventorySlotParser parser, Map<String, InventorySlotParser> parserData){
        Objects.requireNonNull(parser);
        return parser.parse(section, parserData);
    }

}
