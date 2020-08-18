package fr.flo504.abstractmenu.parser.inventory;

import fr.flo504.abstractmenu.parser.inventory.data.TextInventoryInfo;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class TextInventoryParser {

    public static TextInventoryInfo parseInventory(ConfigurationSection section){
        Objects.requireNonNull(section);

        final String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(section.getString("title", "")));

        String placeHolder = section.getString("placeholder");
        if(placeHolder != null)
            placeHolder = ChatColor.translateAlternateColorCodes('&', placeHolder);

        return new TextInventoryInfo(
                title,
                placeHolder
        );
    }

}
