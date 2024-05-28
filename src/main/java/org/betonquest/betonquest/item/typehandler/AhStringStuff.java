package org.betonquest.betonquest.item.typehandler;

/**
 * {@link AhTStuff} which stores {@link String}s.
 */
public class AhStringStuff extends AhTStuff<String> {
    /**
     * Construct a {@link AhTStuff} which stores a {@link String}.
     *
     * @param noneAble if "none" as value should forbid a value
     */
    public AhStringStuff(final boolean noneAble) {
        super(noneAble);
    }

    @Override
    protected String convertStringToValue(final String value) {
        return value;
    }
}
