package fr.flo504.abstractmenu.parser.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemParser {

    public static ItemInfo parseItem(ConfigurationSection section){
        Objects.requireNonNull(section);

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

        return new ItemInfo(
                id,
                material,
                name,
                lore,
                amount,
                glow
        );
    }

}
