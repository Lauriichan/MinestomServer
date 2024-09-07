package me.lauriichan.minecraft.minestom.server.command;

import me.lauriichan.minecraft.minestom.server.command.annotation.Param;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;

public final class ArgumentMapBuilder {
    
    public static ArgumentMapBuilder builder() {
        return new ArgumentMapBuilder();
    }
    
    public static IArgumentMap of(Param[] params) {
        return new ArgumentMapBuilder().add(params).build();
    }

    private IArgumentMap map;

    private ArgumentMapBuilder() {}

    public IArgumentMap build() {
        return map == null ? IArgumentMap.empty() : map;
    }

    private IArgumentMap map() {
        if (map != null) {
            return map;
        }
        return map = IArgumentMap.newMap();
    }

    public ArgumentMapBuilder add(Param[] params) {
        loop:
        for (Param param : params) {
            switch (param.type()) {
            case Param.TYPE_STRING:
                map().set(param.name(), param.stringValue());
                break;
            case Param.TYPE_BOOLEAN:
                map().set(param.name(), param.booleanValue());
                break;
            case Param.TYPE_BYTE:
                map().set(param.name(), param.byteValue());
                break;
            case Param.TYPE_SHORT:
                map().set(param.name(), param.shortValue());
                break;
            case Param.TYPE_INT:
                map().set(param.name(), param.intValue());
                break;
            case Param.TYPE_LONG:
                map().set(param.name(), param.longValue());
                break;
            case Param.TYPE_FLOAT:
                map().set(param.name(), param.floatValue());
                break;
            case Param.TYPE_DOUBLE:
                map().set(param.name(), param.doubleValue());
                break;
            case Param.TYPE_CLASS:
                map().set(param.name(), param.classValue());
                break;
            case Param.TYPE_STRING_ARRAY:
                map().set(param.name(), param.stringArrayValue());
                break;
            case Param.TYPE_BOOLEAN_ARRAY:
                map().set(param.name(), param.booleanArrayValue());
                break;
            case Param.TYPE_BYTE_ARRAY:
                map().set(param.name(), param.byteArrayValue());
                break;
            case Param.TYPE_SHORT_ARRAY:
                map().set(param.name(), param.shortArrayValue());
                break;
            case Param.TYPE_INT_ARRAY:
                map().set(param.name(), param.intArrayValue());
                break;
            case Param.TYPE_LONG_ARRAY:
                map().set(param.name(), param.longArrayValue());
                break;
            case Param.TYPE_FLOAT_ARRAY:
                map().set(param.name(), param.floatArrayValue());
                break;
            case Param.TYPE_DOUBLE_ARRAY:
                map().set(param.name(), param.doubleArrayValue());
                break;
            case Param.TYPE_CLASS_ARRAY:
                map().set(param.name(), param.classArrayValue());
                break;
            default:
                continue loop;
            }
        }
        return this;
    }

}
