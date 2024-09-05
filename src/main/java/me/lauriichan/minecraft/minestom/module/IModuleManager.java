package me.lauriichan.minecraft.minestom.module;

import java.nio.file.Path;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.minecraft.minestom.MinestomServer;

public interface IModuleManager {
    
    MinestomServer server();
    
    Path moduleRoot();
    
    Path moduleDataRoot();

    ObjectList<IMinestomModule> modules();
    
    <M extends IMinestomModule> Optional<M> module(Class<M> moduleClass);
    
    <M extends IMinestomModule> Optional<M> module(String id);

    boolean isMavenArtifactKnown(String groupId, String artifactId);

}
