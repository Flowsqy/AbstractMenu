package fr.flo504.abstractmenu.item.defaults;

import fr.flo504.abstractmenu.item.ItemClickEvent;
import fr.flo504.abstractmenu.item.ToggleItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;


public class GroupItemClickEvent implements ItemClickEvent{
    private final ToggleItem item;
    private final List<ToggleItem> other = new ArrayList<>();

    public GroupItemClickEvent(ToggleItem item) {
        this.item = item;
    }

    public ToggleItem getItem() {
        return item;
    }

    public List<ToggleItem> getOther() {
        return other;
    }

    public final void registerOther(ToggleItem... other){
        this.other.addAll(Arrays.stream(other).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public final void registerOther(Collection<ToggleItem> other){
        this.other.addAll(other.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Override
    public void onClick(ClickType type, Player player) {
        for(ToggleItem item : other){
            if(item.isState()) {
                item.toggleState();
                item.setMustToggleOnClick(true);
            }
        }

        item.setMustToggleOnClick(false);
    }

    public static void linkOther(ToggleItem... toggleItems){
        for(ToggleItem item : toggleItems){
            if(!(item.getEvent() instanceof GroupItemClickEvent))
                continue;
            ((GroupItemClickEvent) item.getEvent()).registerOther(
                    Arrays.stream(toggleItems)
                            .filter(toggleItem -> toggleItem != item)
                            .collect(Collectors.toList())
            );
        }
    }

}
