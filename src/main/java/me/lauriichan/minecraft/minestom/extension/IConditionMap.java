package me.lauriichan.minecraft.minestom.extension;

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
