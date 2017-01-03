package pw.stamina.munition.feature.simple;

import pw.stamina.munition.core.versioning.Version;
import pw.stamina.munition.feature.core.Plugin;
import pw.stamina.munition.feature.core.dependency.Dependency;
import pw.stamina.munition.feature.core.metadata.FeatureMetadata;

import java.util.*;

/**
 * @author Mark Johnson
 */
public abstract class SimplePlugin extends SimpleFeature implements Plugin {

    private final Set<Dependency<?>> dependencies;

    protected SimplePlugin(final String label, final String bundle, final Version version, final FeatureMetadata metadata, final List<Dependency<?>> dependencies) {
        super(label, bundle, version, metadata);
        this.dependencies = new HashSet<>(dependencies);
    }

    protected SimplePlugin(final String label, final String bundle, final Version version, final List<Dependency<?>> dependencies) {
        this(label, bundle, version, null, dependencies);
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimplePlugin that = (SimplePlugin) o;
        return Objects.equals(getLabel(), that.getLabel()) &&
                Objects.equals(getBundle(), that.getBundle()) &&
                Objects.equals(getVersion(), that.getVersion()) &&
                Objects.equals(findMetadata(), that.findMetadata()) &&
                Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dependencies);
    }

    @Override
    public String toString() {
        return "SimpleFeature{" +
                "label='" + getLabel() + '\'' +
                ", version=" + getVersion() +
                ", bundle='" + getBundle() + '\'' +
                ", metadata=" + findMetadata() +
                ", dependencies=" + dependencies +
                '}';
    }

}
