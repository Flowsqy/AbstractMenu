package fr.flo504.abstractmenu.item;

import fr.flo504.abstractmenu.utils.Cloneable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class MultipleItem implements InventorySlot, Clickable, Cloneable, Iterable<Map.Entry<InventorySlot, MultipleItem.ItemLink>> {

    protected final static BiPredicate<Player, ClickType> ALWAYS = (player, clickType) -> true;

    private final Map<InventorySlot, ItemLink> links;
    private final ItemClickEvent event = new MultipleItemClickEvent();

    private InventorySlot currentItem;
    private ItemClickEvent currentEvent;

    public MultipleItem() {
        links = new HashMap<>();
    }

    private MultipleItem(MultipleItem item){
        this.links = new HashMap<>(item.links);
        this.currentItem = item.currentItem;
        this.currentEvent = item.currentEvent;
    }

    @Override
    public ItemStack getItem(Player player) {
        return currentItem == null ? null : currentItem.getItem(player);
    }

    @Override
    public ItemClickEvent getEvent() {
        return event;
    }

    @Override
    public void setEvent(ItemClickEvent event) {
        this.currentEvent = event;
    }

    public MultipleItem registerItem(InventorySlot item, InventorySlot linked){
        return registerItem(item, linked, ALWAYS);
    }

    public MultipleItem registerItem(InventorySlot item, InventorySlot linked, BiPredicate<Player, ClickType> predicate){
        Objects.requireNonNull(item);
        Objects.requireNonNull(linked);
        Objects.requireNonNull(predicate);

        links.put(item, new ItemLink(linked, predicate));
        currentItem = linked;
        currentEvent = event;

        return this;
    }

    public void setCurrent(InventorySlot slot){
        currentItem = slot;
        currentEvent = currentItem instanceof Clickable ? ((Clickable) currentItem).getEvent() : null;
    }

    @Override
    public MultipleItem clone() {
        return new MultipleItem(this);
    }

    @Override
    public Iterator<Map.Entry<InventorySlot, ItemLink>> iterator() {
        return links.entrySet().iterator();
    }

    public Stream<Map.Entry<InventorySlot, ItemLink>> stream(){
        return links.entrySet().stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultipleItem that = (MultipleItem) o;
        return links.equals(that.links) &&
                event.equals(that.event) &&
                Objects.equals(currentItem, that.currentItem) &&
                Objects.equals(currentEvent, that.currentEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, event, currentItem, currentEvent);
    }

    @Override
    public String toString() {
        return "MultipleItem{" +
                "links=" + links +
                ", event=" + event +
                ", currentItem=" + currentItem +
                ", currentEvent=" + currentEvent +
                '}';
    }

    public static final class ItemLink {

        private InventorySlot item;
        private BiPredicate<Player, ClickType> predicate;

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

        public void setItem(InventorySlot item) {
            this.item = item;
        }

        public void setPredicate(BiPredicate<Player, ClickType> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemLink itemLink = (ItemLink) o;
            return Objects.equals(item, itemLink.item) &&
                    Objects.equals(predicate, itemLink.predicate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, predicate);
        }

        @Override
        public String toString() {
            return "ItemLink{" +
                    "item=" + item +
                    ", predicate=" + predicate +
                    '}';
        }
    }

    private final class MultipleItemClickEvent implements ItemClickEvent {

        @Override
        public void onClick(ClickType type, Player player) {
            final ItemClickEvent clicked = currentEvent;
            final ItemLink itemLink = links.get(currentItem);
            if(itemLink != null) {
                if (itemLink.getPredicate().test(player, type)) {
                    setCurrent(itemLink.getItem());
                }
            }

            if(clicked != null)
                clicked.onClick(type, player);

        }

    }

}
