package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

/**
 * Holds and checks {@link Color} on a QuestItem.
 */
public class ColorHandler extends AhDefaultTStuff<Color> {
    /**
     * Construct a new ColorHandler with Default Leather Color.
     */
    public ColorHandler() {
        super(true, Bukkit.getServer().getItemFactory().getDefaultLeatherColor());
    }

    @Override
    protected Color convertStringToValue(final String value) throws InstructionParseException {
        return Utils.getColor(value);
    }

    @Override
    public boolean check(@Nullable final Color color) {
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED, FORBIDDEN -> // if it's forbidden, this.color is default leather color (undyed)
                    get().equals(color);
        };
    }
}
