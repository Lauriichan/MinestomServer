package me.lauriichan.minecraft.minestom.server.module;

import java.io.File;
import java.nio.file.Path;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.module.IModuleDescription.Dependency;
import me.lauriichan.minecraft.minestom.server.module.IModuleDescription.Version;
import me.lauriichan.minecraft.minestom.server.util.Triple;

final class DependencyGraph {

    private static class Node {

        private final Triple<File, Path, IModuleDescription> entry;
        
        private final ObjectArrayList<Dependency> unsatisfied = new ObjectArrayList<>();
        private final ObjectArrayList<Dependency> versionMismatch = new ObjectArrayList<>();

        Node(Triple<File, Path, IModuleDescription> entry) {
            this.entry = entry;
        }

        public String fileName() {
            if (entry.one() == null) {
                return null;
            }
            return entry.one().getName();
        }
        
        public String id() {
            return entry.three().id();
        }
        
        public ObjectList<Dependency> dependencies() {
            return entry.three().dependencies();
        }

        public Version version() {
            return entry.three().version();
        }

    }

    private final Object2ObjectArrayMap<String, Node> nodes = new Object2ObjectArrayMap<>();
    
    private final ObjectArrayList<Node> loaded = new ObjectArrayList<>();

    private final ObjectArrayList<Node> duplicate = new ObjectArrayList<>();
    private final ObjectArrayList<Node> cyclic = new ObjectArrayList<>();
    private final ObjectArrayList<Node> unsatisfied = new ObjectArrayList<>();
    private final ObjectArrayList<Node> dontLoad = new ObjectArrayList<>();

    private final ObjectArrayList<Node> sorted = new ObjectArrayList<>();
    private final ObjectArrayList<Node> visited = new ObjectArrayList<>();
    private final ObjectArraySet<String> notFound = new ObjectArraySet<>();

    private boolean isCyclic = false;

    public DependencyGraph(final ObjectList<Triple<File, Path, IModuleDescription>> list) {
        for (final Triple<File, Path, IModuleDescription> entry : list) {
            final Node node = new Node(entry);
            if (nodes.containsKey(node.id())) {
                duplicate.add(node);
                continue;
            }
            loaded.add(node);
            nodes.put(node.id(), node);
        }
        sort();
    }

    private void sort() {
        while (true) {
            dontLoad.clear();
            for (final Node node : loaded) {
                visit(node);
            }
            if (dontLoad.isEmpty()) {
                break;
            }
            for (final Node node : dontLoad) {
                loaded.remove(node);
            }
            sorted.clear();
            visited.clear();
        }
    }
    
    private void visit(Node node) {
        if (dontLoad.contains(node)) {
            return;
        }
        if (visited.contains(node)) {
            if (sorted.contains(node)) {
                return;
            }
            isCyclic = true;
            cyclic.add(node);
            dontLoad.add(node);
            return;
        }
        visited.add(node);
        boolean notAllowedToLoad = false;
        for (final Dependency dependency : node.dependencies()) {
            Node depNode = nodes.get(dependency.id());
            if (depNode == null || !loaded.contains(depNode)) {
                if (!dependency.required()) {
                    continue;
                }
                notAllowedToLoad = true;
                if (depNode == null) {
                    notFound.add(dependency.id());
                }
                node.unsatisfied.add(dependency);
                continue;
            }
            if (!dependency.isSatisfied(depNode.version())) {
                notAllowedToLoad = true;
                node.versionMismatch.add(dependency);
                continue;
            }
            visit(depNode);
        }
        if (notAllowedToLoad) {
            unsatisfied.add(node);
            dontLoad.add(node);
            return;
        }
        sorted.add(node);
    }

    public boolean isCyclic() {
        return isCyclic;
    }
    
    public ObjectArrayList<Triple<File, Path, IModuleDescription>> sorted() {
        return loaded.stream().map(node -> node.entry).collect(ObjectArrayList.toList());
    }
    
    public void printReport(ISimpleLogger logger) {
        logger.info(" ____| Module dependency resolution report");
        logger.info("/");
        logger.info("| Resolved ({0}):", nodes.size());
        for (Node node : nodes.values()) {
            if (node.fileName() == null) {
                logger.info("| - {0}", node.id());
            } else {
                logger.info("| - {0} ({1})", node.id(), node.fileName());
            }
        }
        logger.info("| Loadable ({0}):", loaded.size());
        for (Node node : loaded) {
            if (node.fileName() == null) {
                logger.info("| - {0}", node.id());
            } else {
                logger.info("| - {0} ({1})", node.id(), node.fileName());
            }
        }
        logger.info("|");
        logger.info("| Cyclic: {0}", isCyclic ? "Yes" : "No");
        if (isCyclic) {
            logger.info("| Cyclics ({0}):");
            for (Node node : cyclic) {
                logger.info("| - {0} ({1})", node.id(), node.fileName());
            }
        }
        logger.info("|");
        logger.info("| Duplicates ({0}):", duplicate.size());
        for (Node node : duplicate) {
            logger.info("| - {0} ({1})", node.id(), node.fileName());
        }
        logger.info("|");
        logger.info("| Unsatisfied ({0}):", unsatisfied.size());
        for (Node node : unsatisfied) {
            logger.info("| - {0} ({1}):", node.id(), node.fileName());
            if (!node.unsatisfied.isEmpty()) {
                logger.info("|   Missing ({0}):", node.unsatisfied.size());
                for (Dependency dependency : node.unsatisfied) {
                    logger.info("|   - {0}@{1}", dependency.id(), dependency.versionString());
                }
            }
            if (!node.versionMismatch.isEmpty()) {
                logger.info("|   Version mismatch ({0}):", node.versionMismatch.size());
                for (Dependency dependency : node.versionMismatch) {
                    logger.info("|   - {0}@{1} (found {2})", dependency.id(), dependency.versionString(), nodes.get(dependency.id()).version());
                }
            }
        }
        logger.info("|");
        logger.info("\\____");
        logger.info("     | Module dependency resolution report");
    }

}
