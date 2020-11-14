package fr.flo504.abstractmenu.item;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CreatorListener {

    String handleName(String name);

    List<String> handleLore(List<String> lore);

    boolean handleUnbreakable(boolean unbreakable);

    Material handleMaterial(Material material);

    int handleAmount(int amount);

    Map<Enchantment, Integer> handleEnchants(Map<Enchantment, Integer> enchants);

    Set<ItemFlag> handleFlags(Set<ItemFlag> flags);

    Map<Attribute, AttributeModifier> handleAttributes(Map<Attribute, AttributeModifier> attributes);

}
