package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableEnum;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Checks if the player has specified level in an mcMMO skill.
 */
public class McMMOSkillLevelCondition implements OnlineCondition {

    /**
     * Skill to check.
     */
    private final VariableEnum<PrimarySkillType> skillType;

    /**
     * Required level in Skill.
     */
    private final VariableNumber level;

    /**
     * Create a new level condition.
     *
     * @param skillType the type to check
     * @param level     the required level
     */
    public McMMOSkillLevelCondition(final VariableEnum<PrimarySkillType> skillType, final VariableNumber level) {
        this.skillType = skillType;
        this.level = level;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return ExperienceAPI.getLevel(profile.getPlayer(),
                skillType.getValue(profile)) >= level.getValue(profile).intValue();
    }
}
