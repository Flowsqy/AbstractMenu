package fr.flo504.abstractmenu.parser.inventory;

import fr.flo504.abstractmenu.item.InventorySlot;
import fr.flo504.abstractmenu.parser.item.InventorySlotParser;
import fr.flo504.abstractmenu.parser.item.ItemParser;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryParser {

    public static InventoryInfo parseInventory(ConfigurationSection section, Map<String, InventorySlotParser> parserData){
        Objects.requireNonNull(section);
        Objects.requireNonNull(parserData);

        final String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(section.getString("title", "")));
        int line = section.getInt("line", 1);

        if(line < 1)
            line = 1;
        else if(line > 6)
            line = 6;

        final int maxSlot = line*9;

        final List<SlotInfo> slotInfo = new ArrayList<>();

        final ConfigurationSection items = section.getConfigurationSection("items");
        if(items != null){
            for(String key : items.getKeys(false)){
                final ConfigurationSection slotSection = items.getConfigurationSection(key);
                assert slotSection != null : "Section is gotten from key name, so can not be null";

                final String parserName = slotSection.getString("parser");
                if(parserName == null)
                    continue;

                final InventorySlotParser parser = parserData.get(parserName);
                if(parser == null)
                    continue;

                final List<Integer> slots = slotSection.getIntegerList("slots").stream()
                        .filter(slot -> slot >= 0 && slot < maxSlot)
                        .collect(Collectors.toList());
                if(slots.isEmpty())
                    continue;

                final Optional<String> itemKey = slotSection
                        .getKeys(false)
                        .stream()
                        .filter(possibleItemKey -> !possibleItemKey.equals("slots") && !possibleItemKey.equals("parser"))
                        .findFirst();

                if(!itemKey.isPresent())
                    continue;

                final InventorySlot info = ItemParser.parseItem(slotSection.getConfigurationSection(itemKey.get()), parser, parserData);

                if(info == null)
                    continue;

                slotInfo.add(
                        new SlotInfo(
                                info,
                                slots
                        )
                );
            }
        }

        return new InventoryInfo(
                title,
                line,
                slotInfo
        );
    }

}
