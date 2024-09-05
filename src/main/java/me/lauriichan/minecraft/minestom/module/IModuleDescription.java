package me.lauriichan.minecraft.minestom.module;

import java.util.Optional;

import org.apache.maven.model.Model;

import it.unimi.dsi.fastutil.objects.ObjectList;

public interface IModuleDescription {

    record Version(int major, int minor, int patch) {

        public static final Version ANY = new Version(-1, -1, -1);

        @Override
        public final String toString() {
            return new StringBuilder().append(major).append('.').append(minor).append('.').append(patch).toString();
        }
    }

    record Dependency(String id, String versionString, Version minVersion, Version maxVersion, boolean required) {
        public boolean isSatisfied(Version version) {
            return isMinSatisfied(version) && isMaxSatisfied(version);
        }

        private boolean isMinSatisfied(Version version) {
            if (minVersion.major() == -1 || minVersion.major() < version.major()) {
                return true;
            }
            if (minVersion.major() > version.major()) {
                return false;
            }
            if (minVersion.minor() == -1 || minVersion.minor() < version.minor()) {
                return true;
            }
            if (minVersion.minor() > version.minor()) {
                return false;
            }
            return minVersion.patch() == -1 || minVersion.patch() <= version.patch();
        }

        private boolean isMaxSatisfied(Version version) {
            if (maxVersion.major() == -1 || maxVersion.major() > version.major()) {
                return true;
            }
            if (maxVersion.major() < version.major()) {
                return false;
            }
            if (maxVersion.minor() == -1 || maxVersion.minor() > version.minor()) {
                return true;
            }
            if (maxVersion.minor() < version.minor()) {
                return false;
            }
            return maxVersion.patch() == -1 || maxVersion.patch() >= version.patch();
        }
    }
    
    Model mavenModel();

    String id();

    default String name() {
        return id();
    }

    Version version();

    ObjectList<Dependency> dependencies();

    Optional<Dependency> dependency(String id);

    Dependency systemDependency();

}
