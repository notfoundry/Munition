package pw.stamina.munition.feature.simple;

import pw.stamina.munition.core.versioning.Version;
import pw.stamina.munition.feature.core.Feature;
import pw.stamina.munition.feature.core.metadata.FeatureMetadata;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Mark Johnson
 */
public abstract class SimpleFeature implements Feature {

    private final String label;

    private final String bundle;

    private final Version version;

    private final FeatureMetadata metadata;

    protected SimpleFeature(final String label, final String bundle, final Version version, final FeatureMetadata metadata) {
        this.label = label;
        this.bundle = bundle;
        this.version = version;
        this.metadata = metadata;
    }

    protected SimpleFeature(final String label, final String bundle, final Version version) {
        this(label, bundle, version, null);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getBundle() {
        return bundle;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public Optional<FeatureMetadata> findMetadata() {
        return Optional.ofNullable(metadata);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleFeature that = (SimpleFeature) o;
        return Objects.equals(label, that.label) &&
                Objects.equals(bundle, that.bundle) &&
                Objects.equals(version, that.version) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, bundle, version, metadata);
    }

    @Override
    public String toString() {
        return "SimpleFeature{" +
                "label='" + label + '\'' +
                ", bundle='" + bundle + '\'' +
                ", version=" + version +
                ", metadata=" + metadata +
                '}';
    }
}
