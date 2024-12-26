package me.lauriichan.minecraft.minestom.server.util.cli;

final class NumberArgument extends Argument<Number> {

    public NumberArgument(final String name, final String valueName, final String description, final Number defaultValue) {
        super(name, valueName, description, defaultValue);
    }

    @Override
    protected String defaultName() {
        return "NUMBER";
    }

    @Override
    protected Number parse(final String string) {
        if (string.isEmpty()) {
            return 0;
        }
        if (string.contains(".")) {
            return Double.parseDouble(string);
        }
        return Long.parseLong(string);
    }

}
