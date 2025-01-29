package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de/-serialization of Items from/into QuestItem string format.
 *
 * @param <T> the specific to de/-serialize
 * @param <G> the generic of T
 */
public interface ItemHandler<T, G> {
    /**
     * Gets the class this Handler works on.
     *
     * @return the class for the Handler
     */
    Class<T> clazz();

    /**
     * The keys this handler allows in {@link #set(String, String)} which are used for data identification.
     *
     * @return keys in lower case
     */
    Set<String> keys();

    /**
     * Converts the meta into QuestItem format.
     *
     * @param argument the object to serialize
     * @return parsed values or null
     */
    @Nullable
    String serializeToString(T argument);

    /**
     * Converts the generic into QuestItem format if it is applicable to {@link #clazz()}.
     * When the generic is not applicable it will return null.
     *
     * @param generic the generic to serialize
     * @return parsed values or null
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default String rawSerializeToString(final G generic) {
        if (clazz().isInstance(generic)) {
            return serializeToString((T) generic);
        }
        return null;
    }

    /**
     * Sets the data into the Handler.
     * <p>
     * The data may be the same as the key if it is just a keyword.
     *
     * @param key  the lower case key
     * @param data the associated data
     * @throws QuestException if the data is malformed or key not valid for handler
     */
    void set(String key, String data) throws QuestException;

    /**
     * Reconstitute this Handler data into the specified meta.
     *
     * @param meta the meta to populate
     */
    void populate(T meta);

    /**
     * Reconstitute this Handler data into the specified meta.
     * <p>
     * Defaults to {@link #populate(T)}.
     *
     * @param specific the meta to populate
     * @param profile  the optional profile for customized population
     */
    default void populate(final T specific, @Nullable final Profile profile) {
        populate(specific);
    }

    /**
     * Reconstitute this Handler data into the specified generic if it is applicable to {@link #clazz()}.
     * <p>
     * When the generic is not applicable nothing changes.
     *
     * @param generic the generic to populate
     * @param profile the profile for customized population
     */
    @SuppressWarnings("unchecked")
    default void rawPopulate(final G generic, @Nullable final Profile profile) {
        if (clazz().isInstance(generic)) {
            populate((T) generic, profile);
        }
    }

    /**
     * Check to see if the specified ItemMeta matches the Handler.
     *
     * @param specific the ItemMeta to check
     * @return if the specific satisfies the requirement defined via {@link #set(String, String)}
     */
    boolean check(T specific);

    /**
     * Check to see if the specified ItemMeta matches the Handler if it is applicable to {@link #clazz()}.
     * <p>
     * When the generic is not applicable it will return {@code true}.
     *
     * @param generic the ItemMeta to check
     * @return if the generic satisfies the requirement defined via {@link #set(String, String)}
     */
    @SuppressWarnings("unchecked")
    default boolean rawCheck(final G generic) {
        return !clazz().isInstance(generic) || check((T) generic);
    }
}
