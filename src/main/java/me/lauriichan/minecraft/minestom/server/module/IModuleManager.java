package me.lauriichan.minecraft.minestom.server.module;

import java.nio.file.Path;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.minecraft.minestom.server.MinestomServer;

public interface IModuleManager {
    
    MinestomServer server();
    
    Path moduleRoot();
    
    Path moduleDataRoot();

    ObjectList<IMinestomModule> modules();
    
    <M extends IMinestomModule> Optional<M> module(Class<M> moduleClass);
    
    <M extends IMinestomModule> Optional<M> module(String id);
    
    default Optional<IMinestomModule> findModule(Class<?> anyClass) {
        return Optional.ofNullable(anyClass).flatMap(clz -> findModule(clz.getClassLoader()));
    }
    
    Optional<IMinestomModule> findModule(ClassLoader classLoader);

    boolean isMavenArtifactKnown(String groupId, String artifactId);

    Class<?> getClassByName(String name);
    
}
