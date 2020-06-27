package fr.flo504.abstractmenu.inventory;

import fr.flo504.abstractmenu.factory.MenuFactory;
import fr.flo504.abstractmenu.parser.inventory.InventoryInfo;
import fr.flo504.abstractmenu.parser.inventory.SlotInfo;

import java.util.List;

public abstract class AbstractInventory extends BaseInventory {

    public AbstractInventory(InventoryInfo info, MenuFactory factory){
        this(info.getTitle(), info.getLine(), factory, info.getSlots());
    }

    public AbstractInventory(String name, int line, MenuFactory factory, List<SlotInfo> infos) {
        super(name, line, factory);
        setup(infos);
    }

    protected abstract void setup(List<SlotInfo> infos);

    @Override
    public String toString() {
        return "AbstractInventory{} " + super.toString();
    }
}
