package me.lauriichan.minecraft.minestom.server.translation.component;

import java.util.Collections;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;

public enum Formatting {

    BLACK('0', NamedTextColor.BLACK),
    DARK_BLUE('1', NamedTextColor.DARK_BLUE),
    DARK_GREEN('2', NamedTextColor.DARK_GREEN),
    DARK_AQUA('3', NamedTextColor.DARK_AQUA),
    DARK_RED('4', NamedTextColor.DARK_RED),
    DARK_PURPLE('5', NamedTextColor.DARK_PURPLE),
    GOLD('6', NamedTextColor.GOLD),
    GRAY('7', NamedTextColor.GRAY),
    DARK_GRAY('8', NamedTextColor.DARK_GRAY),
    BLUE('9', NamedTextColor.BLUE),
    GREEN('a', NamedTextColor.GREEN),
    AQUA('b', NamedTextColor.AQUA),
    RED('c', NamedTextColor.RED),
    LIGHT_PURPLE('d', NamedTextColor.LIGHT_PURPLE),
    YELLOW('e', NamedTextColor.YELLOW),
    WHITE('f', NamedTextColor.WHITE),
    MAGIC('k', TextDecoration.OBFUSCATED),
    BOLD('l', TextDecoration.BOLD),
    STRIKETHROUGH('m', TextDecoration.STRIKETHROUGH),
    UNDERLINE('n', TextDecoration.UNDERLINED),
    ITALIC('i', TextDecoration.ITALIC),
    RESET('r');

    static final Formatting[] VALUES = Formatting.values();
    static final Formatting[] DECORATION = new Formatting[] {
        MAGIC,
        BOLD,
        STRIKETHROUGH,
        UNDERLINE,
        ITALIC,
        RESET
    };
    static final Formatting[] COLORS = new Formatting[] {
        BLACK,
        DARK_BLUE,
        DARK_GREEN,
        DARK_AQUA,
        DARK_RED,
        DARK_PURPLE,
        GOLD,
        GRAY,
        DARK_GRAY,
        BLUE,
        GREEN,
        AQUA,
        RED,
        LIGHT_PURPLE,
        YELLOW,
        WHITE
    };

    private final char legacy;
    private final NamedTextColor velocityColor;
    private final TextDecoration velocityDecoration;

    private Formatting(char legacy) {
        this.legacy = legacy;
        this.velocityColor = null;
        this.velocityDecoration = null;
    }

    private Formatting(char legacy, TextDecoration velocityValue) {
        this.legacy = legacy;
        this.velocityColor = null;
        this.velocityDecoration = velocityValue;
    }

    private Formatting(char legacy, NamedTextColor velocityColor) {
        this.legacy = legacy;
        this.velocityColor = velocityColor;
        this.velocityDecoration = null;
    }

    public TextDecoration velocityDecoration() {
        return velocityDecoration;
    }

    public NamedTextColor velocityColor() {
        return velocityColor;
    }
    
    public boolean isDecoration() {
        return velocityColor == null;
    }

    public Style apply(Style style, boolean state) {
        if (velocityColor != null) {
            if (velocityColor.equals(style.color())) {
                if (!state) {
                    return style.color(null);
                }
                return style;
            }
            if (state) {
                return style.color(velocityColor);
            }
            return style;
        }
        if (velocityDecoration == null) {
            if (!state) {
                return style;
            }
            return style.decorations(Collections.emptyMap()).color(null);
        }
        if (style.hasDecoration(velocityDecoration) == state) {
            return style;
        }
        return style.decoration(velocityDecoration, state);
    }

    public boolean isApplied(Style style) {
        if (velocityColor != null) {
            return velocityColor.equals(style.color());
        }
        if (velocityDecoration == null) {
            return style == Style.empty();
        }
        return style.decoration(velocityDecoration) == TextDecoration.State.TRUE;
    }

    public static Formatting find(TextDecoration velocityDecoration) {
        for (Formatting value : DECORATION) {
            if (value.velocityDecoration == velocityDecoration) {
                return value;
            }
        }
        return null;
    }

    public static Formatting find(NamedTextColor velocityColor) {
        for (Formatting value : COLORS) {
            if (value.velocityColor == velocityColor) {
                return value;
            }
        }
        return null;
    }

    public static Formatting find(char legacy) {
        legacy = Character.toLowerCase(legacy);
        for (Formatting value : VALUES) {
            if (value.legacy == legacy) {
                return value;
            }
        }
        return null;
    }

}
