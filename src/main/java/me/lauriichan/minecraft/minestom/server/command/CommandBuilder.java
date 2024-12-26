package me.lauriichan.minecraft.minestom.server.command;

import net.minestom.server.command.builder.Command;

final class CommandBuilder extends Command {

    public CommandBuilder(final String name) {
        super(name);
    }

    public CommandBuilder(final String name, final String[] aliases) {
        super(name, aliases);
    }

}
