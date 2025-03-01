package fr.flowsqy.abstractmenu.item.heads;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class HeadUtils {

    private static final Field profileField;
    private static final Constructor<?> profileConstructor;
    private static final UUID genericUUID;

    static {
        genericUUID = new UUID(0L, 0L);
        try {
            final Class<?> cSkullMetaClass = Class
                    .forName(Bukkit.getServer().getClass().getPackage().getName() + ".inventory.CraftMetaSkull");
            profileField = cSkullMetaClass.getDeclaredField("profile");
            profileField.setAccessible(true);
            final Class<?> nmsResolvableProfileClass = Class
                    .forName("net.minecraft.world.item.component.ResolvableProfile");
            profileConstructor = nmsResolvableProfileClass.getDeclaredConstructor(GameProfile.class);
            profileConstructor.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void applyProfile(SkullMeta meta, GameProfile profile) {
        Objects.requireNonNull(meta);
        try {
            profileField.set(meta, profileConstructor.newInstance(profile));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
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
