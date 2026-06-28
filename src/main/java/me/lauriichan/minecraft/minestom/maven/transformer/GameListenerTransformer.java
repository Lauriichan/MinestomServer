package me.lauriichan.minecraft.minestom.maven.transformer;

import static me.lauriichan.maven.sourcemod.api.SourceTransformerUtils.importClass;
import static me.lauriichan.maven.sourcemod.api.SourceTransformerUtils.removeMethod;

import java.util.List;
import java.util.Objects;

import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

import me.lauriichan.maven.sourcemod.api.ISourceTransformer;
import me.lauriichan.minecraft.minestom.server.game.EventHandler;
import me.lauriichan.minecraft.minestom.server.game.phased.Phased;
import net.minestom.server.event.EventListener.Result;

public final class GameListenerTransformer implements ISourceTransformer {

    private static final String GAME_STATE = "me.lauriichan.minecraft.minestom.server.game.GameState";
    private static final String GAME_LISTENER = "me.lauriichan.minecraft.minestom.server.game.GameListener";

    private static final String[] IMPORTS = new String[] {
        GAME_STATE,
        "me.lauriichan.minecraft.minestom.server.game.PhasedEventReceiver",
        "me.lauriichan.minecraft.minestom.server.game.PhasedEventContainer",
        "me.lauriichan.minecraft.minestom.server.game.phased.PhasedObjRef",
        "me.lauriichan.minecraft.minestom.server.game.PhasedEventReceiver.IEventFunc"
    };

    @Override
    public boolean canTransform(final JavaSource<?> source) {
        if (!(source instanceof final JavaClassSource classSource)) {
            return false;
        }
        return !classSource.isAbstract() && !classSource.isRecord() && !classSource.isInterface()
            && (classSource.hasInterface(GAME_LISTENER));
    }

    @Override
    public void transform(final JavaSource<?> source) {
        final JavaClassSource clazz = (JavaClassSource) source;

        String gameType = clazz.getInterfaces().stream().filter(str -> str.startsWith(GAME_LISTENER)).findFirst().orElse(null);
        {
            int startIndex;
            if (gameType == null || (startIndex = gameType.indexOf('<')) == -1) {
                return;
            }
            gameType = gameType.substring(startIndex + 1, gameType.length() - 1);
        }

        StringBuilder containerBuilder = new StringBuilder("""
            @Override
            @SuppressWarnings("unchecked")
            public PhasedEventContainer<%1$s> newContainer(GameState<%1$s> gameState) {
                return new PhasedEventContainer<>(gameState, this, new PhasedEventReceiver[] {
            """.formatted(gameType));
        int amount = 0;
        for (final MethodSource<JavaClassSource> method : clazz.getMethods()) {
            if (!method.hasAnnotation(EventHandler.class)) {
                continue;
            }
            Type<?> type = method.getReturnType();
            if (!type.isType(Result.class) && !type.isType(void.class) && !type.isType(Void.class)) {
                continue;
            }
            final List<ParameterSource<JavaClassSource>> params = method.getParameters();
            if (params.isEmpty() || params.size() > 2) {
                continue;
            }
            Type<JavaClassSource> paramType = params.get(0).getType();
            if (paramType.isType(GAME_STATE)) {
                if (params.size() == 1) {
                    continue;
                }
                paramType = params.get(1).getType();
            }
            if (amount++ != 0) {
                containerBuilder.append(",");
            }
            String eventName = paramType.getQualifiedName();
            containerBuilder.append("\n\t\tnew PhasedEventReceiver<").append(gameType).append(", ").append(eventName).append(">(\"")
                .append(method.getName()).append("\", ").append(eventName).append(".class, new PhasedObjRef<>(IEventFunc.of(");
            containerBuilder.append("this::").append(method.getName()).append("), ");
            if (method.hasAnnotation(Phased.class)) {
                AnnotationSource<?> annotation = method.getAnnotation(Phased.class);
                containerBuilder.append(bool(annotation, eventName, false));
                String literal = annotation.getLiteralValue("phase");
                if (literal != null) {
                    containerBuilder.append(", ").append(literal).append(".class");
                }
            } else {
                containerBuilder.append("false");
            }
            containerBuilder.append("), ").append(bool(method.getAnnotation(EventHandler.class), "ignoreCancelled", false)).append(')');
        }
        if (amount == 0) {
            return;
        }

        removeMethod(clazz, "newContainer", GAME_STATE);

        for (String importType : IMPORTS) {
            importClass(clazz, importType);
        }

        containerBuilder.append('\n').append("""
                });
            }
            """);
        clazz.addMethod(containerBuilder.toString());
        containerBuilder = null;
    }

    private boolean bool(AnnotationSource<?> src, String name, boolean fallback) {
        String str = src.getLiteralValue(name);
        if (str == null || str.isBlank()) {
            return fallback;
        }
        return Objects.equals(str, "true");
    }

}
