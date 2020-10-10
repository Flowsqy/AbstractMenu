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

public abstract class AbstractSessionInventory<T> extends SessionInventory<T> {

    public AbstractSessionInventory(String name, int line, MenuFactory factory) {
        super(name, line, factory);
    }

    public AbstractSessionInventory(CustomInventoryInfo info, MenuFactory factory){
        this(info.getTitle(), info.getLine(), factory, info.getSlots());
    }

    public AbstractSessionInventory(String name, int line, MenuFactory factory, List<SlotInfo> infos) {
        super(name, line, factory);
        setup(infos);
    }

    protected abstract void setup(List<SlotInfo> infos);

    @Override
    public String toString() {
        return "AbstractSelectInventory{} " + super.toString();
    }

}
