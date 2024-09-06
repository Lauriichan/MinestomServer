//package me.lauriichan.minecraft.minestom.maven.transformer;
//
//import static me.lauriichan.maven.sourcemod.api.SourceTransformerUtils.*;
//
//import java.util.List;
//
//import org.jboss.forge.roaster.model.Type;
//import org.jboss.forge.roaster.model.source.AnnotationSource;
//import org.jboss.forge.roaster.model.source.JavaClassSource;
//import org.jboss.forge.roaster.model.source.JavaSource;
//import org.jboss.forge.roaster.model.source.MethodSource;
//import org.jboss.forge.roaster.model.source.ParameterSource;
//
//import me.lauriichan.maven.sourcemod.api.ISourceTransformer;
//import me.lauriichan.minecraft.minestom.server.command.Command;
//import me.lauriichan.minecraft.minestom.server.command.ICommandExtension;
//import me.lauriichan.minecraft.minestom.server.command.SimpleCommand;
//import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
//import net.minestom.server.command.CommandManager;
//
//public class CommandTransformer implements ISourceTransformer {
//
//    @Override
//    public boolean canTransform(JavaSource<?> source) {
//        if (!(source instanceof final JavaClassSource classSource)) {
//            return false;
//        }
//        return !classSource.isAbstract() && !classSource.isRecord() && classSource.hasInterface(ICommandExtension.class);
//    }
//
//    @Override
//    public void transform(JavaSource<?> source) {
//        final JavaClassSource clazz = (JavaClassSource) source;
//
//        StringBuilder containerBuilder = new StringBuilder("""
//            @Override
//            public final void registerCommands(IMinestomModule module, CommandManager commandManager) {
//                SimpleCommand command;
//            """);
//        int amount = 0;
//        for (final MethodSource<JavaClassSource> method : clazz.getMethods()) {
//            if (!method.hasAnnotation(Command.class) || !(method.getReturnType().isType(void.class)
//                || method.getReturnType().isType(Void.class))) {
//                continue;
//            }
//            List<ParameterSource<JavaClassSource>> params = method.getParameters();
//            if (params.size() != 1) {
//                continue;
//            }
//            Type<JavaClassSource> paramType = params.get(0).getType();
//            if (!paramType.isType(SimpleCommand.class)) {
//                continue;
//            }
//            amount++;
//            AnnotationSource<JavaClassSource> annotation = method.getAnnotation(Command.class);
//            containerBuilder.append("\n\t").append(method.getName()).append("(command = new SimpleCommand(module, ");
//            containerBuilder.append(annotation.getLiteralValue("name"));
//            String[] array = annotation.getStringArrayValue("aliases");
//            if (array != null && array.length != 0) {
//                for (String alias : array) {
//                    containerBuilder.append(", \"").append(alias).append('"');
//                }
//            }
//            containerBuilder.append("));").append("\n\tcommand.register(commandManager);");
//        }
//        if (amount == 0) {
//            return;
//        }
//
//        removeMethod(clazz, "registerCommands", IMinestomModule.class, CommandManager.class);
//
//        importClass(clazz, IMinestomModule.class);
//        importClass(clazz, CommandManager.class);
//
//        containerBuilder.append('\n').append("""
//                });
//            }
//            """);
//        clazz.addMethod(containerBuilder.toString());
//        containerBuilder = null;
//    }
//
//}
