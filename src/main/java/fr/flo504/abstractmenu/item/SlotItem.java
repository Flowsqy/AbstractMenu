package fr.flo504.abstractmenu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SlotItem extends BaseItem implements InventorySlot {

    public final static Function<Player, ItemStack> NULL = (player) -> null;

    private Function<Player, ItemStack> itemSlotFunction;

    public SlotItem(String id, ItemStack item) {
        super(id, item);
    }

    public SlotItem(String id, String name, Material material, List<String> lore, boolean glow, int amount) {
        super(id, name, material, lore, glow, amount);
    }

    public SlotItem(String id, ItemStack item, Function<Player, ItemStack> itemSlotFunction) {
        super(id, item);
        this.itemSlotFunction = itemSlotFunction;
    }

    public SlotItem(String id, String name, Material material, List<String> lore, boolean glow, int amount, Function<Player, ItemStack> itemSlotFunction) {
        super(id, name, material, lore, glow, amount);
        this.itemSlotFunction = itemSlotFunction;
    }

    protected SlotItem(SlotItem item){
        super(item);
        this.itemSlotFunction = item.itemSlotFunction;
    }

    public Function<Player, ItemStack> getItemSlotFunction() {
        return itemSlotFunction;
    }

    public void setItemSlotFunction(Function<Player, ItemStack> itemSlotFunction) {
        this.itemSlotFunction = itemSlotFunction;
    }

    @Override
    public ItemStack getItem(Player player) {
        return itemSlotFunction == null ? getItem() : itemSlotFunction.apply(player);
    }

    @Override
    public SlotItem clone() {
        return new SlotItem(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SlotItem slotItem = (SlotItem) o;
        return Objects.equals(itemSlotFunction, slotItem.itemSlotFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemSlotFunction);
    }

    @Override
    public String toString() {
        return "SlotItem{" +
                "itemSlotFunction=" + itemSlotFunction +
                "} " + super.toString();
    }
}
