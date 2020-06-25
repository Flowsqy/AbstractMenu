package fr.flo504.abstractmenu.item;

import fr.flo504.abstractmenu.inventory.InventorySlot;
import fr.flo504.abstractmenu.utils.Cloneable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

public class MultipleItem implements InventorySlot, Cloneable {

    protected final static BiPredicate<Player, ClickType> ALWAYS = (player, clickType) -> true;

    private final Map<InventorySlot, ItemLink> links;

    private InventorySlot currentItem;

    public MultipleItem() {
        links = new HashMap<>();
    }

    private MultipleItem(MultipleItem item){
        this.links = new HashMap<>(item.links);
        this.currentItem = item.currentItem;
    }

    @Override
    public ItemStack getItem(Player player) {
        return currentItem == null ? null : currentItem.getItem(player);
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        final InventorySlot clicked = currentItem;
        final ItemLink itemLink = links.get(currentItem);
        if(itemLink != null)
            if(itemLink.getPredicate().test(player, clickType))
                currentItem = itemLink.getItem();

        clicked.onClick(player, clickType);
    }

    public MultipleItem registerItem(InventorySlot item, InventorySlot linked){
        return registerItem(item, linked, ALWAYS);
    }

    public MultipleItem registerItem(InventorySlot item, InventorySlot linked, BiPredicate<Player, ClickType> predicate){
        Objects.requireNonNull(item);
        Objects.requireNonNull(linked);
        Objects.requireNonNull(predicate);

        links.put(item, new ItemLink(linked, predicate));
        currentItem = item;

        return this;
    }

    @Override
    public MultipleItem clone() {
        return new MultipleItem(this);
    }

    private static final class ItemLink {

        private final InventorySlot item;
        private final BiPredicate<Player, ClickType> predicate;

        public ItemLink(InventorySlot item, BiPredicate<Player, ClickType> predicate) {
            this.item = item;
            this.predicate = predicate;
        }

        public InventorySlot getItem() {
            return item;
        }

        public BiPredicate<Player, ClickType> getPredicate() {
            return predicate;
        }
    }

    public static final class ISGInventorySlot implements InventorySlot {

        private final ItemStackGetter itemStackGetter;

        public ISGInventorySlot(ItemStackGetter itemStackGetter) {
            this.itemStackGetter = itemStackGetter;
        }

        @Override
        public ItemStack getItem(Player player) {
            return itemStackGetter.getItem();
        }

        @Override
        public void onClick(Player player, ClickType clickType) {}
    }

}
