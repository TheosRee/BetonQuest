package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.versioning.Version;

import java.io.IOException;

/**
 * A version which can also place it inside a Quest.
 *
 * @param <T> the type of Stuff to version
 */
public abstract class SettableVersion<T> extends Version {
    /**
     * Creates a new Version.
     *
     * @param versionString The raw version string
     */
    public SettableVersion(final String versionString) {
        super(versionString);
    }

    /**
     * Sets this version.
     *
     * @param stuff the stuff to put the version in
     * @param path  the path to set the version at
     * @throws IOException when the version cannot be set
     */
    public abstract void setVersion(T stuff, String path) throws IOException;
}
