package fr.flowsqy.abstractmenu.item;

import java.net.URL;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record HeadData(@Nullable UUID id, @Nullable String name, @NotNull URL textureURL) {
}
