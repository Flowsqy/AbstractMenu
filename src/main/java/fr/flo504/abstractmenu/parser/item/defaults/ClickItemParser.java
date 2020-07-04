package fr.flo504.abstractmenu.parser.item.defaults;

import fr.flo504.abstractmenu.item.ClickableItem;
import fr.flo504.abstractmenu.parser.item.InventorySlotParser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClickItemParser implements InventorySlotParser {
    @Override
    public ClickableItem parse(ConfigurationSection section, Map<String, InventorySlotParser> parserData) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(parserData);

        final String id = section.getName();

        final String materialName = section.getString("material");

        if(materialName == null)
            return null;

        Material material = null;
        for(Material possibleMaterial : Material.values()){
            if(materialName.equalsIgnoreCase(possibleMaterial.name())) {
                material = possibleMaterial;
                break;
            }
        }

        if(material == null)
            return null;

        String name = section.getString("name");
        if(name != null)
            name = ChatColor.translateAlternateColorCodes('&', name);
        final List<String> lore = section.getStringList("lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        final int amount = section.getInt("amount", 0);
        final boolean glow = section.getBoolean("glow", false);

        return new ClickableItem(
                id,
                name,
                material,
                lore,
                glow,
                amount
        );
    }
}