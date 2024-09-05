package me.lauriichan.minecraft.minestom.util.cli;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public final class ArgumentReader {

    public static enum Token {
        NAME,
        NAME_SHORT,
        VALUE,
        LINKED_VALUE,
        END;
    }

    private final ObjectList<String> values;
    private final IntList linked;
    private int index = 0;

    protected Token token;
    protected String value;

    public ArgumentReader(String[] array) {
        ObjectArrayList<String> list = new ObjectArrayList<>();
        IntArrayList linked = new IntArrayList();
        for (String argument : array) {
            if (!(argument.contains("-") && argument.contains("="))) {
                list.add(argument);
                continue;
            }
            int whitespace = argument.indexOf(' ');
            int equals = argument.indexOf('=');
            if (whitespace != -1 && whitespace <= equals) {
                continue;
            }
            list.add(argument.substring(0, equals));
            list.add(argument.substring(equals + 1, argument.length()));
            linked.add(list.size());
        }
        this.values = ObjectLists.unmodifiable(list);
        this.linked = IntLists.unmodifiable(linked);
    }

    public final Token token() throws IllegalArgumentException {
        if (token != null) {
            return token;
        }
        return token = nextToken();
    }

    public final String value() throws IllegalArgumentException {
        if (token() == Token.END) {
            return null;
        }
        token = null;
        return value;
    }

    private Token nextToken() throws IllegalArgumentException {
        if (index >= values.size()) {
            return Token.END;
        }
        String current = values.get(index++);
        if (linked.contains(index)) {
            value = current;
            return Token.LINKED_VALUE;
        }
        if (current.startsWith("-")) {
            current = current.substring(1);
            if (current.startsWith("-")) {
                value = current.substring(1);
                return Token.NAME;
            }
            value = current;
            return Token.NAME_SHORT;
        }
        value = current;
        return Token.VALUE;
    }

}
