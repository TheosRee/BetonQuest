package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Creates validated Npc Wrapper for MythicMobs Npcs.
 */
public class MMNpcFactory implements NpcFactory<ActiveMob> {
    private final MobExecutor mobExecutor;

    public MMNpcFactory(final MobExecutor mobExecutor) {
        this.mobExecutor = mobExecutor;
    }

    @Override
    public NpcWrapper<ActiveMob> parseInstruction(final Instruction instruction) throws InstructionParseException {
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
        return MythicMobNpcAdapter.class;
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
            NpcWrapper<ActiveMob> parse(final Instruction instruction, final MobExecutor mobExecutor) throws InstructionParseException {
                final Optional<MythicMob> mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(instruction.next());
                if (mythicMob.isPresent()) {
                    return new TypeWrapper(mythicMob.get(), mobExecutor);
                }
                throw new InstructionParseException("There exists no MythicMob type '" + instruction.current() + "'");
            }

            @Override
            String toInstructionString(final ActiveMob mob) {
                return name() + " " + mob.getType().getInternalName();
            }
        },
        /**
         * Identifies the Npc by {@link UUID}.
         */
        BY_UUID {
            @Override
            NpcWrapper<ActiveMob> parse(final Instruction instruction, final MobExecutor mobExecutor) throws InstructionParseException {
                final UUID uuid;
                try {
                    uuid = UUID.fromString(instruction.next());
                } catch (final IllegalArgumentException exception) {
                    throw new InstructionParseException(exception);
                }
                return new UUIDWrapper(uuid, mobExecutor);
            }

            @Override
            String toInstructionString(final ActiveMob mob) {
                return name() + " " + mob.getUniqueId().toString();
            }
        };

        abstract NpcWrapper<ActiveMob> parse(Instruction instruction, MobExecutor mobExecutor) throws InstructionParseException;

        abstract String toInstructionString(ActiveMob mob);
    }

    private static class UUIDWrapper implements NpcWrapper<ActiveMob> {
        private final UUID uuid;

        private final MobExecutor mobExecutor;

        public UUIDWrapper(final UUID uuid, final MobExecutor mobExecutor) {
            this.uuid = uuid;
            this.mobExecutor = mobExecutor;
        }

        @Override
        public Npc<ActiveMob> getNpc() throws QuestRuntimeException {
            final Optional<ActiveMob> activeMob = mobExecutor.getActiveMob(uuid);
            if (activeMob.isPresent()) {
                return new MythicMobNpcAdapter(activeMob.get());
            }
            throw new QuestRuntimeException("Could not find entity '" + uuid + "' for MythicMob Npc");
        }
    }

    private static class TypeWrapper implements NpcWrapper<ActiveMob> {
        private final MythicMob type;

        private final MobExecutor mobExecutor;

        public TypeWrapper(final MythicMob type, final MobExecutor mobExecutor) {
            this.type = type;
            this.mobExecutor = mobExecutor;
        }

        @Override
        public Npc<ActiveMob> getNpc() throws QuestRuntimeException {
            final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> mob.getType().equals(type));
            if (activeMobs.isEmpty()) {
                throw new QuestRuntimeException("Could not find MythicMob for type '" + type + "'");
            }
            if (activeMobs.size() != 1) {
                // TODO mode for random choosing?
                throw new QuestRuntimeException("There exists multiple MythicMobs with type '" + type + "', can't determine!");
            }
            return new MythicMobNpcAdapter(activeMobs.iterator().next());
        }
    }
}
