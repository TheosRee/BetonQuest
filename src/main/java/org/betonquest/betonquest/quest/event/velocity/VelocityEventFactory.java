package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableVector;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory to create velocity events from {@link Instruction}s.
 */
public class VelocityEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;

    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;

    /**
     * Create the velocity event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler to use
     * @param plugin        plugin to use
     */
    public VelocityEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String rawVector = instruction.getOptional("vector");
        if (rawVector == null) {
            throw new InstructionParseException("A 'vector' is required");
        }
        final VariableVector vector = new VariableVector(BetonQuest.getInstance().getVariableProcessor(), instruction.getPackage(), rawVector);
        final VectorDirection direction = instruction.getEnum(instruction.getOptional("direction"), VectorDirection.class, VectorDirection.ABSOLUTE);
        final VectorModification modification = instruction.getEnum(instruction.getOptional("modification"), VectorModification.class, VectorModification.SET);
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        loggerFactory.create(VelocityEvent.class), new VelocityEvent(vector, direction, modification),
                        instruction.getPackage()),
                server, scheduler, plugin);
    }
}
