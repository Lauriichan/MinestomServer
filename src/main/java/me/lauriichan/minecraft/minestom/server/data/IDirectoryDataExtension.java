package me.lauriichan.minecraft.minestom.server.data;

import java.io.File;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.data.IDataHandler.Wrapper;
import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;

@ExtensionPoint
public interface IDirectoryDataExtension<T> extends IDataExtension<T> {

    public static final class FileData<T> extends Wrapper<T> {

        private final File file;
        private final String name;
        private volatile boolean delete = false;

        public FileData(final File file, final String name) {
            this.file = file;
            this.name = name;
        }

        public File file() {
            return file;
        }

        public String name() {
            return name;
        }

        public void delete() {
            delete = true;
        }

        public boolean shouldBeDeleted() {
            return delete;
        }

    }

    String path();

    default ObjectSet<String> newData() {
        return ObjectSets.emptySet();
    }

    default void clearNewData() {}

    default boolean isSupported(final File file, final String name, final String extension, final boolean isFile) {
        return true;
    }

    default boolean saveNewDataAfterLoad() {
        return false;
    }

    default boolean removeNewDataOnReload() {
        return true;
    }

    default boolean saveKnownFiles() {
        return true;
    }

    default void onLoadStart(final ISimpleLogger logger) {}

    default void onLoad(final ISimpleLogger logger, final FileData<T> value) throws Exception {}

    default void onLoadEnd(final ISimpleLogger logger) {}

    default void onDeleteDone(final ISimpleLogger logger, final DirectoryDataWrapper<?, ?> wrapper) {}

    default void onDeleted(final ISimpleLogger logger, final String name) {}

    default void onSaveStart(final ISimpleLogger logger) {}

    default void onSave(final ISimpleLogger logger, final FileData<T> value) throws Exception {}

    default void onSaveEnd(final ISimpleLogger logger) {}

}
