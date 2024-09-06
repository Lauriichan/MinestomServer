package me.lauriichan.minecraft.minestom.server.config;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;

@ExtensionPoint
public interface ISingleConfigExtension extends IConfigExtension {

    String path();

}
