package me.lauriichan.minecraft.minestom.server.command;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.laylib.reflection.StackTracker;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.command.annotation.Action;
import me.lauriichan.minecraft.minestom.server.command.annotation.Arg;
import me.lauriichan.minecraft.minestom.server.command.annotation.Command;
import me.lauriichan.minecraft.minestom.server.command.annotation.DefaultAction;
import me.lauriichan.minecraft.minestom.server.command.annotation.Permission;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.event.player.PlayerDisconnectEvent;

public final class MinestomCommandManager {

    private static final Pattern CAPITALIZED_WORD = Pattern.compile("[A-Z][^A-Z]*");

    static record ArgumentNode(int order, int argIdx, boolean optional, String id, IArgumentType<?> type, IArgumentMap map) {}

    private final Object2ObjectMap<Class<?>, IArgumentType<?>> types = new Object2ObjectArrayMap<>();

    private final SystemModule module;

    public MinestomCommandManager(SystemModule module) {
        if (module.server().commandManager() != null) {
            throw new UnsupportedOperationException("Only one instance allowed");
        }
        this.module = module;
        module.extension(IArgumentType.class, true).callInstances((mod, ext) -> {
            if (types.containsKey(ext.type())) {
                mod.logger().warning("There is already a argument type for '" + ext.type().getName() + "'");
                return;
            }
            types.put(ext.type(), ext);
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            UUID uuid = event.getPlayer().getUuid();
            for (IMinestomModule mod : module.moduleManager().modules()) {
                mod.actorMap().remove(uuid);
            }
        });
    }

