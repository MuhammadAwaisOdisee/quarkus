package io.quarkus.bootstrap.workspace;

import io.quarkus.paths.EmptyPathTree;
import io.quarkus.paths.MultiRootPathTree;
import io.quarkus.paths.PathTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ArtifactSources {

    static ArtifactSources main(SourceDir sources, SourceDir resources) {
        return new DefaultArtifactSources(DefaultWorkspaceModule.MAIN, List.of(sources), List.of(resources));
    }

    static ArtifactSources test(SourceDir sources, SourceDir resources) {
        return new DefaultArtifactSources(DefaultWorkspaceModule.TEST, List.of(sources), List.of(resources));
    }

    String getClassifier();

    Collection<SourceDir> getSourceDirs();

    Collection<SourceDir> getResourceDirs();

    default boolean isOutputAvailable() {
        for (SourceDir src : getSourceDirs()) {
            if (src.isOutputAvailable()) {
                return true;
            }
        }
        for (SourceDir src : getResourceDirs()) {
            if (src.isOutputAvailable()) {
                return true;
            }
        }
        return false;
    }

    default PathTree getOutputTree() {
        final Collection<SourceDir> sourceDirs = getSourceDirs();
        final Collection<SourceDir> resourceDirs = getResourceDirs();
        final List<PathTree> trees = new ArrayList<>(sourceDirs.size() + resourceDirs.size());
        for (SourceDir src : sourceDirs) {
            final PathTree outputTree = src.getOutputTree();
            if (outputTree != null && !outputTree.isEmpty() && !trees.contains(outputTree)) {
                trees.add(outputTree);
            }
        }
        for (SourceDir src : resourceDirs) {
            final PathTree outputTree = src.getOutputTree();
            if (outputTree != null && !outputTree.isEmpty() && !trees.contains(outputTree)) {
                trees.add(outputTree);
            }
        }
        if (trees.isEmpty()) {
            return EmptyPathTree.getInstance();
        }
        if (trees.size() == 1) {
            return trees.get(0);
        }
        return new MultiRootPathTree(trees.toArray(new PathTree[0]));
    }
}
