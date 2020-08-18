package fr.flo504.abstractmenu.inventory.anvil;

import fr.flo504.reflect.Reflect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InventoryAnvil {

    private static final Method getHandleCraftPlayer;
    private static final Constructor<?> containerAnvilConstructor;
    private static final Method nextContainerCounterMethod;
    private static final Field inventoryField;
    private static final Object zeroBlockPosition;
    private static final Method atContainerAccess;
    private static final Method getHandleCraftWorld;
    private static final Field checkReachableField;
    private static final Method setTitleMethod;
    private static final Constructor<?> chatMessageConstructor;
    private static final Method getBukkitViewMethod;
    private static final Field containerField;
    private static final Field windowIdField;
    private static final Field playerConnectionField;
    private static final Method sendPacketMethod;
    private static final Constructor<?> packetPlayOutOpenWindowConstructor;
    private static final Object anvil;
    private static final Method getTitleMethod;
    private static final Field activeContainerField;
    private static final Method addSlotListenerMethod;

    static{
        final Class<?> craftPlayerClass = Reflect.getClass(Reflect.NMS.CRAFTBUKKIT+"entity.CraftPlayer");
        getHandleCraftPlayer = Reflect.getMethod(craftPlayerClass, "getHandle");
        final Class<?> containerAnvilClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"ContainerAnvil");
        final Class<?> playerInventoryClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"PlayerInventory");
        final Class<?> containerAccessClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"ContainerAccess");
        containerAnvilConstructor = Reflect.getConstructor(containerAnvilClass, int.class,playerInventoryClass, containerAccessClass);
        final Class<?> entityPlayerClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"EntityPlayer");
        nextContainerCounterMethod = Reflect.getMethod(entityPlayerClass, "nextContainerCounter");
        final Class<?> entityHumanClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"EntityHuman");
        inventoryField = Reflect.getField(entityHumanClass, "inventory");
        final Class<?> worldClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"World");
        final Class<?> blockPositionClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"BlockPosition");
        final Field zeroField = Reflect.getField(blockPositionClass, "ZERO");
        zeroField.setAccessible(true);
        zeroBlockPosition = Reflect.getStatic(zeroField);
        atContainerAccess = Reflect.getMethod(containerAccessClass, "at", worldClass, blockPositionClass);
        final Class<?> craftWorldClass = Reflect.getClass(Reflect.NMS.CRAFTBUKKIT+"CraftWorld");
        getHandleCraftWorld = Reflect.getMethod(craftWorldClass, "getHandle");
        final Class<?> containerClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"Container");
        checkReachableField = Reflect.getField(containerClass, "checkReachable");
        final Class<?> iChatBaseComponentClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"IChatBaseComponent");
        setTitleMethod = Reflect.getMethod(containerClass, "setTitle", iChatBaseComponentClass);
        final Class<?> chatMessageClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"ChatMessage");
        chatMessageConstructor = Reflect.getConstructor(chatMessageClass, String.class);
        getBukkitViewMethod = Reflect.getMethod(containerAnvilClass, "getBukkitView");
        
        final Class<?> craftInventoryClass = Reflect.getClass(Reflect.NMS.CRAFTBUKKIT+"inventory.CraftInventoryAnvil");
        containerField = Reflect.getField(craftInventoryClass, "container");
        windowIdField = Reflect.getField(containerClass, "windowId");
        playerConnectionField = Reflect.getField(entityPlayerClass, "playerConnection");
        final Class<?> playerConnectionClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"PlayerConnection");
        final Class<?> packetClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"Packet");
        sendPacketMethod = Reflect.getMethod(playerConnectionClass, "sendPacket", packetClass);
        final Class<?> packetPlayOutOpenWindowClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"PacketPlayOutOpenWindow");
        final Class<?> containersClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"Containers");
        packetPlayOutOpenWindowConstructor = Reflect.getConstructor(packetPlayOutOpenWindowClass, int.class, containersClass, iChatBaseComponentClass);
        final Field anvilField = Reflect.getField(containersClass, "ANVIL");
        anvilField.setAccessible(true);
        anvil = Reflect.getStatic(anvilField);
        getTitleMethod = Reflect.getMethod(containerClass, "getTitle");
        activeContainerField = Reflect.getField(entityHumanClass, "activeContainer");
        final Class<?> iCraftingClass = Reflect.getClass(Reflect.NMS.MINECRAFT+"ICrafting");
        addSlotListenerMethod = Reflect.getMethod(containerClass, "addSlotListener", iCraftingClass);

        getHandleCraftPlayer.setAccessible(true);
        containerAnvilConstructor.setAccessible(true);
        nextContainerCounterMethod.setAccessible(true);
        inventoryField.setAccessible(true);
        atContainerAccess.setAccessible(true);
        getHandleCraftWorld.setAccessible(true);
        checkReachableField.setAccessible(true);
        setTitleMethod.setAccessible(true);
        chatMessageConstructor.setAccessible(true);
        getBukkitViewMethod.setAccessible(true);
        containerField.setAccessible(true);
        windowIdField.setAccessible(true);
        playerConnectionField.setAccessible(true);
        sendPacketMethod.setAccessible(true);
        packetPlayOutOpenWindowConstructor.setAccessible(true);
        getTitleMethod.setAccessible(true);
        activeContainerField.setAccessible(true);
        addSlotListenerMethod.setAccessible(true);
    }

    public static AnvilInventory create(String title, Player owner){
        final Object entityPlayer = Reflect.invoke(getHandleCraftPlayer, owner);

        final Object containerAnvil = Reflect.newInstance(containerAnvilConstructor,
                Reflect.invoke(nextContainerCounterMethod, entityPlayer),
                Reflect.get(inventoryField, entityPlayer),
                Reflect.invokeStatic(atContainerAccess,
                        Reflect.invoke(getHandleCraftWorld, owner.getWorld()),
                        zeroBlockPosition
                        )
                );
        Reflect.set(checkReachableField, containerAnvil, false);
        Reflect.invoke(setTitleMethod, containerAnvil, Reflect.newInstance(chatMessageConstructor, title));

        return (AnvilInventory) ((InventoryView)Reflect.invoke(getBukkitViewMethod, containerAnvil)).getTopInventory();
    }

    public static void open(Player player, AnvilInventory inventory){
        final Object containerAnvil = Reflect.get(containerField, inventory);
        final Object entityPlayer = Reflect.invoke(getHandleCraftPlayer, player);

        final Object id = Reflect.get(windowIdField, containerAnvil);

        final Object playerConnection = Reflect.get(playerConnectionField, entityPlayer);
        final Object packetOpenWindow = Reflect.newInstance(packetPlayOutOpenWindowConstructor,
                id,
                anvil,
                Reflect.invoke(getTitleMethod, containerAnvil)
                );
        Reflect.invoke(sendPacketMethod, playerConnection, packetOpenWindow);

        Reflect.set(activeContainerField, entityPlayer, containerAnvil);
        Reflect.invoke(addSlotListenerMethod, containerAnvil, entityPlayer);
    }

}