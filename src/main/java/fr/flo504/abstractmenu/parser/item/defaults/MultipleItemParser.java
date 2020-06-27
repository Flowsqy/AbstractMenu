package fr.flo504.abstractmenu.parser.item.defaults;

import fr.flo504.abstractmenu.parser.item.InventorySlotParser;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public class MultipleItemParser implements InventorySlotParser {
    @Override
    public ItemData parse(ConfigurationSection section, Map<String, InventorySlotParser> parserData) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(parserData);

        final String firstId = section.getString("first");

        if(firstId == null || firstId.replace(" ", "").equals(""))
            return null;

        for(String key : section.getKeys(false));

        return null;
    }
}
