package me.lauriichan.minecraft.minestom.server.command.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Param {

    public static final int TYPE_STRING = 0;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_BYTE = 2;
    public static final int TYPE_SHORT = 3;
    public static final int TYPE_INT = 4;
    public static final int TYPE_LONG = 5;
    public static final int TYPE_FLOAT = 6;
    public static final int TYPE_DOUBLE = 7;
    public static final int TYPE_CLASS = 8;
    
    public static final int TYPE_STRING_ARRAY = 10;
    public static final int TYPE_BOOLEAN_ARRAY = 11;
    public static final int TYPE_BYTE_ARRAY = 12;
    public static final int TYPE_SHORT_ARRAY = 13;
    public static final int TYPE_INT_ARRAY = 14;
    public static final int TYPE_LONG_ARRAY = 15;
    public static final int TYPE_FLOAT_ARRAY = 16;
    public static final int TYPE_DOUBLE_ARRAY = 17;
    public static final int TYPE_CLASS_ARRAY = 18;

    String name();

    int type();

    String stringValue() default "";
    
    boolean booleanValue() default false;

    byte byteValue() default 0;

    short shortValue() default 0;

    int intValue() default 0;

    long longValue() default 0;

    float floatValue() default 0;

    double doubleValue() default 0;

    Class<?> classValue() default Void.class;

    String[] stringArrayValue() default {};

    boolean[] booleanArrayValue() default {};

    byte[] byteArrayValue() default {};

    short[] shortArrayValue() default {};

    int[] intArrayValue() default {};

    long[] longArrayValue() default {};

    float[] floatArrayValue() default {};

    double[] doubleArrayValue() default {};

    Class<?>[] classArrayValue() default {};

}