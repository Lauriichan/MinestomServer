package me.lauriichan.minecraft.minestom.server.extension;

public interface IConditionMap {

    static IConditionMap empty() {
        return EmptyConditionMap.EMPTY;
    }

    boolean value(String property);

    void value(String property, boolean value);

    boolean set(String property);

    void unset(String property);

    boolean locked();

}
