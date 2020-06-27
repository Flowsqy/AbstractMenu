package fr.flo504.abstractmenu.item;

import com.mojang.authlib.GameProfile;
import fr.flo504.abstractmenu.item.heads.HeadUtils;
import fr.flo504.abstractmenu.utils.Cloneable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BaseItem implements Cloneable {

    private String name;
    private Material material;
    private List<String> lore;
    private final GlowItem glow;
    private int amount;

    private ItemStack item;

    public BaseItem(ItemStack item) {
        Objects.requireNonNull(item);

        this.item = item;

        this.material = item.getType();
        this.amount = item.getAmount();

        final ItemMeta meta = item.getItemMeta();

        assert meta != null;

        this.name = meta.getDisplayName();
        this.lore = meta.getLore();
        this.glow = new GlowItem(item);

    }

    public BaseItem(String name, Material material, List<String> lore, boolean glow, int amount) {
        this.name = formatName(name);
        this.material = material;
        this.lore = formatLore(lore);
        this.glow = new GlowItem(!glow);
        this.amount = amount;
        this.create();
    }

    private BaseItem(BaseItem item){
        this(
                item.getName(),
                item.getMaterial(),
                item.getLore(),
                item.isGlow(),
                item.getAmount()
        );
    }

    private void create() {

        this.item = new ItemStack(this.getMaterial(), this.getAmount());

        final ItemMeta meta = this.item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(this.name);
        meta.setLore(this.lore);

        this.item.setItemMeta(meta);

        glow.setGlow(!glow.isGlow(), item);
    }

    protected String formatName(String name){
        return ChatColor.RESET+name;
    }

    protected List<String> formatLore(List<String> lore){
        return lore.stream()
                .map(line -> ChatColor.RESET + line)
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        if(this.name.equals(name))
            return;

        this.name = formatName(name);

        final ItemMeta meta = item.getItemMeta();

        assert meta != null;

        meta.setDisplayName(this.name);

        item.setItemMeta(meta);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {

        if(this.material == material)
            return;

        this.material = material;

        item.setType(this.material);
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {

        if(this.lore == lore)
            return;

        this.lore = formatLore(lore);

        final ItemMeta meta = item.getItemMeta();

        assert meta != null;

        meta.setLore(this.lore);

        item.setItemMeta(meta);
    }

    public boolean isGlow(){
        return glow.isGlow();
    }

    public void setGlow(boolean glow) {
        this.glow.setGlow(glow, item);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {

        if(this.amount == amount)
            return;

        this.amount = amount;

        item.setAmount(this.amount);

    }

    public void setOwner(String name) {

        if (name != null && name.length() > 16) {
            return;
        }

        if(!(item.getItemMeta() instanceof SkullMeta))
            throw new UnsupportedOperationException("The item is not a player skull");

        final SkullMeta meta = (SkullMeta) item.getItemMeta();

        if(name == null)
            HeadUtils.applyProfile(meta, null);
        else
            HeadUtils.applyProfile(meta, new GameProfile(null, name));

        item.setItemMeta(meta);
    }

    public void setOwner(Player p) {

        if(!(item.getItemMeta() instanceof SkullMeta)){
            throw new UnsupportedOperationException("The item is not a player skull");
        }

        final SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwningPlayer(p);

        item.setItemMeta(meta);
    }

    public void setOwner(GameProfile profile){
        if(!(item.getItemMeta() instanceof SkullMeta)){
            throw new UnsupportedOperationException("The item is not a player skull");
        }

        final SkullMeta meta = (SkullMeta) item.getItemMeta();

        HeadUtils.applyProfile(meta, profile);

        item.setItemMeta(meta);
    }

    public void setOwner(String textures, String signature){
        setOwner(HeadUtils.getProfile(textures, signature));
    }

    public final ItemStack getItem() {
        return this.item;
    }

    @Override
    public BaseItem clone() {
        return new BaseItem(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseItem baseItem = (BaseItem) o;
        return amount == baseItem.amount &&
                Objects.equals(name, baseItem.name) &&
                material == baseItem.material &&
                Objects.equals(lore, baseItem.lore) &&
                Objects.equals(glow, baseItem.glow) &&
                Objects.equals(item, baseItem.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, material, lore, glow, amount, item);
    }

    @Override
    public String toString() {
        return "BaseItem{" +
                "name='" + name + '\'' +
                ", material=" + material +
                ", lore=" + lore +
                ", glow=" + glow +
                ", amount=" + amount +
                ", item=" + item +
                '}';
    }
}