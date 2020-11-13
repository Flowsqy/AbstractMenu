package fr.flo504.abstractmenu.item;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemBuilder {

    private String name;
    private Material material;
    private int amount;
    private boolean unbreakable;
    private List<String> lore;
    private final Map<Enchantment, Integer> enchants;
    private final Set<ItemFlag> flags;
    private final Map<Attribute, AttributeModifier> attributes;

    public ItemBuilder() {
        lore = new ArrayList<>();
        enchants = new HashMap<>();
        flags = new HashSet<>();
        attributes = new HashMap<>();
    }

    public String name() {
        return name;
    }

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Material material() {
        return material;
    }

    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public int amount() {
        return amount;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public boolean unbreakable() {
        return unbreakable;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public List<String> lore() {
        return lore;
    }

    public ItemBuilder lore(Collection<String> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public ItemBuilder lore(String... lore){
        return lore(Arrays.asList(lore));
    }

    public Map<Enchantment, Integer> enchants() {
        return enchants;
    }

    public ItemBuilder enchants(Enchantment enchantment, int level) {
        enchants.put(enchantment, level);
        return this;
    }

    public Set<ItemFlag> flags() {
        return flags;
    }

    public ItemBuilder flags(ItemFlag flag) {
        flags.add(flag);
        return this;
    }

    public Map<Attribute, AttributeModifier> attributes() {
        return attributes;
    }

    public ItemBuilder attributes(Attribute attribute, AttributeModifier modifier) {
        attributes.put(attribute, modifier);
        return this;
    }

    public ItemStack create(){
        if(material == null)
            return null;
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();
        if(meta == null) // Normally impossible
            return item;

        meta.setDisplayName(name);
        meta.setUnbreakable(unbreakable);
        meta.setLore(lore);
        enchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
        flags.forEach(meta::addItemFlags);
        attributes.forEach(meta::addAttributeModifier);

        item.setItemMeta(meta);

        return item;
    }

}
