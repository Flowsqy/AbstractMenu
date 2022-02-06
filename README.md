# AbstractMenu

A Bukkit plugin which offers an API to create item and inventory to do custom GUI with ease. It also unifies the
configuration format to allow simple yaml configuration for servers owners.

## Configuration

- Item

```yaml
<key>:
  type: <material>
  name: <name>
  lore:
    - <line-1>
    - <line-2>
    - ...
  unbreakable: <unbreakable>
  amount: <amount>
  enchants:
    <enchant-1>: <enchant-level>
    <enchant-2>: <enchant-level>
    ...
  flags:
    - <flag-1>
    - <flag-2>
    - ...
  attributes:
    <attribute-1>:
      uuid: <attribute-uuid>
      name: <attribute-name>
      amount: <attribute-amount>
      operation: <attribute-operation>
      slot: <attribute-slot>
    <attribute-2>:
      ...
  head-data:
    texture: <head-texture>
    signature: <head-signature>

# Where:
#
# <key> is the root key
# <material> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html] :
#   The material of the item. Set it to null will create an empty item
# <name> [String] : is the name. It supports colors
# <line-n> [String] : is a line from the lore of the item. It supports colors
# <unbreakable> [boolean] : Whether the item should be unbreakable
# <amount> [integer] : The number of item
#
# Enchantments:
# <enchant-n> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html] :
#   An enchantment to apply.
# <enchant-level> [integer] : The enchantment level
#
# Flags: 
# <flag-n> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/ItemFlag.html] : 
#   An item flag to apply (e.g. to hide enchants)
#
# Attribute:
# <attribute-n> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/Attribute.html] :
#   The attribute to modify. It will be skipped if the value is null, if the attribute is invalid or if the attribute
#   isn't compatible with the specified entity type. You can have as many attribute as you want
# <attribute-uuid> [UUID] : An uuid that identify this attribute. Random by default
# <attribute-name> [String] : The name of this attribute. (REQUIRED only for this attribute, not for the global entity)
# <attribute-amount> [double] : The modifier amount. (REQUIRED only for this attribute, not for the global entity)
# <attribute-operation> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/AttributeModifier.Operation.html] :
#   The operation to apply to the attribute. (REQUIRED only for this attribute, not for the global entity)
# <attribute-slot> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/EquipmentSlot.html] :
#   The slot in which the modifier is active. If not present, the modifier is effective permanently (default)
#
# Head
# This section is used only if the item is a player skull
# <head-texture> [String] : The texture string of the skin to apply to the head
# <head-signature> [String] : The signature string of the skin to apply to the head
```

- Inventory

```yaml
<key>:
  name: <name>
  line: <line>
  items:
    <item-n>:
      slots:
        - <slot-1>
        - <slot-2>
        - ...
      item: <item>

# Where :
#
# <key> is the root key
# <name> [String] : The inventory name. It supports colors
# <line> [integer] : The number of line. It must be between 1 and 6
#
# Items:
# <item-n> is the root tag of an item that will be in one or more slot.
#   It can be whatever you want, it just needs to be unique.
#   You can have as many item as you want.
# <slot-n> [integer] : A slot where the item must be. Invalid slot will be ignored
# <item> [AbstractMenu Item (see above)] : The item to set in the specified slot
```

## Developers

How to include the API with Maven:

```xml

<project>
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>com.github.Flowsqy</groupId>
            <artifactId>AbstractMenu</artifactId>
            <version>2.0.1</version>
        </dependency>
    </dependencies>
</project>
```

## Building

Just clone the repository and do `mvn clean install` or `mvn clean package`. The .jar is in the _target_ directory.