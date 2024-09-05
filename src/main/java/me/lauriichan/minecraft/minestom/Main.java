package me.lauriichan.minecraft.minestom;

public final class Main {

    private Main() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        if (!MinestomArguments.process(args)) {
            return;
        }
        new MinestomServer();
    }

}
