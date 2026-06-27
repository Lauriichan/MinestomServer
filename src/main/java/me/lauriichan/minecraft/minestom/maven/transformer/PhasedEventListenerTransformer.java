package me.lauriichan.minecraft.minestom.maven.transformer;

import static me.lauriichan.maven.sourcemod.api.SourceTransformerUtils.importClass;
import static me.lauriichan.maven.sourcemod.api.SourceTransformerUtils.removeMethod;

import java.util.List;

import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

import me.lauriichan.maven.sourcemod.api.ISourceTransformer;
import me.lauriichan.minecraft.minestom.server.game.EventHandler;
import me.lauriichan.minecraft.minestom.server.game.IPhasedListener;
import me.lauriichan.minecraft.minestom.server.game.PhasedEventContainer;
import me.lauriichan.minecraft.minestom.server.game.PhasedEventReceiver;
import net.minestom.server.event.EventListener.Result;

public final class PhasedEventListenerTransformer implements ISourceTransformer {

    private static final String GAME_STATE = "me.lauriichan.minecraft.minestom.server.game.GameState";

    @Override
    public boolean canTransform(final JavaSource<?> source) {
        if (!(source instanceof final JavaClassSource classSource)) {
            return false;
        }
        return !classSource.isAbstract() && !classSource.isRecord() && !classSource.isInterface()
            && (classSource.hasInterface(IPhasedListener.class));
    }

    @Override
    public void transform(final JavaSource<?> source) {
        final JavaClassSource clazz = (JavaClassSource) source;

        StringBuilder containerBuilder = new StringBuilder("""
            @Override
            public PhasedEventContainer<G> newContainer(GameState<G> gameState) {
                return new PhasedEventContainer(gameState, this, new PhasedEventReceiver<>[] {
            """);
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
            containerBuilder.append("\n\t\tnew PhasedEventReceiver<>(").append(method.getName()).append(", ").append(paramType.getQualifiedName()).append(".class, this::")
                .append(method.getName()).append(", ")
                .append(Boolean.parseBoolean(method.getAnnotation(EventHandler.class).getLiteralValue("ignoreCancelled"))).append(')');
        }
        if (amount == 0) {
            return;
        }

        removeMethod(clazz, "newContainer", GAME_STATE);

        importClass(clazz, GAME_STATE);
        importClass(clazz, PhasedEventContainer.class);
        importClass(clazz, PhasedEventReceiver.class);

        containerBuilder.append('\n').append("""
                });
            }
            """);
        clazz.addMethod(containerBuilder.toString());
        containerBuilder = null;
    }

}
