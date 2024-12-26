package me.lauriichan.minecraft.minestom.server.extension;

final class EmptyConditionMap implements IConditionMap {

    static final EmptyConditionMap EMPTY = new EmptyConditionMap();

    private EmptyConditionMap() {
        if (EMPTY != null) {
            throw new UnsupportedOperationException("Only one instance allowed");
        }
    }

    @Override
    public boolean value(final String property) {
        return false;
    }

    @Override
    public void value(final String property, final boolean value) {}

    @Override
    public boolean set(final String property) {
        return false;
    }

    @Override
    public void unset(final String property) {}

    @Override
    public boolean locked() {
        return true;
    }

}
