package me.lauriichan.minecraft.minestom.util.cli;

final class StringArgument extends Argument<String> {

    public StringArgument(String name, String valueName, String description, String defaultValue) {
        super(name, valueName, description, defaultValue);
    }

    @Override
    protected String defaultName() {
        return "STRING";
    }

    @Override
    protected String parse(String string) {
        return string;
    }

}
