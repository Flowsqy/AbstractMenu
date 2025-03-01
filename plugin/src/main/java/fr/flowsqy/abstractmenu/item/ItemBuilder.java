package fr.flowsqy.abstractmenu.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Multimap;

public class ItemBuilder {

    private final Map<Enchantment, Integer> enchants;
    private final Set<ItemFlag> flags;
    private final Map<Attribute, AttributeModifier> attributes;
    private String name;
    private Material material;
    private int amount;
    private boolean unbreakable;
    private List<String> lore;
    private HeadData headData;
    private CreatorListener creatorListener;

    public ItemBuilder() {
        lore = new ArrayList<>();
        enchants = new HashMap<>();
        flags = new HashSet<>();
        attributes = new HashMap<>();
    }

    private ItemBuilder(ItemBuilder builder) {
        this.name = builder.name;
        this.material = builder.material;
        this.amount = builder.amount;
        this.unbreakable = builder.unbreakable;
        this.lore = builder.lore == null ? null : new ArrayList<>(builder.lore());
        this.enchants = new HashMap<>(builder.enchants());
        this.flags = new HashSet<>(builder.flags());
        this.attributes = new HashMap<>(builder.attributes);
        this.headData = builder.headData;
        this.creatorListener = creatorListener();
    }

    public static ItemBuilder deserialize(ConfigurationSection section) {
        return ItemBuilderSerializer.deserialize(section);
    }

    public static void serialize(ConfigurationSection section, ItemBuilder itemBuilder) {
        ItemBuilderSerializer.serialize(section, itemBuilder);
    }

    public static void serialize(ConfigurationSection section, ItemStack itemStack) {
        serialize(section, copy(itemStack));
    }

    public static ItemBuilder copy(ItemStack item) {
        Objects.requireNonNull(item);
        final ItemBuilder builder = new ItemBuilder();

        builder
                .material(item.getType())
                .amount(item.getAmount());

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) // Normally impossible
            return builder;

        builder
                .name(meta.getDisplayName())
                .lore(meta.getLore())
                .unbreakable(meta.isUnbreakable());

        meta.getEnchants().forEach(builder::enchants);
        meta.getItemFlags().forEach(builder::flags);
        final Multimap<Attribute, AttributeModifier> attributes = meta.getAttributeModifiers();
        if (attributes != null)
            attributes.forEach(builder::attributes);

        if (meta instanceof SkullMeta skullMeta) {
            final var ownerProfile = skullMeta.getOwnerProfile();
            if (ownerProfile != null) {
                final var skinURL = ownerProfile.getTextures().getSkin();
                if (skinURL != null) {
                    builder.headData = new HeadData(ownerProfile.getUniqueId(), ownerProfile.getName(), skinURL);
                }
            }
        }

        return builder;
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

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public Map<Enchantment, Integer> enchants() {
        return enchants;
    }

    public ItemBuilder enchants(Enchantment enchantment, int level) {
        if (enchantment != null)
            enchants.put(enchantment, level);
        return this;
    }

    public Set<ItemFlag> flags() {
        return flags;
    }

    public ItemBuilder flags(ItemFlag flag) {
        if (flag != null)
            flags.add(flag);
        return this;
    }

    public Map<Attribute, AttributeModifier> attributes() {
        return attributes;
    }

    public ItemBuilder attributes(Attribute attribute, AttributeModifier modifier) {
        if (attribute != null && modifier != null)
            attributes.put(attribute, modifier);
        return this;
    }

    public HeadData headData() {
        return headData;
    }

    public ItemBuilder headData(HeadData headData) {
        this.headData = headData;
        return this;
    }

    public CreatorListener creatorListener() {
        return creatorListener;
    }

    public ItemBuilder creatorListener(CreatorListener creatorListener) {
        this.creatorListener = creatorListener;
        return this;
    }

    public ItemStack create(Player player) {
        return create(player, creatorListener);
    }

    public synchronized ItemStack create(Player player, CreatorListener creatorListener) {
        if (creatorListener == null)
            creatorListener = new CreatorAdaptor();

        // A little tricky but the variable can not changed because the method is
        // synchronized too
        synchronized (creatorListener) {
            creatorListener.open(player);

            final Material handledMaterial = creatorListener.handleMaterial(player, material);

            if (handledMaterial == null) {
                creatorListener.close(player);
                return null;
            }

            final ItemStack item = new ItemStack(handledMaterial, creatorListener.handleAmount(player, amount));
            final ItemMeta meta = item.getItemMeta();
            if (meta == null) { // Normally impossible
                creatorListener.close(player);
                return item;
            }

            meta.setDisplayName(creatorListener.handleName(player, name));
            meta.setUnbreakable(creatorListener.handleUnbreakable(player, unbreakable));
            meta.setLore(creatorListener.handleLore(player, lore));

            final Map<Enchantment, Integer> handledEnchants = creatorListener.handleEnchants(player, enchants);
            final Set<ItemFlag> handledFlags = creatorListener.handleFlags(player, flags);
            final Map<Attribute, AttributeModifier> handledAttributes = creatorListener.handleAttributes(player,
                    attributes);

            if (handledEnchants != null)
                handledEnchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
            if (handledFlags != null)
                handledFlags.forEach(meta::addItemFlags);
            if (handledAttributes != null)
                handledAttributes.forEach(meta::addAttributeModifier);

            if (handledMaterial == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
                final var handledHeadData = creatorListener.handleHeadData(player, headData);
                if (handledHeadData != null) {
                    final var id = handledHeadData.id() == null ? new UUID(0xFFFFFF, 0xFFFFFF) : handledHeadData.id();
                    final var name = handledHeadData.name() == null ? "AbstractMenu" : handledHeadData.name();
                    final var profile = Bukkit.createPlayerProfile(id, name);
                    profile.getTextures().setSkin(handledHeadData.textureURL());
                    skullMeta.setOwnerProfile(profile);
                }
            }

            item.setItemMeta(meta);

            creatorListener.close(player);

            return item;
        }
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this);
    }

}
