package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Event to add class points to a player.
 */
public class MMOCoreClassPointsEvent implements Event {

    /**
     * Amount to grant.
     */
    private final VariableNumber amountVar;

    /**
     * Create a new class point add event.
     *
     * @param amount the amount to grant
     */
    public MMOCoreClassPointsEvent(final VariableNumber amount) {
        amountVar = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = amountVar.getValue(profile).intValue();
        data.giveClassPoints(amount);
    }
}
