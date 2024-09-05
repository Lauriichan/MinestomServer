package me.lauriichan.minecraft.minestom.util.cli;

final class NumberArgument extends Argument<Number> {

    public NumberArgument(String name, String valueName, String description, Number defaultValue) {
        super(name, valueName, description, defaultValue);
    }
    
    @Override
    protected String defaultName() {
        return "NUMBER";
    }

    @Override
    protected Number parse(String string) {
        if (string.isEmpty()) {
            return 0;
        }
        if (string.contains(".")) {
            return Double.parseDouble(string);
        }
        return Long.parseLong(string);
    }

}
