package me.lauriichan.minecraft.minestom.server.permission;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IPermissionAccess {
    
    boolean isSet(String string);
    
    boolean isAllowed(String string);
    
    Optional<IPermissionContext> context(String string);
    
    /**
     * @return a completable future returning the same object
     */
    CompletableFuture<IPermissionAccess> update();

}
