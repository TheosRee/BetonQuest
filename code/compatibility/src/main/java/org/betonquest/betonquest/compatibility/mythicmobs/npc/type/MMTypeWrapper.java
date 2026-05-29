package org.betonquest.betonquest.compatibility.mythicmobs.npc.type;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsNpcAdapter;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.WrappingMMNpcAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gets the Mob Npc by their {@link MythicMob} definition.
 */
public class MMTypeWrapper extends GenericMythicTypeWrapper {

    /**
     * Identifying mythic mob type.
     */
    private final Argument<MythicMob> type;

    /**
     * Creates a new instance.
     *
     * @param mobExecutor the instance to get the mob from
     * @param mythicHider the hider for mobs
     * @param type        the identifying mythic mob type
     */
    public MMTypeWrapper(final MobExecutor mobExecutor, final MythicHider mythicHider, final Argument<MythicMob> type) {
        super(mobExecutor, mythicHider, "type");
        this.type = type;
    }

    @Override
    public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
        final Set<Npc<ActiveMob>> npcs = getNpcs(profile);
        final int one = 1;
        if (npcs.size() != one) {
            throw new QuestException("There exists multiple MythicMobs with type '" + type + "', can't determine!");
        }
        return npcs.iterator().next();
    }

    @Override
    public Set<Npc<ActiveMob>> getNpcs(@Nullable final Profile profile) throws QuestException {
        final MythicMob type = this.type.getValue(profile);
        final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> type.equals(mob.getType()));
        if (activeMobs.isEmpty()) {
            return Set.of(new WrappingMMNpcAdapter(type, mythicHider));
        }
        return activeMobs.stream()
                .map(activeMob -> new MythicMobsNpcAdapter(activeMob, mythicHider))
                .collect(Collectors.toSet());
    }
}
