package org.betonquest.betonquest.quest.registry.processor;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Does the logic around Conditions.
 */
public class ConditionProcessor extends TypedQuestProcessor<ConditionID, PlayerlessCondition, PlayerCondition> {
    /**
     * Create a new Condition Processor to store Conditions and checks them.
     *
     * @param log            the custom logger for this class
     * @param conditionTypes the available condition types
     */
    public ConditionProcessor(final BetonQuestLogger log, final ConditionTypeRegistry conditionTypes) {
        super(log, conditionTypes, "Condition", "conditions");
    }

    @Override
    protected ConditionID getIdentifier(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        return new ConditionID(pack, identifier);
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public boolean checks(@Nullable final Profile profile, final ConditionID... conditionIDs) {
        if (Bukkit.isPrimaryThread()) {
            for (final ConditionID id : conditionIDs) {
                if (!check(profile, id)) {
                    return false;
                }
            }
        } else {
            final List<CompletableFuture<Boolean>> conditions = new ArrayList<>();
            for (final ConditionID id : conditionIDs) {
                final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                        () -> check(profile, id));
                conditions.add(future);
            }
            for (final CompletableFuture<Boolean> condition : conditions) {
                try {
                    if (!condition.get()) {
                        return false;
                    }
                } catch (final InterruptedException | ExecutionException e) {
                    // Currently conditions that are forced to be sync cause every CompletableFuture.get() call
                    // to delay the check by one tick.
                    // If this happens during a shutdown, the check will be delayed past the last tick.
                    // This will throw a CancellationException and IllegalPluginAccessExceptions.
                    // For Paper, we can detect this and only log it to the debug log.
                    // When the conditions get reworked, this complete check can be removed including the Spigot message.
                    if (PaperLib.isPaper() && Bukkit.getServer().isStopping()) {
                        log.debug("Exception during shutdown while checking conditions (expected):", e);
                        return false;
                    }
                    if (PaperLib.isSpigot()) {
                        log.warn("The following exception is only ok when the server is currently stopping."
                                + "Switch to papermc.io to fix this.");
                    }
                    log.reportException(e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the condition described by conditionID is met
     *
     * @param conditionID ID of the condition to check
     * @param profile     the {@link Profile} of the player which should be checked
     * @return if the condition is met
     */
    public boolean check(@Nullable final Profile profile, final ConditionID conditionID) {
        final TrippleFactory.Wrapper<PlayerlessCondition, PlayerCondition> condition = values.get(conditionID);
        if (condition == null) {
            log.warn(conditionID.getPackage(), "The condition " + conditionID + " is not defined!");
            return false;
        }
        if (profile != null && condition.playerType() != null) {
            final PlayerCondition playerCondition = condition.playerType();
            return getOutcome(() -> playerCondition.check(profile), conditionID, profile);
        }
        if (condition.playerlessType() != null) {
            final PlayerlessCondition playerlessType = condition.playerlessType();
            return getOutcome(playerlessType::check, conditionID, profile);
        }
        log.warn(conditionID.getPackage(),
                "Cannot check non-static condition '" + conditionID + "' without a player, returning false");
        return false;
    }

    private boolean getOutcome(final QREThrowingSupplier supplier, final ConditionID conditionID, @Nullable final Profile profile) {
        final boolean outcome;
        try {
            outcome = supplier.doStuff();
        } catch (final QuestRuntimeException e) {
            log.warn(conditionID.getPackage(), "Error while checking '" + conditionID + "' condition: " + e.getMessage(), e);
            return false;
        }
        final boolean isMet = outcome != conditionID.inverted();
        log.debug(conditionID.getPackage(),
                (isMet ? "TRUE" : "FALSE") + ": " + (conditionID.inverted() ? "inverted" : "") + " condition "
                        + conditionID + " for " + profile);
        return isMet;
    }

    private interface QREThrowingSupplier {
        boolean doStuff() throws QuestRuntimeException;
    }
}
