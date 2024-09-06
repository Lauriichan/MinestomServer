package me.lauriichan.minecraft.minestom.server.translation.component;

import java.awt.Color;

import me.lauriichan.minecraft.minestom.server.translation.component.ComponentBuilder.TextAppender;
import me.lauriichan.minecraft.minestom.server.util.color.ColorParser;

final class ComponentBuilderUtils {

    private static record ColorResult(Color color, int length) {}

    private ComponentBuilderUtils() {
        throw new UnsupportedOperationException();
    }

    public static <S extends ComponentBuilder<?, ?>> SubComponentBuilder<S> append(SubComponentBuilder<S> builder, String content) {
        String[] lines = content.split("\n");
        if (lines.length > 1) {
            SubComponentBuilder<?> component = builder.newComponent();
            SubComponentBuilder<?> lastComponent = component;
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    component.newComponent().copyFrom(lastComponent).text("\n").finish();
                    continue;
                }
                lastComponent = appendLine(component, lastComponent, lines[i]);
                if (i + 1 != lines.length) {
                    lastComponent.appendText("\n");
                }
            }
            component.finish();
            return builder;
        }
        appendLine(builder, null, content);
        return builder;
    }

    private static <S extends ComponentBuilder<?, ?>> SubComponentBuilder<?> appendLine(SubComponentBuilder<S> builder,
        SubComponentBuilder<?> copyFormatting, String content) {
        SubComponentBuilder<?> component = builder.newComponent();
        if (copyFormatting != null) {
            component.copyFrom(copyFormatting);
        }
        TextAppender<?> appender = null;
        int last = 0;
        for (int i = content.indexOf('&'); i != -1; i = content.indexOf('&', i + 1)) {
            if (appender != null) {
                appender.text(content.substring(last, i)).finish();
                appender = null;
                component = component.finish().newComponent().copyFrom(component);
            } else {
                component.appendText(content.substring(last, i));
            }
            if (content.charAt(i + 1) != '#') {
                // Apply formatting
                Formatting format = Formatting.find(content.charAt(i + 1));
                if (format == null) {
                    continue;
                }
                last = i + 2;
                if (!component.isEmpty()) {
                    component = component.finish().newComponent().copyFrom(component);
                }
                if (format == Formatting.RESET) {
                    component = component.finish().newComponent();
                } else {
                    component.apply(format);
                }
                continue;
            }
            if (content.charAt(i + 2) != '[') {
                // Apply hex color
                ColorResult result = parseColor(content, i + 2);
                if (result == null) {
                    continue;
                }
                last = i + 2 + result.length();
                if (content.charAt(last) == ';') {
                    last++;
                }
                if (!component.isEmpty()) {
                    component = component.finish().newComponent().copyFrom(component);
                }
                component.color(result.color());
                continue;
            }
            // Apply hex gradient
            ColorResult start = parseColor(content, i + 3);
            if (start == null || content.charAt(i + 3 + start.length()) != '-') {
                continue;
            }
            ColorResult end = parseColor(content, i + 4 + start.length());
            if (end == null) {
                continue;
            }
            int colorEnd = end.length() + i + 4 + start.length();
            int colorAmount = -1;
            int offsetIdx = colorEnd;
            if (content.length() + 1 != colorEnd && content.charAt(colorEnd) != ']') {
                if (content.charAt(colorEnd) != '/') {
                    continue;
                }
                offsetIdx = content.indexOf(']', colorEnd);
                if (offsetIdx == -1) {
                    continue;
                }
                try {
                    colorAmount = Integer.parseInt(content.substring(colorEnd + 1, offsetIdx));
                } catch (NumberFormatException nfe) {
                    continue;
                }
            }
            last = offsetIdx + 1;
            if (!component.isEmpty()) {
                component = component.finish().newComponent().copyFrom(component);
            }
            appender = component.newText().startColor(start.color()).endColor(end.color()).colorAmount(colorAmount);
        }
        if (last == 0 || last != content.length()) {
            if (appender != null) {
                appender.text(content.substring(last, content.length())).finish();
                component.finish();
            } else {
                component.appendText(content.substring(last, content.length())).finish();
            }
        } else {
            component.finish();
        }
        return component;
    }

    private static ColorResult parseColor(String string, int startIndex) {
        int end = Math.min(startIndex + 6, string.length());
        final StringBuilder hex = new StringBuilder();
        for (int i = startIndex; i < end; i++) {
            final char ch = string.charAt(i);
            if (ch >= 'A' && ch <= 'F') {
                hex.append((char) (ch + 32));
                continue;
            }
            if ((ch >= 'a' && ch <= 'f') || (ch >= '0' && ch <= '9')) {
                hex.append(ch);
                continue;
            }
            break;
        }
        Color color = ColorParser.parseOrNull(hex.toString());
        if (color == null) {
            return null;
        }
        return new ColorResult(color, hex.length());
    }

}
