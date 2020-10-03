package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.item.Clickable;
import fr.flo504.abstractmenu.item.InventorySlot;
import fr.flo504.abstractmenu.item.ToggleItem;
import fr.flo504.abstractmenu.item.defaults.GroupItemClickEvent;
import fr.flo504.abstractmenu.parser.inventory.data.CustomInventoryInfo;
import fr.flo504.abstractmenu.parser.inventory.data.SlotInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractSelectInventory<T, K> extends SelectInventory<T, K> {

    public AbstractSelectInventory(String name, int line, MenuFactory factory) {
        super(name, line, factory);
    }

    public AbstractSelectInventory(CustomInventoryInfo info, MenuFactory factory){
        this(info.getTitle(), info.getLine(), factory, info.getSlots());
    }

    public AbstractSelectInventory(String name, int line, MenuFactory factory, List<SlotInfo> infos) {
        super(name, line, factory);
        setup(infos);
    }

    protected abstract void setup(List<SlotInfo> infos);

    public final static class ToggleGroupParser {

        private final Map<String, Function<ToggleItem, GroupItemClickEvent>> eventsById = new HashMap<>();
        private final List<Integer> toggleSlots = new ArrayList<>();

        public final void register(String id, Function<ToggleItem, GroupItemClickEvent> event){
            eventsById.put(id, event);
        }

        public final void check(SlotInfo info){

            if (info.getSlots().isEmpty())
                return;

            final InventorySlot slot = info.getItem();

            if(slot instanceof ToggleItem) {
                final ToggleItem toggleItem = (ToggleItem) slot;
                if (!(toggleItem.getOff() instanceof Clickable))
                    return;

                final Function<ToggleItem, GroupItemClickEvent> event = eventsById.get(toggleItem.getId());

                if(event == null)
                    return;

                final Clickable clickable = (Clickable) toggleItem.getOff();
                final int position = info.getSlots().get(0);

                clickable.setEvent(event.apply(toggleItem));

                if(!toggleSlots.contains(position)){
                    toggleSlots.add(position);
                }

            }
        }

        public final List<Integer> getToggleSlots(){
            return toggleSlots;
        }

    }

    @Override
    public String toString() {
        return "AbstractSelectInventory{} " + super.toString();
    }

}
