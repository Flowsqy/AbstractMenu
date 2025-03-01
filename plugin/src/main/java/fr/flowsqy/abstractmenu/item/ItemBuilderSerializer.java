package fr.flowsqy.abstractmenu.item;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBuilderSerializer {

    private final static String RESET_PATTERN = ChatColor.RESET.toString() + ChatColor.WHITE;

    public static void serialize(@NotNull ConfigurationSection section, @NotNull ItemBuilder itemBuilder) {
        section.set("type", itemBuilder.material().getKey().toString());

        String name = itemBuilder.name();

        if (name != null) {
            if (name.startsWith(RESET_PATTERN))
                name = name.replaceFirst(RESET_PATTERN, "");

            name = name.replace(ChatColor.COLOR_CHAR, '&');

            section.set("name", name);
        }

        section.set("lore", itemBuilder.lore()
                .stream()
                .filter(Objects::nonNull)
                .map(line -> {
                    if (line.startsWith(RESET_PATTERN))
                        line = line.replaceFirst(RESET_PATTERN, "");
                    line = line.replace(ChatColor.COLOR_CHAR, '&');
                    return line;
                })
                .collect(Collectors.toList()));
        section.set("amount", itemBuilder.amount());
        section.set("unbreakable", itemBuilder.unbreakable());
        if (!itemBuilder.enchants().isEmpty()) {
            final ConfigurationSection enchantSection = section.createSection("enchants");
            int index = 0;
            for (var enchant : itemBuilder.enchants().entrySet()) {
                index++;
                final var nEnchantSection = enchantSection.createSection("enchant-" + index);
                nEnchantSection.set("enchant", enchant.getKey().getKey().toString());
                nEnchantSection.set("level", enchant.getValue());
            }
        }
        if (!itemBuilder.flags().isEmpty())
            section.set("flags", itemBuilder.flags().stream()
                    .filter(Objects::nonNull)
                    .map(ItemFlag::name)
                    .collect(Collectors.toList()));
        if (!itemBuilder.attributes().isEmpty()) {
            final ConfigurationSection attributeSection = section.createSection("attributes");
            itemBuilder.attributes().forEach((attribute, modifier) -> {
                final ConfigurationSection subSection = attributeSection.createSection(attribute.name().toLowerCase());
                subSection.set("attribute", modifier.getKey().toString());
                subSection.set("amount", modifier.getAmount());
                subSection.set("operation", modifier.getOperation().name());
                final var slotGroup = modifier.getSlotGroup();
                if (slotGroup != null) {
                    subSection.set("slot-group", getSlotGroupName(slotGroup));
                }
            });
        }

        final var headData = itemBuilder.headData();
        if (headData != null) {
            final var headDataSection = section.createSection("head-data");
            headDataSection.set("url", headData.textureURL().toExternalForm());
            if (headData.id() != null) {
                headDataSection.set("id", headData.id());
            }
            if (headData.name() != null) {
                headDataSection.set("name", headData.name());
            }
        }
    }

    public static ItemBuilder deserialize(ConfigurationSection section) {
        Objects.requireNonNull(section);

        final ItemBuilder builder = new ItemBuilder();

        builder.material(getEnumConstant(Material.class, section.getString("type")));

        String name = section.getString("name");
        if (name != null) {
            name = ChatColor.translateAlternateColorCodes('&', name);
            if (!name.startsWith(RESET_PATTERN))
                name = RESET_PATTERN + name;
            builder.name(name);
        }

        List<String> lore = section.getStringList("lore");
        lore = lore.stream()
                .filter(Objects::nonNull)
                .map(line -> {
                    line = ChatColor.translateAlternateColorCodes('&', line);
                    if (!line.startsWith(RESET_PATTERN))
                        line = RESET_PATTERN + line;
                    return line;

                })
                .collect(Collectors.toList());

        builder.lore(lore);

        builder.unbreakable(section.getBoolean("unbreakable"));
        builder.amount(section.getInt("amount", 1));

        final ConfigurationSection enchantsSection = section.getConfigurationSection("enchants");
        if (enchantsSection != null) {
            for (String enchantKey : enchantsSection.getKeys(false)) {
                final String levelString = enchantsSection.getString(enchantKey);
                if (levelString == null)
                    continue;
                final int level;
                try {
                    level = Integer.parseInt(levelString);
                } catch (NumberFormatException ignored) {
                    continue;
                }

                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantKey));

                builder.enchants(enchantment, level);
            }
        }

        final List<String> flags = section.getStringList("flags");
        for (String flagString : flags) {
            final ItemFlag flag = getEnumConstant(ItemFlag.class, flagString);
            builder.flags(flag);
        }

        final ConfigurationSection attributesSection = section.getConfigurationSection("attributes");
        if (attributesSection != null) {
            for (final String attributeKey : attributesSection.getKeys(false)) {
                final ConfigurationSection attributeSection = attributesSection.getConfigurationSection(attributeKey);
                if (attributeSection == null)
                    continue;

                final Attribute attribute = getEnumConstant(Attribute.class, attributeKey);
                if (attribute == null)
                    continue;

                final String rawUuid = attributeSection.getString("uuid", "");
                UUID uuid;
                try {
                    uuid = UUID.fromString(rawUuid);
                } catch (Exception ignored) {
                    uuid = UUID.randomUUID();
                }
                final String modifierName = attributeSection.getString("name");
                if (modifierName == null)
                    continue;

                final String modifierAmountString = attributeSection.getString("amount");
                if (modifierAmountString == null)
                    continue;
                final double modifierAmount;
                try {
                    modifierAmount = Double.parseDouble(modifierAmountString);
                } catch (NumberFormatException ignored) {
                    continue;
                }

                final String operationString = attributeSection.getString("operation");
                final AttributeModifier.Operation operation = getEnumConstant(AttributeModifier.Operation.class,
                        operationString);
                if (operation == null)
                    continue;

                final EquipmentSlot slot = getEnumConstant(EquipmentSlot.class, attributeSection.getString("slot"));

                final AttributeModifier modifier = new AttributeModifier(uuid, modifierName, modifierAmount, operation,
                        slot);

                builder.attributes(attribute, modifier);
            }
        }

        final ConfigurationSection headDataSection = section.getConfigurationSection("head-data");
        if (headDataSection != null) {
            // TODO Deserialize correctly textures
            final String texture = headDataSection.getString("texture");
            final String signature = headDataSection.getString("signature");
            // builder.headData(texture, signature);
        }

        return builder;
    }

    private static String getSlotGroupName(@NotNull EquipmentSlotGroup slotGroup) {
        if (slotGroup == EquipmentSlotGroup.ANY) {
            return "any";
        }
        if (slotGroup == EquipmentSlotGroup.ARMOR) {
            return "armor";
        }
        if (slotGroup == EquipmentSlotGroup.CHEST) {
            return "chest";
        }
        if (slotGroup == EquipmentSlotGroup.FEET) {
            return "feet";
        }
        if (slotGroup == EquipmentSlotGroup.HAND) {
            return "hand";
        }
        if (slotGroup == EquipmentSlotGroup.HEAD) {
            return "head";
        }
        if (slotGroup == EquipmentSlotGroup.LEGS) {
            return "legs";
        }
        if (slotGroup == EquipmentSlotGroup.MAINHAND) {
            return "mainhand";
        }
        if (slotGroup == EquipmentSlotGroup.OFFHAND) {
            return "offhand";
        }
        return "any";
    }

    @Nullable
    private static <T extends Enum<T>> T agetEnumConstant(Class<T> enumClass, String value) {
        if (enumClass == null || value == null)
            return null;

        value = value.trim().toUpperCase();

        for (final T constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(value))
                return constant;
        }

        return null;
    }

}
