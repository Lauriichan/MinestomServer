package me.lauriichan.minecraft.minestom.util.cli;

final class BooleanArgument extends Argument<Boolean> {

    public BooleanArgument(String name, String valueName, String description, Boolean defaultValue) {
        super(name, valueName, description, defaultValue);
    }

    @Override
    protected String defaultName() {
        return null;
    }

    @Override
    protected Boolean parse(String string) {
        return string.isEmpty() || Boolean.parseBoolean(string);
    }

}
