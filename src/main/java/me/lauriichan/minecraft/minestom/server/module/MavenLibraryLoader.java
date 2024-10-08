package me.lauriichan.minecraft.minestom.server.module;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.apache.maven.model.Model;
import org.apache.maven.repository.internal.ArtifactDescriptorUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.logger.ISimpleLogger;

final class MavenLibraryLoader {

    private static class TransferListener extends AbstractTransferListener {

        private final ISimpleLogger fallback;
        private volatile ISimpleLogger logger;
        private volatile String name;

        public TransferListener(final ISimpleLogger fallback) {
            this.fallback = fallback;
        }

        public void set(ISimpleLogger logger, String name) {
            this.logger = Objects.requireNonNull(logger);
            this.name = Objects.requireNonNull(name);
        }

        public void reset() {
            this.logger = fallback;
            this.name = null;
        }

        @Override
        public void transferStarted(TransferEvent event) throws TransferCancelledException {
            Thread thread = Thread.currentThread();
            if (thread.getName().startsWith("BasicRepositoryConnector")) {
                thread.setName("MavenConnector");
            }
            logger.debug("[{0}] Downloading {1}", name == null ? SystemModule.ID : name,
                event.getResource().getRepositoryUrl() + event.getResource().getResourceName());
        }
    }

    private static final String CENTRAL = "https://repo.maven.apache.org/maven2";
    private static final String DOWNLOADABLE_SCOPE = "compile";
    private static final String DOWNLOADABLE_TYPE = "jar";

    private final ISimpleLogger logger;

    private final RepositorySystem repository;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    private final TransferListener transferListener;

    public MavenLibraryLoader(ISimpleLogger logger, Model systemModel) {
        this.logger = logger;

        this.repository = new RepositorySystemSupplier().get();
        this.session = MavenRepositorySystemUtils.newSession();

        session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);
        session.setLocalRepositoryManager(repository.newLocalRepositoryManager(session, new LocalRepository("libraries")));

        session.setTransferListener(transferListener = new TransferListener(logger));

        session.setSystemProperties(System.getProperties());
        session.setReadOnly();

        // Load system repositories
        ObjectArrayList<RemoteRepository> repositories = systemModel.getRepositories().stream()
            .map(ArtifactDescriptorUtils::toRemoteRepository).collect(ObjectArrayList.toList());
        if (repositories.stream().noneMatch(repo -> CENTRAL.equals(repo.getUrl()))) {
            repositories.add(new RemoteRepository.Builder("central", "default", CENTRAL).build());
        }
        this.repositories = repository.newResolutionRepositories(session, repositories);
    }

    public LibraryLoader createLoader(IModuleManager manager, ModuleDescription description) {
        logger.debug("[{0}] Searching for libraries to load...", description.name());
        ObjectArrayList<Dependency> dependencies = description.mavenModel().getDependencies().stream()
            .filter(dep -> (dep.getScope() == null || DOWNLOADABLE_SCOPE.equalsIgnoreCase(dep.getScope()))
                && DOWNLOADABLE_TYPE.equals(dep.getType()))
            .filter(dep -> !manager.isMavenArtifactKnown(dep.getGroupId(), dep.getArtifactId()))
            .map(dep -> new Dependency(new DefaultArtifact(dep.getManagementKey() + ":" + dep.getVersion()), null))
            .collect(ObjectArrayList.toList());
        if (dependencies.isEmpty()) {
            logger.debug("[{0}] Couldn't find any libraries to load.", description.name());
            return new LibraryLoader(getClass().getClassLoader());
        }
        logger.info("[{0}] Loading {1} libraries...", description.name(), dependencies.size());

        transferListener.set(logger, description.name());

        // Load module-specific repositories
        ObjectArrayList<RemoteRepository> repositories = description.mavenModel().getRepositories().stream()
            .map(ArtifactDescriptorUtils::toRemoteRepository)
            .filter(repo1 -> this.repositories.stream()
                .noneMatch(repo2 -> repo1.getUrl().equals(repo2.getUrl()) || repo1.getId().equals(repo2.getId())))
            .collect(ObjectArrayList.toList());
        List<RemoteRepository> remoteRepositories = this.repositories;
        if (!repositories.isEmpty()) {
            remoteRepositories = new ObjectArrayList<>(remoteRepositories);
            remoteRepositories.addAll(repository.newResolutionRepositories(session, repositories));
        }

        DependencyResult result;
        try {
            result = repository.resolveDependencies(session,
                new DependencyRequest(new CollectRequest((Dependency) null, dependencies, remoteRepositories), null));
        } catch (DependencyResolutionException exp) {
            throw new IllegalStateException("Failed to resolve libraries", exp);
        }

        transferListener.reset();

        ObjectArrayList<URL> files = new ObjectArrayList<>();
        for (ArtifactResult artifactResult : result.getArtifactResults()) {
            Artifact artifact = artifactResult.getArtifact();
            File file = artifact.getFile();

            URL url;
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException exp) {
                throw new IllegalStateException("Failed to convert artifact file path '" + file.getAbsolutePath() + "' to url.", exp);
            }
            files.add(url);
            logger.info("[{0}] Loaded library {1}.{2}@{3}", description.name(), artifact.getGroupId(), artifact.getArtifactId(),
                artifact.getVersion());
        }
        return new LibraryLoader(files.toArray(URL[]::new), getClass().getClassLoader());
    }

}
