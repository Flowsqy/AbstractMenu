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

public class CreatorCopy implements CreatorListener {

    private ItemBuilder original;

    public CreatorCopy() {
    }

    public CreatorCopy(ItemBuilder original) {
        this.original = original;
    }

    public ItemBuilder original() {
        return original;
    }

    public CreatorCopy original(ItemBuilder original) {
        this.original = original;
        return this;
    }

    @Override
    public String handleName(Player player, String name) {
        return original == null ? name : original.name();
    }

    @Override
    public List<String> handleLore(Player player, List<String> lore) {
        return original == null ? lore : original.lore();
    }

    @Override
    public boolean handleUnbreakable(Player player, boolean unbreakable) {
        return original == null ? unbreakable : original.unbreakable();
    }

    @Override
    public Material handleMaterial(Player player, Material material) {
        return original == null ? material : original.material();
    }

    @Override
    public int handleAmount(Player player, int amount) {
        return original == null ? amount : original.amount();
    }

    @Override
    public Map<Enchantment, Integer> handleEnchants(Player player, Map<Enchantment, Integer> enchants) {
        return original == null ? enchants : original.enchants();
    }

    @Override
    public Set<ItemFlag> handleFlags(Player player, Set<ItemFlag> flags) {
        return original == null ? flags : original.flags();
    }

    @Override
    public Map<Attribute, AttributeModifier> handleAttributes(Player player, Map<Attribute, AttributeModifier> attributes) {
        return original == null ? attributes : original.attributes();
    }

    @Override
    public String handleHeadDataTextures(Player player, String textures) {
        return original == null ? textures : original.headDataTexture();
    }

    @Override
    public String handleHeadDataSignature(Player player, String signature) {
        return original == null ? signature : original.headDataSignature();
    }
}
