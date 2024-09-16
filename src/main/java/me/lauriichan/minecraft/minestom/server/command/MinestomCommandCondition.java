package me.lauriichan.minecraft.minestom.server.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.condition.CommandCondition;

final class MinestomCommandCondition implements CommandCondition {

    private final IMinestomModule module;
    private final String permission;

    public MinestomCommandCondition(final IMinestomModule module, final String permission) {
        this.module = module;
        this.permission = permission;
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender, @Nullable String commandString) {
        return module.actorMap().actor(sender).isPermitted(permission);
    }

}
