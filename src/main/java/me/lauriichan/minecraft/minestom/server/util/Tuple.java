package me.lauriichan.minecraft.minestom.server.util;

public record Tuple<A, B>(A one, B two) {

    public static <C, D> Tuple<C, D> of(C one, D two) {
        return new Tuple<>(one, two);
    }

}
