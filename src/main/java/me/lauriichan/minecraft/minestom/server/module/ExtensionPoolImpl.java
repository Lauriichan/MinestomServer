package me.lauriichan.minecraft.minestom.server.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.minecraft.minestom.server.extension.ExtensionCondition;
import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IConditionMap;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.extension.IExtensionPool;
import me.lauriichan.minecraft.minestom.server.extension.processor.ExtensionProcessor;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;

final class ExtensionPoolImpl<T extends IExtension> implements IExtensionPool<T> {

    public static boolean isExtendable(final Class<?> type) {
        return ClassUtil.getAnnotation(type, ExtensionPoint.class) != null;
    }

    static final class ConditionMapImpl implements IConditionMap {

        private final Object2BooleanOpenHashMap<String> map = new Object2BooleanOpenHashMap<>();
        private volatile boolean locked = false;

        public ConditionMapImpl() {
            map.defaultReturnValue(false);
        }

        @Override
        public boolean value(final String property) {
            return map.getBoolean(property);
        }

        @Override
        public void value(final String property, final boolean value) {
            if (locked) {
                return;
            }
            map.put(property, value);
        }

        @Override
        public boolean set(final String property) {
            return map.containsKey(property);
        }

        @Override
        public void unset(final String property) {
            if (locked) {
                return;
            }
            map.removeBoolean(property);
        }

        @Override
        public boolean locked() {
            return locked;
        }

        void lock() {
            locked = true;
        }

    }

    private final Class<T> type;
    private final boolean instantiated;
    private final int count;
    private final Object2ObjectMap<IMinestomModule, ObjectList<T>> extensions;
    private final Object2ObjectMap<IMinestomModule, ObjectList<Class<? extends T>>> extensionClasses;

    ExtensionPoolImpl(final IMinestomModule module, final Class<T> type, final boolean instantiate) {
        this(module, type, type, instantiate);
    }

    ExtensionPoolImpl(final IMinestomModule owningModule, final Class<? extends IExtension> extensionType, final Class<T> type,
        final boolean instantiate) {
        Objects.requireNonNull(owningModule, "Module can not be null!");
        this.instantiated = instantiate;
        this.type = Objects.requireNonNull(type, "Extension type can not be null!");
        final String typeName = extensionType.getName();
        if (!isExtendable(extensionType)) {
            throw new IllegalArgumentException("The class '" + typeName + "' is not extendable!");
        }
        if (!extensionType.isAssignableFrom(type)) {
            throw new IllegalArgumentException("The class '" + type.getName() + "' can not be casted to '" + typeName + "'");
        }
        final ISimpleLogger owningLogger = owningModule.logger();
        owningLogger.debug("Processing extension '{0}'", typeName);
        ObjectList<IMinestomModule> modules = owningModule.moduleManager().modules();
        Object2ObjectArrayMap<IMinestomModule, ObjectList<T>> extensions = new Object2ObjectArrayMap<>();
        Object2ObjectArrayMap<IMinestomModule, ObjectList<Class<? extends T>>> extensionClasses = new Object2ObjectArrayMap<>();
        int count = 0;
        for (IMinestomModule module : modules) {
            final ISimpleLogger logger = module.logger();
            final IDataSource source = module.resource("jar://" + ExtensionProcessor.extensionPath(typeName));
            if (!source.exists() || !source.isReadable()) {
                continue;
            }
            ObjectArrayList<T> extensionList = null;
            ObjectArrayList<Class<? extends T>> extensionClassList = null;
            try (BufferedReader reader = source.openReader()) {
                if (instantiate) {
                    extensionList = new ObjectArrayList<>();
                }
                extensionClassList = new ObjectArrayList<>();
                String line;
                readLoop:
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        break;
                    }
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(line, true, module.classLoader());
                    } catch(ClassNotFoundException | LinkageError ignore) {
                    }
                    if (clazz == null) {
                        logger.debug("Couldn't find classs '{0}'", line);
                        continue;
                    }
                    if (!type.isAssignableFrom(clazz)) {
                        logger.debug("Class '{0}' is not assignable from '{1}'", clazz.getName(), typeName);
                        continue;
                    }
                    final Class<? extends T> extensionClazz = clazz.asSubclass(type);
                    if (owningModule.conditionMap() != null) {
                        final IConditionMap map = owningModule.conditionMap();
                        final ExtensionCondition[] conditions = ClassUtil.getAnnotations(extensionClazz, ExtensionCondition.class);
                        for (final ExtensionCondition condition : conditions) {
                            if (map.set(condition.name()) && map.value(condition.name()) != condition.condition()
                                || !map.set(condition.name()) && !condition.activeByDefault()) {
                                logger.debug(
                                    "Extension implementation '{0}' for extension '{1}' is disabled because condition '{2}' is not set to '{3}'",
                                    extensionClazz.getName(), typeName, condition.name(), condition.condition());
                                continue readLoop;
                            }
                        }
                    }
                    if (extensionList == null) {
                        logger.debug("Found extension '{0}'", extensionClazz.getName());
                        extensionClassList.add(extensionClazz);
                        continue;
                    }
                    T extension = null;
                    try {
                        extension = owningModule.sharedExtensions().get(extensionClazz);
                    } catch (Throwable exp) {
                        logger.debug("Failed to load instance '{0}' for extension '{1}'", exp, extensionClazz.getName(), typeName);
                        continue;
                    }
                    if (extension == null) {
                        logger.debug("Failed to load instance '{0}' for extension '{1}'", extensionClazz.getName(), typeName);
                        continue;
                    }
                    logger.debug("Found extension '{0}'", extensionClazz.getName());
                    extensionList.add(extension);
                    extensionClassList.add(extensionClazz);
                }
            } catch (final IOException exp) {
                logger.debug("Couldn't load instances for extension '{0}'", typeName);
            }
            if (extensionClassList == null || (extensionList == null && instantiate)) {
                continue;
            }
            if (instantiate) {
                extensions.put(module, ObjectLists.unmodifiable(extensionList));
            }
            extensionClasses.put(module, ObjectLists.unmodifiable(extensionClassList));
            count += extensionClassList.size();
            logger.debug("Found {1} extension(s) for '{0}'", typeName, extensionClassList.size());
        }
        this.count = count;
        this.extensions = extensions.isEmpty() ? Object2ObjectMaps.emptyMap() : Object2ObjectMaps.unmodifiable(extensions);
        this.extensionClasses = extensionClasses.isEmpty() ? Object2ObjectMaps.emptyMap() : Object2ObjectMaps.unmodifiable(extensionClasses);
        owningLogger.debug("Found {1} total extension(s) for '{0}' in all modules", typeName, count);
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public Object2ObjectMap<IMinestomModule, ObjectList<T>> extensions() {
        return extensions;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public boolean instantiated() {
        return instantiated;
    }

    @Override
    public Object2ObjectMap<IMinestomModule, ObjectList<Class<? extends T>>> extensionClasses() {
        return extensionClasses;
    }

}
