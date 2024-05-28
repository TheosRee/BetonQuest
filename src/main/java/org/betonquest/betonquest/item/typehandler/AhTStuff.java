package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.item.QuestItem;
import org.jetbrains.annotations.Nullable;

public abstract class AhTStuff<T> {
    private final boolean noneAble;

    /**
     * Set value.
     */
    @Nullable
    private T value;

    /**
     * Existence of the value.
     */
    private QuestItem.Existence existence;

    /**
     * @param noneAble if "none" as value should forbid a value
     */
    public AhTStuff(final boolean noneAble) {
        this.noneAble = noneAble;
        this.value = null;
        this.existence = QuestItem.Existence.WHATEVER;
    }

    @Nullable
    public T get() {
        return value;
    }

    public void set(final String value) {
        if (noneAble && "none".equalsIgnoreCase(value)) {
            this.existence = QuestItem.Existence.FORBIDDEN;
        } else {
            this.value = convertStringToValue(value);
            this.existence = QuestItem.Existence.REQUIRED;
        }
    }

    protected abstract T convertStringToValue(final String value);

    public boolean check(@Nullable final T value) {
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED -> value != null && value.equals(this.value);
            case FORBIDDEN -> value == null;
        };
    }
}
