package me.lauriichan.minecraft.minestom.maven.transformer;

import static me.lauriichan.maven.sourcemod.api.SourceTransformerUtils.*;

import java.util.List;

import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

import me.lauriichan.maven.sourcemod.api.ISourceTransformer;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.signal.ISignalHandler;
import me.lauriichan.minecraft.minestom.server.signal.ISignalHandlerExtension;
import me.lauriichan.minecraft.minestom.server.signal.SignalContainer;
import me.lauriichan.minecraft.minestom.server.signal.SignalContext;
import me.lauriichan.minecraft.minestom.server.signal.SignalHandler;
import me.lauriichan.minecraft.minestom.server.signal.SignalReceiver;

public final class SignalHandlerTransformer implements ISourceTransformer {

    @Override
    public boolean canTransform(JavaSource<?> source) {
        if (!(source instanceof final JavaClassSource classSource)) {
            return false;
        }
        return !classSource.isAbstract() && !classSource.isRecord() && !classSource.isInterface()
            && (classSource.hasInterface(ISignalHandler.class) || classSource.hasInterface(ISignalHandlerExtension.class));
    }

    @Override
    public void transform(JavaSource<?> source) {
        final JavaClassSource clazz = (JavaClassSource) source;

        StringBuilder containerBuilder = new StringBuilder("""
            @Override
            public SignalContainer newContainer(IMinestomModule module) {
                return new SignalContainer(module, this, new SignalReceiver[] {
            """);
        int amount = 0;
        for (final MethodSource<JavaClassSource> method : clazz.getMethods()) {
            if (!method.hasAnnotation(SignalHandler.class)
                || !(method.getReturnType().isType(void.class) || method.getReturnType().isType(Void.class))) {
                continue;
            }
            List<ParameterSource<JavaClassSource>> params = method.getParameters();
            if (params.size() != 1) {
                continue;
            }
            Type<JavaClassSource> paramType = params.get(0).getType();
            if (!paramType.isType(SignalContext.class) || !paramType.isParameterized()) {
                continue;
            }
            Type<JavaClassSource> packetType = paramType.getTypeArguments().get(0);
            if (amount++ != 0) {
                containerBuilder.append(",");
            }
            containerBuilder.append("\n\t\tnew SignalReceiver<>(").append(packetType.getQualifiedName()).append(".class, this::")
                .append(method.getName()).append(", ")
                .append(Boolean.parseBoolean(method.getAnnotation(SignalHandler.class).getLiteralValue())).append(')');
        }
        if (amount == 0) {
            return;
        }

        removeMethod(clazz, "newContainer", IMinestomModule.class);

        importClass(clazz, IMinestomModule.class);
        importClass(clazz, SignalContainer.class);
        importClass(clazz, SignalReceiver.class);

        containerBuilder.append('\n').append("""
                });
            }
            """);
        clazz.addMethod(containerBuilder.toString());
        containerBuilder = null;
    }

}
