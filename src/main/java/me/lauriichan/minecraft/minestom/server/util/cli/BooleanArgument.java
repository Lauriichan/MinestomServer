package me.lauriichan.minecraft.minestom.server.util.cli;

final class BooleanArgument extends Argument<Boolean> {

    public BooleanArgument(final String name, final String valueName, final String description, final Boolean defaultValue) {
        super(name, valueName, description, defaultValue);
    }

    @Override
    protected String defaultName() {
        return null;
    }

    @Override
    protected Boolean parse(final String string) {
        return string.isEmpty() || Boolean.parseBoolean(string);
    }

}
