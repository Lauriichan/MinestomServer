package me.lauriichan.minecraft.minestom.config;

import me.lauriichan.minecraft.minestom.extension.ExtensionPoint;

@ExtensionPoint
public interface ISingleConfigExtension extends IConfigExtension {

    String path();

}
