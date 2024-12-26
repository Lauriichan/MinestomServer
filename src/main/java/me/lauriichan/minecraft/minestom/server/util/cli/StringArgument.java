package me.lauriichan.minecraft.minestom.server.util.cli;

final class StringArgument extends Argument<String> {

    public StringArgument(final String name, final String valueName, final String description, final String defaultValue) {
        super(name, valueName, description, defaultValue);
    }

    @Override
    protected String defaultName() {
        return "STRING";
    }

    @Override
    protected String parse(final String string) {
        return string;
    }

}
