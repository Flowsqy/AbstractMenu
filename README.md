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
    <enchant-1>:
      enchant: <enchant-name>
      level: <enchant-level>
    <enchant-2>:
      enchant: <enchant-name>
      level: <enchant-level>
    ...
  flags:
    - <flag-1>
    - <flag-2>
    - ...
  attributes:
    <attribute-1>:
      attribute: <attribute-name>
      amount: <attribute-amount>
      operation: <attribute-operation>
      slot-group: <attribute-slot>
    <attribute-2>:
      ...
  head-data:
    id: <head-id>
    name: <head-name>
    url: <head-url>

# Where:
#
# <key> is the root key
# <material> [https://minecraft.wiki/w/Java_Edition_data_values#Blocks] :
#   The material of the item. Set it to null will create an empty item
# <name> [String] : is the name. It supports colors
# <line-n> [String] : is a line from the lore of the item. It supports colors
# <unbreakable> [boolean] : Whether the item should be unbreakable
# <amount> [integer] : The number of item
#
# Enchantments:
# <enchant-n> [Section] :
#   An enchantment to apply. The section name does not matter.
# <enchant-name> [https://minecraft.wiki/w/Java_Edition_data_values#Enchantments] : 
#   The enchant name
# <enchant-level> [integer] : The enchantment level
#
# Flags: 
# <flag-n> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/ItemFlag.html] : 
#   An item flag to apply (e.g. to hide enchants)
#
# Attribute:
# <attribute-n> [String] : The name of this attribute. It will be stored with this name in the item.
#   You can have as many attribute as you want
# <attribute-name> [https://minecraft.wiki/w/Attribute#Attributes] :
#   The attribute to modify. It will be skipped if the value is null, if the attribute is invalid or if the attribute
#   isn't compatible with the specified entity type. (REQUIRED only for this attribute, not for the global item)
# <attribute-amount> [double] : The modifier amount. (REQUIRED only for this attribute, not for the global item)
# <attribute-operation> [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/attribute/AttributeModifier.Operation.html] :
#   The operation to apply to the attribute. ADD_NUMBER by default
# <attribute-slot> ['any', 'armor', 'chest', 'feet', 'hand', 'head', 'legs', 'mainhand', 'offhand'] :
#   The slot in which the modifier is active. 'any' by default
#
# Head
# This section is used only if the item is a player skull
# <head-id> [UUID] : The uuid to store in the head (OPTIONAL)
# <head-name> [String] : The name to store in the head (OPTIONAL)
# <head-url> [URL] : The texture url to apply (REQUIRED)
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

Just clone the repository and do `./gradlew clean build`. The .jar is in the _plugin/build/libs_ directory.
