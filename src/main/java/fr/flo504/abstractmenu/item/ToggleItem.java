package fr.flo504.abstractmenu.item;

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

    public final void registerItems(InventorySlot first, InventorySlot second, ItemClickEvent firstEvent, ItemClickEvent secondEvent){
        registerItems(first, second, ALWAYS, ALWAYS, firstEvent, secondEvent);
    }

    public final void registerItems(InventorySlot first, InventorySlot second, BiPredicate<Player, ClickType> firstPredicate, BiPredicate<Player, ClickType> secondPredicate, ItemClickEvent firstEvent, ItemClickEvent secondEvent){
        registerItem(first, second, firstPredicate, firstEvent);
        registerItem(second, first, secondPredicate, secondEvent);
    }

}
