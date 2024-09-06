package me.lauriichan.minecraft.minestom.server.extension;

final class EmptyConditionMap implements IConditionMap {

    static final EmptyConditionMap EMPTY = new EmptyConditionMap();

    private EmptyConditionMap() {
        if (EMPTY != null) {
            throw new UnsupportedOperationException("Only one instance allowed");
        }
    }

    @Override
    public boolean value(String property) {
        return false;
    }

    @Override
    public void value(String property, boolean value) {}

    @Override
    public boolean set(String property) {
        return false;
    }

    @Override
    public void unset(String property) {}

    @Override
    public boolean locked() {
        return true;
    }

}