    public void registerCommands() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        CommandManager commandManager = MinecraftServer.getCommandManager();
        module.extension(ICommandExtension.class, true).callInstances((mod, ext) -> {
            try {
                commandManager.register(buildCommand(mod, ext));
            } catch (RuntimeException ise) {
                mod.logger().error("Failed to register commands of extension '{0}'", ext.getClass().getName(), ise);
            }
        });
    }

    private CommandBuilder buildCommand(IMinestomModule module, ICommandExtension extension) {
        Class<?> clazz = extension.getClass();
        if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface() || clazz.isEnum() || clazz.isAnnotation()) {
            throw new IllegalStateException(
                "Command class '" + clazz.getName() + "' is not allowed to be abstract, an interface, an enum or an annotation");
        }
        Command command = clazz.getDeclaredAnnotation(Command.class);
        if (command == null) {
            throw new IllegalStateException("Command class '" + clazz.getName() + "' doesn't have required Command annotation");
        }
        ISimpleLogger logger = module.logger();
        CommandBuilder builder = new CommandBuilder(command.name(), command.aliases());
        builder.setCondition(createConditionFor(clazz));
        commandLoop:
        for (Method method : clazz.getDeclaredMethods()) {
            DefaultAction defaultAction = method.getDeclaredAnnotation(DefaultAction.class);
            Action[] actions = method.getDeclaredAnnotationsByType(Action.class);
            if ((actions == null || actions.length == 0)) {
                continue;
            }
            if (Modifier.isStatic(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())) {
                logger.warning("Couldn't load command '{0}' of command class '{1}' because it is static or abstract", method.getName(),
                    clazz.getName());
                continue;
            }
            ObjectArrayList<ArgumentNode> arguments = new ObjectArrayList<>();
            Parameter[] parameters = method.getParameters();
            for (int index = 0; index < parameters.length; index++) {
                Parameter parameter = parameters[index];
                Arg arg = parameter.getAnnotation(Arg.class);
                String name;
                if (arg != null && arg.name() != null && !arg.name().isBlank()) {
                    name = formatName(arg.name());
                } else {
                    name = parameter.isNamePresent() ? formatName(parameter.getName()) : formatName(parameter.getClass());
                }
                Class<?> type = ClassUtil.toComplexType(parameter.getType());
                IArgumentType<?> argType = types.get(type.isEnum() ? Enum.class : type);
                if (argType == null) {
                    for (Entry<Class<?>, IArgumentType<?>> entry : types.object2ObjectEntrySet()) {
                        if (!type.isAssignableFrom(entry.getKey()) || !(entry.getValue() instanceof ProvidedArgumentType)) {
                            continue;
                        }
                        argType = entry.getValue();
                        break;
                    }
                    if (argType == null) {
                        logger.warning(
                            "Couldn't load command '{0}' of command class '{1}' because parameter (at {2}) type '{3}' has no matching argument type",
                            method.getName(), clazz.getName(), index, type.getName());
                        continue commandLoop;
                    }
                }
                arguments.add(new ArgumentNode(index, arg.index(), arg.optional(), name, argType, ArgumentMapBuilder.of(arg.params())));
            }
            ArgumentNode[] parsedArguments = arguments.stream().sorted((n1, n2) -> {
                int comp = Boolean.compare(n1.optional(), n2.optional());
                if (comp != 0) {
                    return comp;
                }
                comp = Integer.compare(n2.argIdx(), n1.argIdx());
                if (comp != 0) {
                    return comp;
                }
                return Integer.compare(n1.order(), n2.order());
            }).filter(node -> node.type() instanceof ArgumentType).toArray(ArgumentNode[]::new);
            if (defaultAction != null) {
                if (parsedArguments.length > 0) {
                    throw new IllegalStateException("Default executors can only use provided arguments");
                }
                MinestomCommandExecutor executor = new MinestomCommandExecutor(module, arguments, extension,
                    method);
                for (Action action : actions) {
                    CommandBuilder current = find(builder, action.value());
                    if (current.getDefaultExecutor() != null) {
                        throw new IllegalStateException("Path '" + action.value() + "' already has a default action");
                    }
                    current.setDefaultExecutor(executor);
                }
                continue;
            }
            Argument<?>[] commandArguments = new Argument[parsedArguments.length];
            for (int i = 0; i < commandArguments.length; i++) {
                ArgumentNode parsed = parsedArguments[i];
                commandArguments[i] = ((ArgumentType<?, ?>) parsed.type()).create(module, parsed.id(), parsed.map());
                if (parsed.optional()) {
                    commandArguments[i].setDefaultValue(() -> null);
                }
            }
            MinestomCommandExecutor executor = new MinestomCommandExecutor(module, arguments, extension,
                method);
            MinestomCommandCondition condition = createConditionFor(method);
            for (Action action : actions) {
                find(builder, action.value()).addConditionalSyntax(condition, executor, commandArguments);
            }
        }
        return builder;
    }

    private MinestomCommandCondition createConditionFor(AnnotatedElement element) {
        Permission permissionAnnotation = element.getDeclaredAnnotation(Permission.class);
        if (permissionAnnotation != null) {
            String tmp = permissionAnnotation.value().replaceAll("[ ]*", "").toLowerCase();
            if (!tmp.isEmpty()) {
                return new MinestomCommandCondition(module, tmp);
            }
        }
        return null;
    }

    private CommandBuilder find(CommandBuilder root, String path) {
        path = path.replaceAll("[ ]+", " ").trim();
        if (path.isEmpty()) {
            return root;
        }
        String[] parts = path.split(" ");
        CommandBuilder next = root;
        int index = 0;
        CommandBuilder tmp;
        while (index < parts.length) {
            String name = parts[index++];
            tmp = (CommandBuilder) next.getSubcommands().stream().filter(cmd -> cmd.getName().equals(name)).findFirst().orElse(null);
            if (tmp != null) {
                next = tmp;
                continue;
            }
            tmp = new CommandBuilder(name);
            next.addSubcommand(tmp);
            next = tmp;
        }
        return next;
    }

    private String formatName(String name) {
        name = name.replace(' ', '_');
        Matcher matcher = CAPITALIZED_WORD.matcher(name);
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        while (matcher.find()) {
            if (first) {
                first = false;
                if (matcher.start() != 0) {
                    builder.append(name.substring(0, matcher.start())).append('_');
                }
            } else {
                builder.append('_');
            }
            builder.append(matcher.group().toLowerCase());
        }
        return builder.toString();
    }

    private String formatName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        if (name.contains(".")) {
            name = name.split("\\.")[0];
        }
        Matcher matcher = CAPITALIZED_WORD.matcher(name);
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        while (matcher.find()) {
            if (first) {
                first = false;
            } else {
                builder.append('_');
            }
            builder.append(matcher.group().toLowerCase());
        }
        return builder.toString();
    }

}
