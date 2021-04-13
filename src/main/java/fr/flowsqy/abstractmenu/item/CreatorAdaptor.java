package fr.flowsqy.abstractmenu.item;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreatorAdaptor implements CreatorListener {

    @Override
    public String handleName(String name) {
        return name;
    }

    @Override
    public List<String> handleLore(List<String> lore) {
        return lore;
    }

    @Override
    public boolean handleUnbreakable(boolean unbreakable) {
        return unbreakable;
    }

    @Override
    public Material handleMaterial(Material material) {
        return material;
    }

    @Override
    public int handleAmount(int amount) {
        return amount;
    }

    @Override
    public Map<Enchantment, Integer> handleEnchants(Map<Enchantment, Integer> enchants) {
        return enchants;
    }

    @Override
    public Set<ItemFlag> handleFlags(Set<ItemFlag> flags) {
        return flags;
    }

    @Override
    public Map<Attribute, AttributeModifier> handleAttributes(Map<Attribute, AttributeModifier> attributes) {
        return attributes;
    }
}
