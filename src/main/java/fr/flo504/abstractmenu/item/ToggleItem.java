package fr.flo504.abstractmenu.item;

import fr.flo504.abstractmenu.utils.Cloneable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ToggleItem implements InventorySlot, Clickable, Cloneable {

    private ItemClickEvent event = new ToggleItemClickEvent();

    private String id;

    private InventorySlot on;
    private InventorySlot off;

    private boolean mustToggleOnClick = true;

    private boolean state;

    public ToggleItem() {}

    public ToggleItem(String id) {
        this.id = id;
    }

    public ToggleItem(String id, InventorySlot on, InventorySlot off) {
        this.id = id;
        this.on = on;
        this.off = off;
    }

    public ToggleItem(String id, InventorySlot on, InventorySlot off, boolean state) {
        this.id = id;
        this.on = on;
        this.off = off;
        this.state = state;
    }

    private ToggleItem(ToggleItem toggleItem) {
        if(!(toggleItem.event instanceof ToggleItemClickEvent))
            this.event = toggleItem.event;
        this.id = toggleItem.id;
        this.on = toggleItem.on;
        this.off = toggleItem.off;
        this.mustToggleOnClick = toggleItem.mustToggleOnClick;
        this.state = toggleItem.state;
    }

    public InventorySlot getOn() {
        return on;
    }

    public void setOn(InventorySlot on) {
        this.on = on;
    }

    public InventorySlot getOff() {
        return off;
    }

    public void setOff(InventorySlot off) {
        this.off = off;
    }

    public boolean isMustToggleOnClick() {
        return mustToggleOnClick;
    }

    public void setMustToggleOnClick(boolean mustToggleOnClick) {
        this.mustToggleOnClick = mustToggleOnClick;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void toggleState(){
        state = !state;
    }

    @Override
    public ItemClickEvent getEvent() {
        return event;
    }

    @Override
    public void setEvent(ItemClickEvent event) {
        this.event = event;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ItemStack getItem(Player player) {
        final InventorySlot current = state ? on : off;
        return current == null ? null : current.getItem(player);
    }

    @Override
    public ToggleItem clone() {
        return new ToggleItem(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToggleItem that = (ToggleItem) o;
        return mustToggleOnClick == that.mustToggleOnClick &&
                state == that.state &&
                Objects.equals(event, that.event) &&
                Objects.equals(id, that.id) &&
                Objects.equals(on, that.on) &&
                Objects.equals(off, that.off);
    }

    @Override
    public String toString() {
        return "ToggleItem{" +
                "event=" + event +
                ", id='" + id + '\'' +
                ", on=" + on +
                ", off=" + off +
                ", mustToggleOnClick=" + mustToggleOnClick +
                ", state=" + state +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, id, on, off, mustToggleOnClick, state);
    }

    private class ToggleItemClickEvent implements ItemClickEvent {
        @Override
        public void onClick(ClickType type, Player player) {
            final ItemClickEvent event;
            final InventorySlot current = state ? on : off;
            if(current instanceof Clickable)
                event = ((Clickable) current).getEvent();
            else
                event = null;

            if(mustToggleOnClick)
                toggleState();

            if(event != null)
                event.onClick(type, player);
        }
    }
}
