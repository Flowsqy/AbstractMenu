package fr.flo504.abstractmenu.parser.inventory;

import fr.flo504.abstractmenu.parser.item.ItemInfo;

import java.util.List;
import java.util.Objects;

public class SlotInfo {

    private final ItemInfo item;
    private final List<Integer> slots;

    public SlotInfo(ItemInfo item, List<Integer> slots) {
        Objects.requireNonNull(item);
        Objects.requireNonNull(slots);
        this.item = item;
        this.slots = slots;
    }

    public ItemInfo getItem() {
        return item;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotInfo slotInfo = (SlotInfo) o;
        return item.equals(slotInfo.item) &&
                slots.equals(slotInfo.slots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, slots);
    }

    @Override
    public String toString() {
        return "SlotInfo{" +
                "item=" + item +
                ", slots=" + slots +
                '}';
    }
}
