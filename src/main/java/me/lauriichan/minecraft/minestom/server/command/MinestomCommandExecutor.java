package me.lauriichan.minecraft.minestom.server.command;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.laylib.localization.Key;
import me.lauriichan.laylib.reflection.JavaLookup;
import me.lauriichan.minecraft.minestom.server.command.MinestomCommandManager.ArgumentNode;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.translation.MinestomTranslation;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;

final class MinestomCommandExecutor implements CommandExecutor {

    private static record Provided(ProvidedArgumentType<?> type, IArgumentMap map) {}

    private final IMinestomModule module;

    private final Object2ObjectMap<String, Provided> providedMap;
    private final ObjectList<String> argumentIds;

    private final ICommandExtension instance;
    private final MethodHandle handle;

    private final String methodName;

    public MinestomCommandExecutor(final IMinestomModule module, final ObjectList<ArgumentNode> arguments, final ICommandExtension instance,
        final Method method) {
        this.module = module;
        Object2ObjectArrayMap<String, Provided> providedMap = new Object2ObjectArrayMap<>();
        arguments.stream().filter(node -> node.type() instanceof ProvidedArgumentType)
            .forEach(node -> providedMap.put(node.id(), new Provided((ProvidedArgumentType<?>) node.type(), node.map())));
        this.providedMap = providedMap.isEmpty() ? Object2ObjectMaps.emptyMap() : Object2ObjectMaps.unmodifiable(providedMap);
        this.argumentIds = ObjectLists.unmodifiable(arguments.stream().sorted((n1, n2) -> Integer.compare(n1.order(), n2.order()))
            .map(ArgumentNode::id).collect(ObjectArrayList.toList()));
        this.instance = instance;
        this.handle = JavaLookup.PLATFORM.unreflect(method);
        this.methodName = method.getName();
    }

    @Override
    public void apply(CommandSender sender, CommandContext context) {
        Object[] argumentList = new Object[argumentIds.size() + 1];
        argumentList[0] = instance;
        Actor<?> actor = module.actorMap().actor(sender);
        for (int i = 1; i < argumentList.length; i++) {
            Object obj = context.get(argumentIds.get(i - 1));
            if (obj == null) {
                Provided provided = providedMap.get(argumentIds.get(i - 1));
                if (provided != null) {
                    argumentList[i] = provided.type().provide(actor, provided.map());
                    continue;
                }
            }
            argumentList[i] = obj;
        }
        try {
            handle.invokeWithArguments(argumentList);
        } catch (Throwable throwable) {
            actor.send(MinestomTranslation.COMMAND_SYSTEM_EXECUTION_FAILED, Key.of("command", context.getCommandName()));
            module.logger().error("Failed to execute command method '{0}' of command class '{1}' with input: <{2}>", methodName,
                instance.getClass().getName(), context.getInput());
        }
    }

}
