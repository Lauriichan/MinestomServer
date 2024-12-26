package me.lauriichan.minecraft.minestom.server.translation.component;

import java.awt.Color;
import java.util.Objects;

import me.lauriichan.laylib.localization.IMessage;
import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.laylib.logger.util.StringUtil;
import me.lauriichan.minecraft.minestom.server.command.Actor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;

public final class SubComponentBuilder<P extends ComponentBuilder<?, ?>> extends ComponentBuilder<P, SubComponentBuilder<P>> {

    public static SubComponentBuilder<?> parse(final String richString) {
        return ComponentBuilder.create().appendContent(richString);
    }

    private volatile TextComponent component = Component.empty();

    SubComponentBuilder(final P parent) {
        super(parent);
    }

    @SuppressWarnings({
        "unchecked",
        "rawtypes"
    })
    @Override
    public SubComponentBuilder<SubComponentBuilder<P>> newComponent() {
        return new SubComponentBuilder(this);
    }

    private SubComponentBuilder<P> applyStyle(final Style style) {
        component = applyStyleComponent(component, style);
        return this;
    }

    private TextComponent applyStyleComponent(final TextComponent component, final Style style) {
        if (component.style().equals(style)) {
            return component;
        }
        return component.style(style);
    }

    public SubComponentBuilder<P> color(final Color color) {
        return color(TextColor.color(color.getRGB()));
    }

    public SubComponentBuilder<P> color(final TextColor color) {
        return applyStyle(component.style().color(color));
    }

    public TextColor color() {
        return component.style().color();
    }

    public SubComponentBuilder<P> apply(final Formatting formatting) {
        return applyStyle(formatting.apply(component.style(), true));
    }

    public SubComponentBuilder<P> unapply(final Formatting formatting) {
        return applyStyle(formatting.apply(component.style(), false));
    }

    public boolean hasFormatting(final Formatting formatting) {
        return formatting.isApplied(component.style());
    }

    private TextComponent applyTextComponent(final TextComponent component, final String text) {
        if (component.content().equals(text)) {
            return component;
        }
        return component.content(text);
    }

    public SubComponentBuilder<P> text(final String text) {
        component = applyTextComponent(component, text);
        return this;
    }

    public SubComponentBuilder<P> appendText(final String text) {
        return text(component.content() + text);
    }

    public SubComponentBuilder<P> appendChar(final char ch) {
        return text(component.content() + ch);
    }

    public String text() {
        return component.content();
    }

    public SubComponentBuilder<P> clickUrl(final String url, final Object... format) {
        return clickUrl(StringUtil.format(url, format));
    }

    public SubComponentBuilder<P> clickFile(final String file, final Object... format) {
        return clickFile(StringUtil.format(file, format));
    }

    public SubComponentBuilder<P> clickCopy(final String copy, final Object... format) {
        return clickCopy(StringUtil.format(copy, format));
    }

    public SubComponentBuilder<P> clickSuggest(final String suggest, final Object... format) {
        return clickSuggest(StringUtil.format(suggest, format));
    }

    public SubComponentBuilder<P> clickRun(final String run, final Object... format) {
        return clickRun(StringUtil.format(run, format));
    }

    public SubComponentBuilder<P> clickUrl(final String url) {
        return click(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    public SubComponentBuilder<P> clickFile(final String file) {
        return click(ClickEvent.clickEvent(ClickEvent.Action.OPEN_FILE, file));
    }

    public SubComponentBuilder<P> clickCopy(final String copy) {
        return click(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy));
    }

    public SubComponentBuilder<P> clickSuggest(final String suggest) {
        return click(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
    }

    public SubComponentBuilder<P> clickRun(final String run) {
        return click(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, run));
    }

    public SubComponentBuilder<P> click(final ClickEvent event) {
        if (Objects.equals(component.clickEvent(), event)) {
            return this;
        }
        component = component.clickEvent(event);
        return this;
    }

    public ClickEvent click() {
        return component.clickEvent();
    }

    public SubComponentBuilder<P> hoverText(final ComponentBuilder<?, ?> builder) {
        if (builder == null) {
            return this;
        }
        return hover(HoverEvent.showText(builder.buildComponent()));
    }

    public SubComponentBuilder<P> hoverText(final MessageProvider provider, final String language) {
        if (provider == null) {
            return this;
        }
        return hoverText(provider.getMessage(language));
    }

    public SubComponentBuilder<P> hoverText(final MessageProvider provider) {
        if (provider == null) {
            return this;
        }
        return hoverText(provider.getMessage(Actor.DEFAULT_LANGUAGE));
    }

    public SubComponentBuilder<P> hoverText(final IMessage message) {
        if (message == null) {
            return this;
        }
        return hoverText(message.value());
    }

    public SubComponentBuilder<P> hoverText(final String string) {
        if (string == null) {
            return this;
        }
        return hover(HoverEvent.showText(ComponentBuilder.create().appendContent(string).buildComponent()));
    }

    public SubComponentBuilder<P> hover(final HoverEvent<?> event) {
        if (Objects.equals(component.hoverEvent(), event)) {
            return this;
        }
        component = component.hoverEvent(event);
        return this;
    }

    public HoverEvent<?> hover() {
        return component.hoverEvent();
    }

    public SubComponentBuilder<P> copyFrom(final SubComponentBuilder<?> component) {
        return copyFrom(component, false);
    }

    public SubComponentBuilder<P> copyFrom(final SubComponentBuilder<?> component, final boolean copyReset) {
        if (Formatting.RESET.isApplied(component.component.style()) && !copyReset) {
            return this;
        }
        return applyStyle(component.component.style());
    }

    public SubComponentBuilder<P> loadFrom(final SubComponentBuilder<?> component) {
        this.component = applyTextComponent(applyStyleComponent(this.component, component.component.style()),
            component.component.content());
        return this;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && component.content().isEmpty();
    }

    @Override
    public Component buildComponent() {
        return appendComponents(component);
    }

    public P finish() {
        parent.add(this);
        return parent;
    }

}
