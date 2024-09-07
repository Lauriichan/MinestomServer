package me.lauriichan.minecraft.minestom.server.command;

import net.minestom.server.command.builder.Command;

final class CommandBuilder extends Command {

    public CommandBuilder(String name) {
        super(name);
    }

    public CommandBuilder(String name, String[] aliases) {
        super(name, aliases);
    }

}
