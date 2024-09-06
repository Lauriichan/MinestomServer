package me.lauriichan.minecraft.minestom.server.command.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
@Repeatable(Action.Actions.class)
public @interface Action {

    String value();

    @Target(METHOD)
    @Retention(RUNTIME)
    @interface Actions {

        Action[] value();

    }

}