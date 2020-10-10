package fr.flo504.abstractmenu.item.defaults;

import fr.flo504.abstractmenu.item.ItemClickEvent;
import fr.flo504.abstractmenu.utils.Cloneable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

public class OpenableItemClickEvent implements ItemClickEvent, Cloneable {

    private final Openable openable;

    public OpenableItemClickEvent(Openable openable) {
        this.openable = openable;
    }

    private OpenableItemClickEvent(OpenableItemClickEvent event){
        this.openable = event.openable;
    }

    public Openable getOpenable() {
        return openable;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if(openable != null)
            openable.open(player);
    }

    @Override
    public OpenableItemClickEvent clone() {
        return new OpenableItemClickEvent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenableItemClickEvent that = (OpenableItemClickEvent) o;
        return Objects.equals(openable, that.openable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openable);
    }

    @Override
    public String toString() {
        return "OpenableItemClickEvent{" +
                "openable=" + openable +
                '}';
    }

    public interface Openable{

        void open(Player player);

    }

}
