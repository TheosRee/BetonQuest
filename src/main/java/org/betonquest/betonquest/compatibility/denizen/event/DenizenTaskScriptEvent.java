package org.betonquest.betonquest.compatibility.denizen.event;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.core.TaskScriptContainer;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * Runs specified Denizen task script.
 */
public class DenizenTaskScriptEvent implements PlayerEvent {
    /**
     * The {@link VariableString} containing the name of the script to run.
     */
    private final VariableString nameVar;

    /**
     * Create a new Denizen Task Script Event.
     *
     * @param nameVar the {@link VariableString} containing the name of the script to run.
     */
    public DenizenTaskScriptEvent(final VariableString nameVar) {
        this.nameVar = nameVar;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final String name = nameVar.getValue(profile);
        final TaskScriptContainer script = ScriptRegistry.getScriptContainerAs(name, TaskScriptContainer.class);
        if (script == null) {
            throw new QuestException("Could not find '" + name + "' Denizen script");
        }
        final BukkitScriptEntryData data = new BukkitScriptEntryData(PlayerTag.mirrorBukkitPlayer(profile.getPlayer()), null);
        script.run(data, null);
    }
}
