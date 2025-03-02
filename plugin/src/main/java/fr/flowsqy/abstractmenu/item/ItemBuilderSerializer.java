package fr.flowsqy.abstractmenu.item;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.abstractmenu.AbstractMenuPlugin;

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
                final ConfigurationSection subSection = attributeSection.createSection(modifier.getKey().getKey());
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

    @NotNull
    public static ItemBuilder deserialize(@NotNull ConfigurationSection section) {
        return deserialize(section, JavaPlugin.getPlugin(AbstractMenuPlugin.class).getLogger());
    }

    @NotNull
    public static ItemBuilder deserialize(@NotNull ConfigurationSection section, @NotNull Logger logger) {
        final ItemBuilder builder = new ItemBuilder();

        builder.material(deserializeMaterial(section.getString("type"), logger));

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
                final var enchantSection = enchantsSection.getConfigurationSection(enchantKey);
                if (enchantSection == null) {
                    continue;
                }
                final var rawEnchant = enchantSection.getString("enchant");
                if (rawEnchant == null) {
                    continue;
                }
                final var enchantNamespacedKey = NamespacedKey.fromString(rawEnchant);
                if (enchantNamespacedKey == null) {
                    logger.warning("'" + rawEnchant + "' is not a valid ressource location");
                    continue;
                }
                final var enchant = Registry.ENCHANTMENT.get(enchantNamespacedKey);
                if (enchant == null) {
                    logger.warning("'" + rawEnchant + "' is not a valid enchantment");
                    continue;
                }
                final String rawLevel = enchantsSection.getString(enchantKey);
                if (rawLevel == null) {
                    logger.warning("'" + rawEnchant + "' does not specify a level");
                    continue;
                }
                final int level;
                try {
                    level = Integer.parseInt(rawLevel);
                } catch (NumberFormatException ignored) {
                    logger.warning("'" + rawLevel + "' is not a valid number");
                    continue;
                }
                builder.enchants(enchant, level);
            }
        }

        final List<String> flags = section.getStringList("flags");
        for (String flagString : flags) {
            final ItemFlag flag = getEnumConstant(ItemFlag.class, flagString, null);
            builder.flags(flag);
        }

        final ConfigurationSection attributesSection = section.getConfigurationSection("attributes");
        if (attributesSection != null) {
            final var pluginInstance = JavaPlugin.getProvidingPlugin(AbstractMenuPlugin.class);
            for (String attributeKey : attributesSection.getKeys(false)) {
                final NamespacedKey storedAttributeKey;
                try {
                    storedAttributeKey = new NamespacedKey(pluginInstance, attributeKey);
                } catch (Exception e) {
                    logger.warning("'" + attributeKey + "' is not a valid ressource key");
                    continue;
                }
                final ConfigurationSection attributeSection = attributesSection.getConfigurationSection(attributeKey);
                if (attributeSection == null) {
                    continue;
                }
                final String rawAttribute = attributeSection.getString("attribute");
                if (rawAttribute == null) {
                    logger.warning("'" + attributeKey + "' should specify an attribute name");
                    continue;
                }
                final NamespacedKey attributeNamespacedKey = NamespacedKey.fromString(rawAttribute);
                if (attributeNamespacedKey == null) {
                    logger.warning("'" + rawAttribute + "' is not a valid ressource location");
                    continue;
                }
                final Attribute attribute = Registry.ATTRIBUTE.get(attributeNamespacedKey);
                if (attribute == null) {
                    logger.warning("'" + attributeNamespacedKey.toString() + "' is not a valid attribute name");
                    continue;
                }
                final double value = attributeSection.getDouble("amount", 0d);
                if (value == 0) {
                    continue;
                }
                final EquipmentSlotGroup slotGroup = getEquipmentSlotGroup(attributeSection.getString("slot-group"));
                final AttributeModifier.Operation operation = getEnumConstant(AttributeModifier.Operation.class,
                        attributeSection.getString("operation"), AttributeModifier.Operation.ADD_NUMBER);
                builder.attributes(attribute, new AttributeModifier(storedAttributeKey, value, operation, slotGroup));
            }
        }

        final ConfigurationSection headDataSection = section.getConfigurationSection("head-data");
        if (headDataSection != null) {
            builder.headData(deserializeHeadData(headDataSection, logger));
        }

        return builder;
    }

    @Nullable
    private static Material deserializeMaterial(@Nullable String rawType, @NotNull Logger logger) {
        if (rawType == null) {
            return null;
        }
        final var typeKey = NamespacedKey.fromString(rawType);
        if (typeKey == null) {
            logger.warning("'" + rawType + "' is not a valid ressource location");
            return null;
        }
        final var type = Registry.MATERIAL.get(typeKey);
        if (type == null) {
            logger.warning("'" + rawType + "' is not a valid material");
            return null;
        }
        return type;
    }

    @Nullable
    private static HeadData deserializeHeadData(@NotNull ConfigurationSection headDataSection, @NotNull Logger logger) {
        final var rawUrl = headDataSection.getString("url");
        if (rawUrl == null) {
            return null;
        }
        final URL textureUrl;
        try {
            textureUrl = URI.create(rawUrl).toURL();
        } catch (Exception e) {
            logger.warning("'" + rawUrl + "' is not an url. " + e.getMessage());
            return null;
        }
        UUID id = null;
        final var rawId = headDataSection.getString("id");
        if (rawId != null) {
            try {
                id = UUID.fromString(rawId);
            } catch (IllegalArgumentException e) {
                logger.warning("'" + rawId + "' is not a valid UUID. " + e.getMessage());
            }
        }
        final var rawName = headDataSection.getString("name");
        final var name = rawName == null || rawName.isEmpty() ? null : rawName;
        return new HeadData(id, name, textureUrl);
    }

    @NotNull
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

    @NotNull
    private static EquipmentSlotGroup getEquipmentSlotGroup(@Nullable String value) {
        if (value == null) {
            return EquipmentSlotGroup.ANY;
        }
        return switch (value) {
            case "any" -> EquipmentSlotGroup.ANY;
            case "armor" -> EquipmentSlotGroup.ARMOR;
            case "chest" -> EquipmentSlotGroup.CHEST;
            case "feet" -> EquipmentSlotGroup.FEET;
            case "hand" -> EquipmentSlotGroup.HAND;
            case "head" -> EquipmentSlotGroup.HEAD;
            case "legs" -> EquipmentSlotGroup.LEGS;
            case "mainhand" -> EquipmentSlotGroup.MAINHAND;
            case "offhand" -> EquipmentSlotGroup.OFFHAND;
            default -> EquipmentSlotGroup.ANY;
        };
    }

    private static <T extends Enum<T>> T getEnumConstant(Class<T> enumClass, String value, @Nullable T defaultValue) {
        if (enumClass == null || value == null)
            return defaultValue;

        value = value.trim().toUpperCase();

        for (final T constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(value))
                return constant;
        }

        return defaultValue;
    }

}
