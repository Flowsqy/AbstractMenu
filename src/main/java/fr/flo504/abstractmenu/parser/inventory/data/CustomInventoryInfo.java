package fr.flo504.abstractmenu.parser.inventory.data;

import java.util.List;
import java.util.Objects;

public class CustomInventoryInfo extends BaseInventoryInfo{

    private final int line;
    private final List<SlotInfo> slots;

    public CustomInventoryInfo(String title, int line, List<SlotInfo> slots) {
        super(title);
        Objects.requireNonNull(slots);
        this.line = line < 1 ? 1 : Math.min(line, 6);
        this.slots = slots;
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
        if (!super.equals(o)) return false;
        CustomInventoryInfo that = (CustomInventoryInfo) o;
        return line == that.line &&
                slots.equals(that.slots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), line, slots);
    }

    @Override
    public String toString() {
        return "CustomInventoryInfo{" +
                "line=" + line +
                ", slots=" + slots +
                "} " + super.toString();
    }
}
