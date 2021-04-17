package fr.flowsqy.abstractmenu.item;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CreatorListener {

    String handleName(Player player, String name);

    List<String> handleLore(Player player, List<String> lore);

    boolean handleUnbreakable(Player player, boolean unbreakable);

    Material handleMaterial(Player player, Material material);

    int handleAmount(Player player, int amount);

    Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants);

    Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags);

    Map<Attribute, AttributeModifier> handleAttributes(Player player, Map<Attribute, AttributeModifier> attributes);

    String handleHeadDataTextures(Player player, String textures);

    String handleHeadDataSignature(Player player, String signature);

}
