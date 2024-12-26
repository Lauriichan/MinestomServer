package me.lauriichan.minecraft.minestom.server.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.logger.util.StringUtil;
import me.lauriichan.minecraft.minestom.server.data.IDataHandler.Wrapper;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;

public final class DataMigrator {

    private static final class Migration {
        private final ObjectList<DataMigrationExtension<?, ?>> migrations;
        private final int targetVersion;

        public Migration(final int targetVersion, final ObjectList<DataMigrationExtension<?, ?>> migrations) {
            this.targetVersion = targetVersion;
            this.migrations = migrations;
        }

        public int targetVersion() {
            return targetVersion;
        }

        public ObjectList<DataMigrationExtension<?, ?>> migrations() {
            return migrations;
        }
    }

    private final Object2ObjectArrayMap<Class<? extends IDataExtension<?>>, Migration> migrations = new Object2ObjectArrayMap<>();

    public DataMigrator(final SystemModule systemModule) {
        final Object2ObjectArrayMap<Class<? extends IDataExtension<?>>, ObjectArrayList<DataMigrationExtension<?, ?>>> tmpMigrations = new Object2ObjectArrayMap<>();
        systemModule.extension(DataMigrationExtension.class, true).callInstances((module, extension) -> {
            final Class<?> target = extension.targetType();
            if (target == null) {
                module.logger().warning("Couldn't register migration as it doesn't define a target: {0}", extension.getClass().getName());
                return;
            }
            if (extension.minVersion() < 0 || extension.minVersion() >= extension.targetVersion()) {
                module.logger().warning(
                    "Couldn't register migration as the min and/or target version are invalid: {0} (min: {1}, target: {2})",
                    extension.getClass().getName(), extension.minVersion(), extension.targetVersion());
                return;
            }
            final Class<? extends IDataExtension<?>> configTarget = (Class<? extends IDataExtension<?>>) target
                .asSubclass(IDataExtension.class);
            ObjectArrayList<DataMigrationExtension<?, ?>> migrationList = tmpMigrations.get(configTarget);
            if (migrationList == null) {
                migrationList = new ObjectArrayList<>();
                tmpMigrations.put(configTarget, migrationList);
            }
            migrationList.add(extension);
        });
        if (tmpMigrations.isEmpty()) {
            return;
        }
        tmpMigrations.keySet().forEach(key -> {
            final ObjectList<DataMigrationExtension<?, ?>> extensions = tmpMigrations.get(key);
            extensions.sort((m1, m2) -> {
                final int tmp = Integer.compare(m1.minVersion(), m2.minVersion());
                if (tmp != 0) {
                    return tmp;
                }
                return Integer.compare(m1.targetVersion(), m2.targetVersion());
            });
            migrations.put(key, new Migration(extensions.get(extensions.size() - 1).targetVersion(), extensions));
        });
    }

    public int getTargetVersion(final Class<? extends IDataExtension<?>> extension) {
        final Migration migration = migrations.get(extension);
        return migration == null ? 0 : migration.targetVersion();
    }

    public boolean needsMigration(final Class<? extends IDataExtension<?>> extension, final int version) {
        final Migration migration = migrations.get(extension);
        return migration != null && version < migration.targetVersion();
    }

    public <T, D extends IDataExtension<T>> int migrate(final ISimpleLogger logger, int version, final Wrapper<T> wrapper,
        final D extension) throws DataMigrationFailedException {
        final Migration migration = migrations.get(extension.getClass());
        if (migration == null || version >= migration.targetVersion()) {
            return version;
        }
        for (final DataMigrationExtension<?, ?> migrationExt : migration.migrations()) {
            if (migrationExt.targetVersion() <= version) {
                continue;
            }
            logger.info("Applying migration '{3}' (version {1} to {2}) for data '{0}'", extension.name(), version,
                migrationExt.targetVersion(), migrationExt.description());
            try {
                ((DataMigrationExtension<T, ?>) migrationExt).migrate(wrapper);
                version = migrationExt.targetVersion();
            } catch (final Throwable throwable) {
                throw new DataMigrationFailedException(
                    StringUtil.format("Failed to apply migration '{3}' (version {1} to {2}) for data '{0}'", new Object[] {
                        extension.name(),
                        version,
                        migrationExt.targetVersion(),
                        migrationExt.description()
                    }), throwable);
            }
        }
        return migration.targetVersion();
    }

}
