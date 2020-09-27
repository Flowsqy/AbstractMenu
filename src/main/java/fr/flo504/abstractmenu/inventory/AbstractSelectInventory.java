package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.parser.inventory.data.CustomInventoryInfo;
import fr.flo504.abstractmenu.parser.inventory.data.SlotInfo;

import java.util.List;

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

    @Override
    public String toString() {
        return "AbstractSelectInventory{} " + super.toString();
    }

}
