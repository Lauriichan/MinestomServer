package me.lauriichan.minecraft.minestom.server.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.JsonArray;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.json.io.JsonParser;
import me.lauriichan.laylib.json.io.JsonSyntaxException;
import me.lauriichan.minecraft.minestom.server.module.IModuleDescription.Dependency;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.server.resource.source.PathDataSource;

public final class ModuleDescription implements IModuleDescription {

    public static class ModuleDescriptionException extends ModuleException {

        private static final long serialVersionUID = 4693745933534202618L;

        public ModuleDescriptionException(final String message) {
            super(message);
        }

        public ModuleDescriptionException(final String message, final Throwable cause) {
            super(message, cause);
        }

    }

    static final MavenXpp3Reader MAVEN_READER = new MavenXpp3Reader();

    private final Model mavenModel;
    private final String id;

    private final String main;

    private final Version version;

    private final ObjectList<Dependency> dependencies;

    ModuleDescription(final Path jarRoot) throws ModuleDescriptionException {
        final IDataSource moduleJsonSource = new PathDataSource(jarRoot.resolve("module.json"));
        if (!moduleJsonSource.isReadable()) {
            throw new ModuleDescriptionException("Data source is not readable");
        }
        JsonObject object;
        try (BufferedReader reader = moduleJsonSource.openReader()) {
            final IJson<?> json = JsonParser.fromReader(reader);
            if (!json.isObject()) {
                throw new ModuleDescriptionException("Module description has to be a json object, found: " + json.type());
            }
            object = json.asJsonObject();
        } catch (IllegalStateException | IOException | JsonSyntaxException e) {
            throw new ModuleDescriptionException("Failed to parse module description", e);
        }
        final String groupId = expectNonEmpty(object, "groupId");
        final String artifactId = expectNonEmpty(object, "artifactId");
        this.id = artifactId.toLowerCase();
        final IDataSource pomXmlSource = new PathDataSource(jarRoot.resolve("META-INF/maven/%s/%s/pom.xml".formatted(groupId, artifactId)));
        if (!pomXmlSource.isReadable()) {
            mavenModel = new Model();
            mavenModel.setGroupId(groupId);
            mavenModel.setArtifactId(artifactId);
            mavenModel.setVersion(expectNonEmpty(object, "version"));
        } else {
            try (BufferedReader reader = pomXmlSource.openReader()) {
                mavenModel = MAVEN_READER.read(reader);
            } catch (IllegalStateException | IOException | XmlPullParserException e) {
                throw new ModuleDescriptionException("Failed to parse pom.xml", e);
            }
        }
        this.version = parseVersion(mavenModel.getVersion());
        this.main = expectNonEmpty(object, "main");
        final ObjectArrayList<Dependency> dependencies = new ObjectArrayList<>();
        final JsonArray array = object.getAsArray("dependencies");
        if (array == null) {
            throw new ModuleDescriptionException("Expected module description to contain an 'dependencies' array");
        }
        for (int i = 0; i < array.size(); i++) {
            final IJson<?> element = array.get(i);
            if (!element.isObject()) {
                throw new ModuleDescriptionException("Invalid dependency entry (" + i + ")");
            }
            object = element.asJsonObject();
            final String depId = expectNonEmpty(object, "id").toLowerCase();
            if (dependencies.stream().anyMatch(dep -> dep.id().equals(depId))) {
                throw new ModuleDescriptionException("Duplicated dependency entry (" + i + ") for '" + depId + "'");
            }
            final String version = expectNonEmpty(object, "version");
            final int rangeIdx = version.indexOf('-');
            if (rangeIdx + 1 == version.length()) {
                throw new ModuleDescriptionException("Invalid dependency version range string '" + version + "'");
            }
            Version min, max;
            if (rangeIdx == -1) {
                min = parseDependencyVersion(version);
                max = Version.ANY;
            } else {
                min = parseDependencyVersion(version.substring(0, rangeIdx));
                max = parseDependencyVersion(version.substring(rangeIdx + 1));
            }
            dependencies.add(new Dependency(depId, version, min, max, !object.getAsBoolean("optional")));
        }
        if (dependencies.stream().noneMatch(dep -> SystemModule.ID.equals(dep.id()))) {
            throw new ModuleDescriptionException("System dependency is missing");
        }
        dependencies.sort(Comparator.comparing(Dependency::required));
        this.dependencies = ObjectLists.unmodifiable(dependencies);
    }

    /*
     * Getter
     */

    @Override
    public Model mavenModel() {
        return mavenModel;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        final String name = mavenModel.getName();
        if (name == null || name.isBlank()) {
            return mavenModel.getArtifactId();
        }
        return name;
    }

    public String main() {
        return main;
    }

    @Override
    public Version version() {
        return version;
    }

    @Override
    public ObjectList<Dependency> dependencies() {
        return dependencies;
    }

    @Override
    public Optional<Dependency> dependency(final String id) {
        return dependencies.stream().filter(dep -> dep.id().equals(id)).findFirst();
    }

    @Override
    public Dependency systemDependency() {
        return dependency(SystemModule.ID).get();
    }

    /*
     * Parse helpers
     */

    public static Version parseVersion(final String string) throws ModuleDescriptionException {
        final String[] parts = string.split("\\.");
        if (parts.length < 2 || parts.length > 3) {
            throw new ModuleDescriptionException("Invalid version string: '" + string + "'");
        }
        final int major = parseVersionComponent("major", parts[0]);
        final int minor = parseVersionComponent("minor", parts[1]);
        int patch = 0;
        if (parts.length == 3) {
            patch = parseVersionComponent("patch", parts[2]);
        }
        return new Version(major, minor, patch);
    }

    public static Version parseDependencyVersion(final String string) throws ModuleDescriptionException {
        final String[] parts = string.split("\\.");
        if (parts.length > 3) {
            throw new ModuleDescriptionException("Invalid version string: '" + string + "'");
        }
        final int major = parseDependencyVersionComponent("major", parts[0]);
        if (major == -1) {
            return Version.ANY;
        }
        int minor = -1;
        if (parts.length >= 2) {
            minor = parseDependencyVersionComponent("minor", parts[1]);
        }
        int patch = -1;
        if (parts.length == 3) {
            patch = parseDependencyVersionComponent("patch", parts[2]);
        }
        return new Version(major, minor, patch);
    }

    private static int parseVersionComponent(final String name, final String component) throws ModuleDescriptionException {
        try {
            final int value = Integer.parseInt(component);
            if (value < 0) {
                throw new ModuleDescriptionException("Invalid " + name + " version component '" + value + "', must be zero or positive");
            }
            return value;
        } catch (final NumberFormatException nfe) {
            throw new ModuleDescriptionException("Failed to parse " + name + " version component: '" + component + "'", nfe);
        }
    }

    private static int parseDependencyVersionComponent(final String name, final String component) throws ModuleDescriptionException {
        if ("*".equals(component)) {
            return -1;
        }
        try {
            final int value = Integer.parseInt(component);
            if (value < 0) {
                throw new ModuleDescriptionException("Invalid " + name + " version component '" + value + "', must be zero or positive");
            }
            return value;
        } catch (final NumberFormatException nfe) {
            throw new ModuleDescriptionException("Failed to parse " + name + " version component: '" + component + "'", nfe);
        }
    }

    private static String expectNonEmpty(final JsonObject object, final String key) throws ModuleDescriptionException {
        final String string = object.getAsString(key, null);
        if (string == null || string.isBlank()) {
            throw new ModuleDescriptionException("Expected non-empty key '" + key + "' in description");
        }
        return string;
    }

}