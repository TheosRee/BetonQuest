package org.betonquest.betonquest.compatibility.mythicmobs.action;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Casts a skill as a player.
 */
public class MythicCastSkillAction implements OnlineAction {

    /**
     * Logger instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Source pack for logging.
     */
    private final QuestPackage pack;

    /**
     * The BukkitAPIHelper used to cast the skill.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * Name of the skill.
     */
    private final Argument<String> skillName;

    /**
     * Metadata for skill casting.
     */
    private final Argument<List<Map.Entry<String, String>>> metadata;

    /**
     * Constructs a new MythicCastSkillAction.
     *
     * @param log       logs when the skill could not be cast
     * @param pack      the source pack used as log source
     * @param apiHelper the BukkitAPIHelper to cast the skill
     * @param skillName the name of the skill
     * @param metadata  the metadata for skill casting
     */
    public MythicCastSkillAction(final BetonQuestLogger log, final QuestPackage pack, final BukkitAPIHelper apiHelper,
                                 final Argument<String> skillName, final Argument<List<Map.Entry<String, String>>> metadata) {
        this.log = log;
        this.pack = pack;
        this.apiHelper = apiHelper;
        this.skillName = skillName;
        this.metadata = metadata;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final String name = skillName.getValue(profile);
        final List<Map.Entry<String, String>> metadata = this.metadata.getValue(profile);
        final Consumer<SkillMetadata> consumer = skillMetadata -> metadata.forEach(entry
                -> skillMetadata.setMetadata(entry.getKey(), entry.getValue()));
        if (!apiHelper.castSkill(profile.getPlayer(), name, consumer)) {
            log.debug(pack, "Could not cast skill '" + name + "' for profile " + profile);
        }
    }
}
