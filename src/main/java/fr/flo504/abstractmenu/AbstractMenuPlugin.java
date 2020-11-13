package fr.flo504.abstractmenu;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class AbstractMenuPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Reflect");
        if(plugin == null || !plugin.isEnabled()){
            this.getLogger().log(Level.WARNING, "There is not reflect api plugin, disabling the api");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

}
