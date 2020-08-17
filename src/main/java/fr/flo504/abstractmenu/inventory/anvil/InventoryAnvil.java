package fr.flo504.abstractmenu.inventory.anvil;

import fr.flo504.reflect.Reflect;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryAnvil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;

import java.lang.reflect.Field;

public class InventoryAnvil {

    //TODO Some reflection for other version support

    public static AnvilInventory create(String title, Player owner){
        final CraftPlayer craftPlayer = (CraftPlayer)owner;
        final EntityPlayer entityPlayer = craftPlayer.getHandle();

        final ContainerAnvil anvilContainer = new ContainerAnvil(
                entityPlayer.nextContainerCounter(),
                entityPlayer.inventory,
                ContainerAccess.at(((CraftWorld)owner.getWorld()).getHandle(), BlockPosition.ZERO)
        );
        anvilContainer.checkReachable = false;
        anvilContainer.setTitle(new ChatMessage(title));

        return (AnvilInventory) anvilContainer.getBukkitView().getTopInventory();
    }

    public static void open(Player player, AnvilInventory inventory){
        final CraftInventoryAnvil craftInventoryAnvil = (CraftInventoryAnvil) inventory;

        final Field containerField = Reflect.getField(CraftInventoryAnvil.class, "container");
        containerField.setAccessible(true);

        final ContainerAnvil containerAnvil = (ContainerAnvil) Reflect.get(containerField, craftInventoryAnvil);

        final CraftPlayer craftPlayer = (CraftPlayer)player;
        final EntityPlayer entityPlayer = craftPlayer.getHandle();

        final int id = containerAnvil.windowId;

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(id, Containers.ANVIL, containerAnvil.getTitle()));

        entityPlayer.activeContainer = containerAnvil;
        containerAnvil.addSlotListener(entityPlayer);
    }

}
