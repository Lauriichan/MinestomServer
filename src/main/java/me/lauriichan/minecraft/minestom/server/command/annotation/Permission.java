package me.lauriichan.minecraft.minestom.server.command.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(value = {
    METHOD,
    TYPE
})
public @interface Permission {

    String value();

}