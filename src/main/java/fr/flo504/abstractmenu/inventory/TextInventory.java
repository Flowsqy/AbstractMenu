package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.AbstractMenuPlugin;
import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.inventory.anvil.InventoryAnvil;
import fr.flo504.abstractmenu.item.defaults.OpenableItemClickEvent;
import fr.flo504.abstractmenu.parser.inventory.data.TextInventoryInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TextInventory extends BaseInventory implements OpenableItemClickEvent.Openable {

    private final static AbstractMenuPlugin pluginInstance = JavaPlugin.getPlugin(AbstractMenuPlugin.class);

    private String placeholder;
    private BiConsumer<Player, String> callBack;
    private Consumer<Player> closeCallback;

    public TextInventory(String name, MenuFactory factory) {
        super(name, factory);
    }

    public TextInventory(String name, MenuFactory factory, String placeholder) {
        super(name, factory);
        this.placeholder = placeholder;
    }

    public TextInventory(String name, MenuFactory factory, BiConsumer<Player, String> callBack) {
        super(name, factory);
        this.callBack = callBack;
    }

    public TextInventory(String name, MenuFactory factory, String placeholder, BiConsumer<Player, String> callBack) {
        super(name, factory);
        this.placeholder = placeholder;
        this.callBack = callBack;
    }

    public TextInventory(MenuFactory factory, TextInventoryInfo info){
        super(info.getTitle(), factory);
        this.placeholder = info.getPlaceholder();
    }

    public TextInventory(MenuFactory factory, TextInventoryInfo info, BiConsumer<Player, String> callBack){
        super(info.getTitle(), factory);
        this.placeholder = info.getPlaceholder();
        this.callBack = callBack;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public BiConsumer<Player, String> getCallBack() {
        return callBack;
    }

    public void setCallBack(BiConsumer<Player, String> callBack) {
        this.callBack = callBack;
    }

    public Consumer<Player> getCloseCallback() {
        return closeCallback;
    }

    public void setCloseCallback(Consumer<Player> closeCallback) {
        this.closeCallback = closeCallback;
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

    public void onRename(Player player, String name){
        if(callBack != null)
            callBack.accept(player, name);
    }

    @Override
    public void onClose(Player player) {
        final Inventory inventory = player.getOpenInventory().getTopInventory();
        inventory.setItem(0, null);
        inventory.setItem(2, null);
        Bukkit.getScheduler().runTask(pluginInstance, () -> {
            if(closeCallback != null)
                closeCallback.accept(player);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextInventory that = (TextInventory) o;
        return Objects.equals(placeholder, that.placeholder) &&
                Objects.equals(callBack, that.callBack) &&
                Objects.equals(closeCallback, that.closeCallback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeholder, callBack, closeCallback);
    }

    @Override
    public String toString() {
        return "TextInventory{" +
                "placeholder='" + placeholder + '\'' +
                ", callBack=" + callBack +
                ", closeCallback=" + closeCallback +
                "} " + super.toString();
    }
}
