package fr.flo504.abstractmenu.parser.item;

import fr.flo504.abstractmenu.item.InventorySlot;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public interface InventorySlotParser {

    InventorySlot parse(ConfigurationSection section, Map<String, InventorySlotParser> parserData);

}
