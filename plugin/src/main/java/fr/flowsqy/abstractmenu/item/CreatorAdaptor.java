package fr.flowsqy.abstractmenu.item;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class CreatorAdaptor implements CreatorListener {

    @Override
    public String handleName(Player player, String name) {
        return name;
    }

    @Override
    public List<String> handleLore(Player player, List<String> lore) {
        return lore;
    }

    @Override
    public boolean handleUnbreakable(Player player, boolean unbreakable) {
        return unbreakable;
    }

    @Override
    public Material handleMaterial(Player player, Material material) {
        return material;
    }

    @Override
    public int handleAmount(Player player, int amount) {
        return amount;
    }

    @Override
    public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
        return enchants;
    }

    @Override
    public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
        return flags;
    }

    @Override
    public Map<Attribute, AttributeModifier> handleAttributes(Player player,
            Map<Attribute, AttributeModifier> attributes) {
        return attributes;
    }

    @Override
    public HeadData handleHeadData(Player player, HeadData headData) {
        return headData;
    }

}
