package fr.flo504.abstractmenu.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GlowItem {

    private final boolean hasFlag;
    private boolean glow;
    private final boolean hasEnchant;

    public GlowItem(boolean glow){
        this.glow = glow;
        hasFlag = false;
        hasEnchant = false;
    }

    public GlowItem(ItemStack item){
        this.glow = false;

        final ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            hasEnchant = false;
            hasFlag = false;
            return;
        }

        hasFlag = meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
        hasEnchant = meta.hasEnchant(Enchantment.LUCK);

        item.setItemMeta(meta);
    }

    public final void setGlow(boolean glow, ItemStack item){
        if(this.glow == glow)
            return;
        this.glow = glow;

        final ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return;

        if(glow){
            if(meta.getEnchants().isEmpty())
                meta.addEnchant(Enchantment.LUCK, 0, true);
            if(!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        else{
            if(!hasFlag && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            if(!hasEnchant && meta.hasEnchant(Enchantment.LUCK))
                meta.removeEnchant(Enchantment.LUCK);
        }
        item.setItemMeta(meta);
    }

    public final boolean isGlow() {
        return glow;
    }
}
