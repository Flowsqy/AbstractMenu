package fr.flo504.abstractmenu.parser.item;


import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

public class ItemInfo {

    private final String id;
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final int amount;
    private final boolean glow;

    public ItemInfo(String id, Material material, String name, List<String> lore, int amount, boolean glow) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(material);
        Objects.requireNonNull(lore);
        this.id = id;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.amount = amount;
        this.glow = glow;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isGlow() {
        return glow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemInfo itemInfo = (ItemInfo) o;
        return amount == itemInfo.amount &&
                glow == itemInfo.glow &&
                id.equals(itemInfo.id) &&
                material == itemInfo.material &&
                Objects.equals(name, itemInfo.name) &&
                lore.equals(itemInfo.lore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, material, name, lore, amount, glow);
    }

    @Override
    public String toString() {
        return "ItemInfo{" +
                "id='" + id + '\'' +
                ", material=" + material +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", amount=" + amount +
                ", glow=" + glow +
                '}';
    }
}
