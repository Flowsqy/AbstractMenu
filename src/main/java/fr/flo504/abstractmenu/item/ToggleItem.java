package fr.flo504.abstractmenu.item;

import fr.flo504.abstractmenu.inventory.InventorySlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.function.BiPredicate;

public class ToggleItem extends MultipleItem  {

    public final void registerItems(InventorySlot first, InventorySlot second){
        registerItems(first, second, ALWAYS, ALWAYS);
    }

    public final void registerItems(InventorySlot first, InventorySlot second, BiPredicate<Player, ClickType> firstPredicate, BiPredicate<Player, ClickType> secondPredicate){
        registerItem(second, first, secondPredicate);
        registerItem(first, second, firstPredicate);
    }

}
