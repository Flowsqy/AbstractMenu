package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.inventory.anvil.InventoryAnvil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class TextInventory extends BaseInventory{

    private String placeholder;

    public TextInventory(String name, MenuFactory factory) {
        super(name, factory);
    }

    public TextInventory(String name, MenuFactory factory, String placeholder) {
        super(name, factory);
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public void open(Player player) {
        final AnvilInventory inventory = InventoryAnvil.create(name, player);

        final ItemStack itemStack = new ItemStack(Material.PAPER);
        final ItemMeta meta = itemStack.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(placeholder);
        itemStack.setItemMeta(meta);
        inventory.setItem(0, itemStack);

        InventoryAnvil.open(player, inventory);

        getFactory().registerInventory(this, inventory);
    }

    @Override
    public void onClick(Player player, ItemStack item, ClickType clickType, int slot, boolean customInventory, InventoryClickEvent e) {
        super.onClick(player, item, clickType, slot, customInventory, e);

        if(e.getRawSlot() == 2 && item != null && item.getType() != Material.AIR){
            onRename(player, Objects.requireNonNull(item.getItemMeta()).getDisplayName());

            player.closeInventory();
        }
    }

    public void onRename(Player player, String name){}

    @Override
    public void onClose(Player player) {
        player.getOpenInventory().getTopInventory().setItem(0, null);
    }
}
