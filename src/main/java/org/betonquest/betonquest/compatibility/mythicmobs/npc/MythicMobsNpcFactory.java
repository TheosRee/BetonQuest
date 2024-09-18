package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Creates validated Npc Wrapper for MythicMobs Npcs.
 */
public class MythicMobsNpcFactory implements NpcFactory<ActiveMob> {
    /**
     * Instance to get mobs from the MythicMobs plugin.
     */
    private final MobExecutor mobExecutor;

    /**
     * Creates a new factory to get MythicMobs as Npcs from Instructions.
     *
     * @param mobExecutor to get mobs from the MythicMobs plugin
     */
    public MythicMobsNpcFactory(final MobExecutor mobExecutor) {
        this.mobExecutor = mobExecutor;
    }

    @Override
    public NpcWrapper<ActiveMob> parseInstruction(final Instruction instruction) throws QuestException {
        final Type type = instruction.getEnum(Type.class);
        return type.parse(instruction, mobExecutor);
    }

    @Override
    public Set<String> npcInstructionStrings(final Npc<ActiveMob> npc) {
        final ActiveMob original = npc.getOriginal();
        final String byType = Type.BY_MYTHIC_MOB.toInstructionString(original);
        final String byUniqueId = Type.BY_UUID.toInstructionString(original);
        return Set.of(byType, byUniqueId);
    }

    @Override
    public Class<? extends Npc<ActiveMob>> factoredClass() {
        return MythicMobsNpcAdapter.class;
    }

    /**
     * How the Npc is identified.
     */
    private enum Type {
        /**
         * Identifies the Npc by {@link MythicMob}.
         */
        BY_MYTHIC_MOB {
            @Override
            protected NpcWrapper<ActiveMob> parse(final Instruction instruction, final MobExecutor mobExecutor) throws QuestException {
                final Optional<MythicMob> mythicMob = mobExecutor.getMythicMob(instruction.next());
                if (mythicMob.isPresent()) {
                    return new TypeWrapper(mythicMob.get(), mobExecutor);
                }
                throw new QuestException("There exists no MythicMob type '" + instruction.current() + "'");
            }

            @Override
            protected String toInstructionString(final ActiveMob mob) {
                return name() + " " + mob.getType().getInternalName();
            }
        },
        /**
         * Identifies the Npc by {@link UUID}.
         */
        BY_UUID {
            @Override
            protected NpcWrapper<ActiveMob> parse(final Instruction instruction, final MobExecutor mobExecutor) throws QuestException {
                final UUID uuid;
                try {
                    uuid = UUID.fromString(instruction.next());
                } catch (final IllegalArgumentException exception) {
                    throw new QuestException(exception);
                }
                return new UUIDWrapper(uuid, mobExecutor);
            }

            @Override
            protected String toInstructionString(final ActiveMob mob) {
                return name() + " " + mob.getUniqueId().toString();
            }
        };

        /**
         * Gets the Wrapper representing the instruction.
         *
         * @param instruction the instruction with already consumed type parameter
         * @param mobExecutor the instance to get MythicMobs from
         * @return a new validated wrapper
         * @throws QuestException if the instruction cannot be parsed or there is no valid target for it
         */
        protected abstract NpcWrapper<ActiveMob> parse(Instruction instruction, MobExecutor mobExecutor) throws QuestException;

        /**
         * Gets the instruction string which parsing would result in getting that mob.
         *
         * @param mob the mob to parse
         * @return the string that would get that mob as instruction
         */
        protected abstract String toInstructionString(ActiveMob mob);
    }

    /**
     * Gets the Mob Npc by {@link UUID}.
     *
     * @param uuid        the identifying uuid
     * @param mobExecutor the instance to get the mob from
     */
    private record UUIDWrapper(UUID uuid, MobExecutor mobExecutor) implements NpcWrapper<ActiveMob> {
        @Override
        public Npc<ActiveMob> getNpc() throws QuestException {
            final Optional<ActiveMob> activeMob = mobExecutor.getActiveMob(uuid);
            if (activeMob.isPresent()) {
                return new MythicMobsNpcAdapter(activeMob.get());
            }
            throw new QuestException("Could not find entity '" + uuid + "' for MythicMob Npc");
        }
    }

    /**
     * Gets the Mob Npc by their {@link MythicMob} definition.
     *
     * @param type        the identifying type
     * @param mobExecutor the instance to get the mob from
     */
    private record TypeWrapper(MythicMob type, MobExecutor mobExecutor) implements NpcWrapper<ActiveMob> {
        @Override
        public Npc<ActiveMob> getNpc() throws QuestException {
            final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> mob.getType().equals(type));
            if (activeMobs.isEmpty()) {
                throw new QuestException("Could not find MythicMob for type '" + type + "'");
            }
            final int one = 1;
            if (activeMobs.size() != one) {
                // TODO mode for random choosing? in instruction: random, all nearest (-.,.-) or throw
                throw new QuestException("There exists multiple MythicMobs with type '" + type + "', can't determine!");
            }
            return new MythicMobsNpcAdapter(activeMobs.iterator().next());
        }
    }
}
