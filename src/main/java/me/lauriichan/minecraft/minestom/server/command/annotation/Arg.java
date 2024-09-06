package me.lauriichan.minecraft.minestom.server.command.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Arg {

    String name() default "";

    int index() default -1;
    
    boolean optional() default false;
    
    Param[] params() default {};

}