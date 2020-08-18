package fr.flo504.abstractmenu;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.inventory.BaseInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class AbstractMenuPlugin extends JavaPlugin implements Listener {

    private final Map<Plugin, List<MenuFactory>> pluginFactories = new HashMap<>();

    @Override
    public void onEnable() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Reflect");
        if(plugin == null || !plugin.isEnabled()){
            this.getLogger().log(Level.WARNING, "There is not reflect api plugin, disabling the api");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        final Map<Inventory, BaseInventory> customInventories = new HashMap<>();
        for(List<MenuFactory> factories : pluginFactories.values()){
            for(MenuFactory factory : factories){
                customInventories.putAll(factory.getInventories());
            }
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            final Inventory inventory = player.getOpenInventory().getTopInventory();
            final BaseInventory customInventory = customInventories.get(inventory);
            if(customInventory != null) {
                customInventory.onClose(player);
                player.closeInventory();
            }
        }
    }

    public void register(Plugin plugin, MenuFactory factory) {
        List<MenuFactory> factories = pluginFactories.get(plugin);
        if(factories == null)
            factories = new ArrayList<>();
        factories.add(factory);

        pluginFactories.put(plugin, factories);
    }

    public List<MenuFactory> getFactories(Plugin plugin){
        return pluginFactories.get(plugin);
    }

}
