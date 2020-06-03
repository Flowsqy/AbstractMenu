package fr.flo504.abstractmenu.item;

import fr.flo504.abstractmenu.inventory.InventorySlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

public class MultipleItem implements InventorySlot {

    private final BiPredicate<Player, ClickType> ALWAYS = (player, clickType) -> true;

    private final Map<AbstractItem, ItemLink> links;

    private AbstractItem currentItem;

    public MultipleItem() {
        links = new HashMap<>();
    }

    @Override
    public ItemStack getItem(Player player) {
        return currentItem == null ? null : currentItem.getItem(player);
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        currentItem.onClick(player, clickType);
        final ItemLink itemLink = links.get(currentItem);
        if(itemLink.getPredicate().test(player, clickType))
            currentItem = itemLink.getItem();
    }

    public MultipleItem registerItem(AbstractItem item, AbstractItem linked){
        return registerItem(item, linked, ALWAYS);
    }

    public MultipleItem registerItem(AbstractItem item, AbstractItem linked, BiPredicate<Player, ClickType> predicate){
        Objects.requireNonNull(item);
        Objects.requireNonNull(linked);
        Objects.requireNonNull(predicate);

        links.put(item, new ItemLink(linked, predicate));

        return this;
    }

    private static final class ItemLink {

        private final AbstractItem item;
        private final BiPredicate<Player, ClickType> predicate;

        public ItemLink(AbstractItem item, BiPredicate<Player, ClickType> predicate) {
            this.item = item;
            this.predicate = predicate;
        }

        public AbstractItem getItem() {
            return item;
        }

        public BiPredicate<Player, ClickType> getPredicate() {
            return predicate;
        }
    }

}
