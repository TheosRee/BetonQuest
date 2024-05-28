package org.betonquest.betonquest.item.typehandler;

import org.jetbrains.annotations.NotNull;

public abstract class AhDefaultTStuff<T> extends AhTStuff<T> {
    private final T defaultValue;

    /**
     * @param noneAble     if "none" as value should forbid a value
     * @param defaultValue the fallback value if none is set
     */
    public AhDefaultTStuff(final boolean noneAble, final T defaultValue) {
        super(noneAble);
        this.defaultValue = defaultValue;
    }

    @NotNull
    @Override
    public T get() {
        final T superValue = super.get();
        return superValue == null ? defaultValue : superValue;
    }
}
