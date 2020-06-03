package fr.flo504.abstractmenu.parser.inventory;

import java.util.List;
import java.util.Objects;

public class InventoryInfo {

    private final String title;
    private final int line;
    private final List<SlotInfo> slots;

    public InventoryInfo(String title, int line, List<SlotInfo> slots) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(slots);
        this.title = title;
        this.line = line < 1 ? 1 : Math.min(line, 6);
        this.slots = slots;
    }

    public String getTitle() {
        return title;
    }

    public int getLine() {
        return line;
    }

    public List<SlotInfo> getSlots() {
        return slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryInfo that = (InventoryInfo) o;
        return line == that.line &&
                title.equals(that.title) &&
                slots.equals(that.slots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, line, slots);
    }

    @Override
    public String toString() {
        return "InventoryInfo{" +
                "title='" + title + '\'' +
                ", line=" + line +
                ", slots=" + slots +
                '}';
    }
}
