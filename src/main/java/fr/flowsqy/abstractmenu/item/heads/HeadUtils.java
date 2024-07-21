package fr.flowsqy.abstractmenu.item.heads;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public class HeadUtils {

    private static final Field profileField;
    private static final UUID genericUUID;

    static {
        try {
            final Class<?> cSkullMetaClass = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".inventory.CraftMetaSkull");
            profileField = cSkullMetaClass.getDeclaredField("profile");
            profileField.setAccessible(true);
            genericUUID = new UUID(0L, 0L);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException();
        }
    }

    public static void applyProfile(SkullMeta meta, GameProfile profile) {
        Objects.requireNonNull(meta);
        try {
            profileField.set(meta, profile);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameProfile getProfile(String textures, String signature) {
        return getProfile(genericUUID, textures, signature);
    }

    public static GameProfile getProfile(UUID uuid, String textures, String signature) {
        final GameProfile profile = new GameProfile(uuid, "");
        profile.getProperties().put("textures", new Property("textures", textures, signature));
        return profile;
    }

}
