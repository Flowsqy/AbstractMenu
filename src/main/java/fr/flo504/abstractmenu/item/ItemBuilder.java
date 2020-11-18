package fr.flo504.abstractmenu.item;

import com.google.common.collect.Multimap;
import fr.flo504.reflect.Reflect;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
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

    private CreatorListener creatorListener;

    public ItemBuilder() {
        lore = new ArrayList<>();
        enchants = new HashMap<>();
        flags = new HashSet<>();
        attributes = new HashMap<>();
    }

    private ItemBuilder(ItemBuilder builder){
        this.name = builder.name;
        this.material = builder.material;
        this.amount = builder.amount;
        this.unbreakable = builder.unbreakable;
        this.lore = builder.lore == null ? null : new ArrayList<>(builder.lore());
        this.enchants = new HashMap<>(builder.enchants());
        this.flags = new HashSet<>(builder.flags());
        this.attributes = new HashMap<>(builder.attributes);
        this.creatorListener = creatorListener();
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
        if(enchantment != null)
            enchants.put(enchantment, level);
        return this;
    }

    public Set<ItemFlag> flags() {
        return flags;
    }

    public ItemBuilder flags(ItemFlag flag) {
        if(flag != null)
            flags.add(flag);
        return this;
    }

    public Map<Attribute, AttributeModifier> attributes() {
        return attributes;
    }

    public ItemBuilder attributes(Attribute attribute, AttributeModifier modifier) {
        if(attribute != null && modifier != null)
            attributes.put(attribute, modifier);
        return this;
    }

    public CreatorListener creatorListener() {
        return creatorListener;
    }

    public ItemBuilder creatorListener(CreatorListener creatorListener) {
        this.creatorListener = creatorListener;
        return this;
    }

    public ItemStack create(){
        return create(creatorListener);
    }

    public ItemStack create(CreatorListener creatorListener){
        if(creatorListener == null)
            creatorListener = new CreatorAdaptor();

        final Material handledMaterial = creatorListener.handleMaterial(material);

        Objects.requireNonNull(material, "Material can not be null");

        final ItemStack item = new ItemStack(handledMaterial, creatorListener.handleAmount(amount));
        final ItemMeta meta = item.getItemMeta();
        if(meta == null) // Normally impossible
            return item;

        meta.setDisplayName(creatorListener.handleName(name));
        meta.setUnbreakable(creatorListener.handleUnbreakable(unbreakable));
        meta.setLore(creatorListener.handleLore(lore));

        Map<Enchantment, Integer> handledEnchants = creatorListener.handleEnchants(enchants);
        Set<ItemFlag> handledFlags = creatorListener.handleFlags(flags);
        Map<Attribute, AttributeModifier> handledAttributes = creatorListener.handleAttributes(attributes);

        if(handledEnchants != null)
            handledEnchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
        if(handledFlags != null)
            handledFlags.forEach(meta::addItemFlags);
        if(handledAttributes != null)
            handledAttributes.forEach(meta::addAttributeModifier);

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this);
    }

    private final static String RESET_PATTERN = ChatColor.RESET.toString()+ChatColor.WHITE;

    public static ItemBuilder deserialize(ConfigurationSection section){
        Objects.requireNonNull(section);

        final ItemBuilder builder = new ItemBuilder();

        builder.material(Reflect.getEnumConstant(Material.class, section.getString("type")));

        String name = section.getString("name");
        if(name != null){
            name = ChatColor.translateAlternateColorCodes('&', name);
            if(!name.startsWith(RESET_PATTERN))
                name = RESET_PATTERN + name;
            builder.name(name);
        }

        List<String> lore = section.getStringList("lore");
        lore = lore.stream()
                .filter(Objects::nonNull)
                .map(line -> {
                        line = ChatColor.translateAlternateColorCodes('&', line);
                        if(!line.startsWith(RESET_PATTERN))
                            line = RESET_PATTERN + line;
                        return line;

                })
                .collect(Collectors.toList());

        builder.lore(lore);

        builder.unbreakable(section.getBoolean("unbreakable"));
        builder.amount(section.getInt("amount", 1));

        final ConfigurationSection enchantsSection = section.getConfigurationSection("enchants");
        if(enchantsSection != null){
            for(String enchantKey : enchantsSection.getKeys(false)){
                final String levelString = enchantsSection.getString(enchantKey);
                if(levelString == null)
                    continue;
                final int level;
                try{
                    level = Integer.parseInt(levelString);
                }catch (NumberFormatException ignored){
                    continue;
                }

                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantKey));

                builder.enchants(enchantment, level);
            }
        }

        final List<String> flags = section.getStringList("flags");
        for(String flagString : flags){
            final ItemFlag flag = Reflect.getEnumConstant(ItemFlag.class, flagString);
            builder.flags(flag);
        }

        final ConfigurationSection attributesSection = section.getConfigurationSection("attributes");
        if(attributesSection != null){
            for(final String attributeKey : attributesSection.getKeys(false)){
                final ConfigurationSection attributeSection = attributesSection.getConfigurationSection(attributeKey);
                if(attributeSection == null)
                    continue;

                final Attribute attribute = Reflect.getEnumConstant(Attribute.class, attributeKey);
                if(attribute == null)
                    continue;

                final String rawUuid = attributeSection.getString("uuid", "");
                UUID uuid;
                if(rawUuid == null || rawUuid.split("-").length != 5)
                    uuid = UUID.randomUUID();
                else {
                    try{
                        uuid = UUID.fromString(rawUuid);
                    }catch (NumberFormatException ignored){
                        uuid = UUID.randomUUID();
                    }
                }
                final String modifierName = attributeSection.getString("name");
                if(modifierName == null)
                    continue;

                final String modifierAmountString = attributeSection.getString("amount");
                if(modifierAmountString == null)
                    continue;
                final double modifierAmount;
                try{
                    modifierAmount = Double.parseDouble(modifierAmountString);
                }catch (NumberFormatException ignored){
                    continue;
                }

                final String operationString = attributeSection.getString("operation");
                final AttributeModifier.Operation operation = Reflect.getEnumConstant(AttributeModifier.Operation.class, operationString);
                if(operation == null)
                    continue;

                final EquipmentSlot slot = Reflect.getEnumConstant(EquipmentSlot.class, attributeSection.getString("slot"));

                final AttributeModifier modifier = new AttributeModifier(uuid, modifierName, modifierAmount, operation, slot);

                builder.attributes(attribute, modifier);
            }
        }

        return builder;
    }

    public static void serialize(ConfigurationSection section, ItemBuilder itemBuilder){
        Objects.requireNonNull(section);
        Objects.requireNonNull(itemBuilder);

        section.set("type", itemBuilder.material().name());

        String name = itemBuilder.name();

        if(name.startsWith(RESET_PATTERN))
            name = name.replaceFirst(RESET_PATTERN, "");

        name = name.replace(ChatColor.COLOR_CHAR, '&');

        section.set("name", name);
        section.set("lore", itemBuilder.lore()
                .stream()
                .filter(Objects::nonNull)
                .map(line -> {
                    if(line.startsWith(RESET_PATTERN))
                        line = line.replaceFirst(RESET_PATTERN, "");
                    line = line.replace(ChatColor.COLOR_CHAR, '&');
                    return line;
                })
                .collect(Collectors.toList())
                );
        section.set("amount", itemBuilder.amount());
        section.set("unbreakable", itemBuilder.unbreakable());
        if(!itemBuilder.enchants().isEmpty()){
            final ConfigurationSection enchantSection = section.createSection("enchants");
            itemBuilder.enchants().forEach((enchant, level) -> enchantSection.set(enchant.getKey().getKey(), level));
        }
        if(!itemBuilder.flags().isEmpty())
            section.set("flags", itemBuilder.flags().stream()
                    .filter(Objects::nonNull)
                    .map(ItemFlag::name)
                    .collect(Collectors.toList()
                    ));
        if(!itemBuilder.attributes().isEmpty()){
            final ConfigurationSection attributeSection = section.createSection("attributes");
            itemBuilder.attributes().forEach((attribute, modifier) -> {
                final ConfigurationSection subSection = attributeSection.createSection(attribute.name().toLowerCase());
                subSection.set("uuid", modifier.getUniqueId().toString());
                subSection.set("name", modifier.getName());
                subSection.set("amount", modifier.getAmount());
                subSection.set("operation", modifier.getOperation().name());
                subSection.set("slot", modifier.getSlot() != null ? modifier.getSlot().name() : null);
            });
        }

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
