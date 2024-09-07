package me.lauriichan.minecraft.minestom.server.command.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Command {

    String name();

    String[] aliases() default {};

    String description() default "";

}