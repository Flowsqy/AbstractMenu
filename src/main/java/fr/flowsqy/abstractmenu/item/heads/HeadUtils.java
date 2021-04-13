package fr.flowsqy.abstractmenu.item.heads;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.flo504.reflect.Reflect;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public class HeadUtils {

    private static final Field profileField;
    private static final UUID genericUUID;

    static {
        final Class<?> cSkullMetaClass = Reflect.getClass(Reflect.Commons.CRAFTBUKKIT + "inventory.CraftMetaSkull");
        profileField = Reflect.getField(cSkullMetaClass, "profile");
        profileField.setAccessible(true);
        genericUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    public static void applyProfile(SkullMeta meta, GameProfile profile) {
        Objects.requireNonNull(meta);
        Reflect.set(profileField, meta, profile);
    }

    public static GameProfile getProfile(String textures, String signature) {
        return getProfile(genericUUID, textures, signature);
    }

    public static GameProfile getProfile(UUID uuid, String textures, String signature) {
        final GameProfile profile = new GameProfile(uuid, null);
        profile.getProperties().put("textures", new Property("textures", textures, signature));
        return profile;
    }

}
