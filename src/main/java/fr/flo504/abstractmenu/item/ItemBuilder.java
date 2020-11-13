package fr.flo504.abstractmenu.item;

import com.google.common.collect.Multimap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

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

    private final static String RESET_PATTERN = ChatColor.RESET.toString()+ChatColor.WHITE;

    public static ItemBuilder deserialize(ConfigurationSection section){
        Objects.requireNonNull(section);
        //TODO
        return null;
    }

    public static void serialize(ConfigurationSection section, ItemBuilder itemBuilder){
        Objects.requireNonNull(section);
        Objects.requireNonNull(itemBuilder);

        section.set("name", itemBuilder.name());
        section.set("lore", itemBuilder.lore()
                .stream()
                .map(line -> {
                    if(line.startsWith(RESET_PATTERN))
                        line = line.replaceFirst(RESET_PATTERN, "");
                    line = line.replace(ChatColor.COLOR_CHAR, '&');
                    return line;
                })
                .collect(Collectors.toList())
                );
        section.set("unbreakable", itemBuilder.unbreakable());
        itemBuilder.enchants().forEach((enchant, level) -> section.set(enchant.getKey().getKey(), level));
        section.set("flags", new ArrayList<>(itemBuilder.flags()));
        itemBuilder.attributes().forEach((attribute, modifier) -> {
            final ConfigurationSection subSection = section.createSection(attribute.name().toLowerCase());
            subSection.set("uuid", modifier.getUniqueId().toString());
            subSection.set("name", modifier.getName());
            subSection.set("amount", modifier.getAmount());
            subSection.set("operation", modifier.getOperation().name());
            if(modifier.getSlot() != null)
                subSection.set("slot", modifier.getSlot().name());
        });

    }

    public static void serialize(ConfigurationSection section, ItemStack itemStack){
        serialize(section, copy(itemStack));
    }

    public static ItemBuilder copy(ItemStack item){
        Objects.requireNonNull(item);
        final ItemBuilder builder = new ItemBuilder();

        builder
                .material(item.getType())
                .amount(item.getAmount());

        final ItemMeta meta = item.getItemMeta();
        if(meta == null) // Normally impossible
            return builder;

        builder
                .name(meta.getDisplayName())
                .lore(meta.getLore())
                .unbreakable(meta.isUnbreakable());

        meta.getEnchants().forEach(builder::enchants);
        meta.getItemFlags().forEach(builder::flags);
        final Multimap<Attribute, AttributeModifier> attributes = meta.getAttributeModifiers();
        if(attributes != null)
            attributes.forEach(builder::attributes);

        return builder;
    }

}
