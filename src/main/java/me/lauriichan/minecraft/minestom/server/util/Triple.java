package me.lauriichan.minecraft.minestom.server.util;

public record Triple<A, B, C>(A one, B two, C three) {

    public static <D, E, F> Triple<D, E, F> of(final D one, final E two, final F three) {
        return new Triple<>(one, two, three);
    }

}
