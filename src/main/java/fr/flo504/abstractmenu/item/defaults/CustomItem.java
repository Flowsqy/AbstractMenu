package fr.flo504.abstractmenu.item.defaults;

import fr.flo504.abstractmenu.item.AbstractItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CustomItem extends AbstractItem {

    private BiConsumer<Player, ClickType> action;
    private Function<Player, ItemStack> specificItemFunction;

    public CustomItem(ItemStack item) {
        super(item);
        this.action = null;
    }

    public CustomItem(String name, Material material, List<String> lore, boolean glow, int amount) {
        super(name, material, lore, glow, amount);
        this.action = null;
    }

    public CustomItem(CustomItem item) {
        super(item);
    }

    public CustomItem(ItemStack item, BiConsumer<Player, ClickType> action) {
        super(item);
        this.action = action;
    }

    public CustomItem(String name, Material material, List<String> lore, boolean glow, int amount, BiConsumer<Player, ClickType> action) {
        super(name, material, lore, glow, amount);
        this.action = action;
    }

    public CustomItem(CustomItem item, BiConsumer<Player, ClickType> action) {
        super(item);
        this.action = action;
    }

    public CustomItem(ItemStack item, Function<Player, ItemStack> specificItemFunction) {
        super(item);
        this.action = null;
        this.specificItemFunction = specificItemFunction;
    }

    public CustomItem(String name, Material material, List<String> lore, boolean glow, int amount, Function<Player, ItemStack> specificItemFunction) {
        super(name, material, lore, glow, amount);
        this.action = null;
        this.specificItemFunction = specificItemFunction;
    }

    public CustomItem(CustomItem item, Function<Player, ItemStack> specificItemFunction) {
        super(item);
        this.specificItemFunction = specificItemFunction;
    }

    public CustomItem(ItemStack item, BiConsumer<Player, ClickType> action, Function<Player, ItemStack> specificItemFunction) {
        super(item);
        this.action = action;
        this.specificItemFunction = specificItemFunction;
    }

    public CustomItem(String name, Material material, List<String> lore, boolean glow, int amount, BiConsumer<Player, ClickType> action, Function<Player, ItemStack> specificItemFunction) {
        super(name, material, lore, glow, amount);
        this.action = action;
        this.specificItemFunction = specificItemFunction;
    }

    public CustomItem(CustomItem item, BiConsumer<Player, ClickType> action, Function<Player, ItemStack> specificItemFunction) {
        super(item);
        this.action = action;
        this.specificItemFunction = specificItemFunction;
    }

    public BiConsumer<Player, ClickType> getAction() {
        return action;
    }

    public void setAction(BiConsumer<Player, ClickType> action) {
        this.action = action;
    }

    public Function<Player, ItemStack> getSpecificItemFunction() {
        return specificItemFunction;
    }

    public void setSpecificItemFunction(Function<Player, ItemStack> specificItemFunction) {
        this.specificItemFunction = specificItemFunction;
    }

    @Override
    public ItemStack getItem(Player player) {
        return specificItemFunction == null ? this.getItem() : specificItemFunction.apply(player);
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if(action != null)
            action.accept(player, clickType);
    }
}
