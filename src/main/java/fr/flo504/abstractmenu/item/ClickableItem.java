package fr.flo504.abstractmenu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ClickableItem extends SlotItem {

    private ItemClickEvent event;

    public ClickableItem(String id, ItemStack item) {
        super(id, item);
    }

    public ClickableItem(String id, String name, Material material, List<String> lore, boolean glow, int amount) {
        super(id, name, material, lore, glow, amount);
    }

    public ClickableItem(String id, ItemStack item, Function<Player, ItemStack> itemSlotFunction) {
        super(id, item, itemSlotFunction);
    }

    public ClickableItem(String id, String name, Material material, List<String> lore, boolean glow, int amount, Function<Player, ItemStack> itemSlotFunction) {
        super(id, name, material, lore, glow, amount, itemSlotFunction);
    }

    public ClickableItem(String id, ItemStack item, ItemClickEvent event) {
        super(id, item);
        setEvent(event);
    }

    public ClickableItem(String id, String name, Material material, List<String> lore, boolean glow, int amount, ItemClickEvent event) {
        super(id, name, material, lore, glow, amount);
        this.event = event;
    }

    public ClickableItem(String id, ItemStack item, Function<Player, ItemStack> itemSlotFunction, ItemClickEvent event) {
        super(id, item, itemSlotFunction);
        this.event = event;
    }

    public ClickableItem(String id, String name, Material material, List<String> lore, boolean glow, int amount, Function<Player, ItemStack> itemSlotFunction, ItemClickEvent event) {
        super(id, name, material, lore, glow, amount, itemSlotFunction);
        this.event = event;
    }

    public ItemClickEvent getEvent() {
        return event;
    }

    public void setEvent(ItemClickEvent event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClickableItem that = (ClickableItem) o;
        return Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), event);
    }

    @Override
    public String toString() {
        return "ClickableItem{" +
                "event=" + event +
                "} " + super.toString();
    }
}
