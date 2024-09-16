package me.lauriichan.minecraft.minestom.server.permission;

import java.util.Collection;
import java.util.Optional;

public interface IPermissionContext {
    
    boolean isAllowed();
    
    Optional<String> getData(String key);
    
    Collection<String> getDataCollection(String key);
    
    boolean hasData(String key);

}
