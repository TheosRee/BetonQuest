package org.betonquest.betonquest.compatibility.mythicmobs.action;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Factory to create {@link MythicCastSkillAction}s from {@link Instruction}s.
 */
public class MythicCastSkillActionFactory implements PlayerActionFactory {

    /**
     * How many splits the metadata part must have.
     */
    private static final int METADATA_SPLIT_COUNT = 1;

    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The BukkitAPIHelper used to cast the skill.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * Create a new Factory.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param apiHelper     the BukkitAPIHelper to cast the skill
     */
    public MythicCastSkillActionFactory(final BetonQuestLoggerFactory loggerFactory, final BukkitAPIHelper apiHelper) {
        this.loggerFactory = loggerFactory;
        this.apiHelper = apiHelper;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> skillName = instruction.string().get();
        final BetonQuestLogger log = loggerFactory.create(MythicCastSkillAction.class);
        final Argument<List<Map.Entry<String, String>>> metadata = instruction.parse(string -> {
            final String[] split = string.split(":", METADATA_SPLIT_COUNT);
            if (split.length == METADATA_SPLIT_COUNT) {
                throw new QuestException("Invalid metadata part: " + string);
            }
            return Map.entry(split[0], split[1]);
        }).list().get("metadata", Collections.emptyList());
        return new OnlineActionAdapter(new MythicCastSkillAction(log, instruction.getPackage(), apiHelper, skillName, metadata));
    }
}
