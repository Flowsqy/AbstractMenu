package fr.flo504.abstractmenu.item.heads;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.flo504.reflect.Reflect;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Objects;

public class HeadUtils {

    private static final Field profileField;

    static {
        final Class<?> cSkullMetaClass = Reflect.getClass(Reflect.Commons.CRAFTBUKKIT+"inventory.CraftMetaSkull");
        profileField = Reflect.getField(cSkullMetaClass, "profile");
        profileField.setAccessible(true);
    }

    public static void applyProfile(SkullMeta meta, GameProfile profile){
        Objects.requireNonNull(meta);
        Reflect.set(profileField, meta, profile);
    }

    public static GameProfile getProfile(String textures, String signature){
        final GameProfile profile = new GameProfile(null, "custom");
        profile.getProperties().put("textures", new Property(textures, signature));
        return profile;
    }

}
