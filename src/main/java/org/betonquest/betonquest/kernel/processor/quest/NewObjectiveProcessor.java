package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.NewObjective;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.id.NewObjID;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.registry.quest.NewObjectiveTypeRegistry;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores Objectives and starts/stops/resumes them.
 * <p>
 * Does only work for global objectives.
 */
public class NewObjectiveProcessor extends TypedQuestProcessor<NewObjID, NewObjective> {

    /**
     * Manager to register listener.
     */
    private final PluginManager pluginManager;

    /**
     * Plugin instance to associate registered listener with.
     */
    private final Plugin plugin;

    /**
     * Loaded global objectives.
     */
    private final Set<NewObjID> globalObjectiveIds;

    /**
     * Create a new Objective Processor to store Objectives and starts/stops/resumes them.
     *
     * @param log            the custom logger for this class
     * @param objectiveTypes the available objective types
     * @param pluginManager  the manager to register listener
     * @param plugin         the plugin instance to associate registered listener with
     */
    public NewObjectiveProcessor(final BetonQuestLogger log, final NewObjectiveTypeRegistry objectiveTypes,
                                 final PluginManager pluginManager, final Plugin plugin) {
        super(log, objectiveTypes, "New Objective", "new_objectives");
        this.pluginManager = pluginManager;
        this.plugin = plugin;
        this.globalObjectiveIds = new HashSet<>();
    }

    /**
     * Get the tag used to mark an already started global objective.
     *
     * @param objectiveID the id of a global objective
     * @return the tag which marks that the given global objective has already been started for the player
     */
    public static String getTag(final NewObjID objectiveID) {
        return objectiveID.getPackage().getQuestPath() + ".global-" + objectiveID.getBaseID();
    }

    @Override
    public void clear() {
        globalObjectiveIds.clear();
        for (final NewObjective objective : values.values()) {
            objective.close();
            if (objective instanceof Listener) {
                HandlerList.unregisterAll((Listener) objective);
            }
        }
        super.clear();
    }

    @Override
    protected NewObjID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new NewObjID(pack, identifier);
    }

    @Override
    protected void postCreation(final NewObjID identifier, final NewObjective value) {
        if (identifier.getInstruction().hasArgument("global")) {
            globalObjectiveIds.add(identifier);
        }
        if (value instanceof Listener) {
            pluginManager.registerEvents((Listener) value, plugin);
        }
    }

    /**
     * Creates new objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    public void start(final Profile profile, final NewObjID objectiveID) {
        final NewObjective objective = values.get(objectiveID);
        if (objective == null) {
            log.error("Tried to start objective '" + objectiveID.getFullID() + "' but it is not loaded! Check for errors on /bq reload!");
            return;
        }
        if (objective.containsPlayer(profile)) {
            log.debug(objectiveID.getPackage(), profile + " already has the " + objectiveID + " objective");
            return;
        }
        objective.newPlayer(profile);
    }

    /**
     * Resumes the existing objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     * @param instruction data instruction string
     */
    public void resume(final Profile profile, final NewObjID objectiveID, final String instruction) {
        final NewObjective objective = values.get(objectiveID);
        if (objective == null) {
            log.warn(objectiveID.getPackage(), "Objective " + objectiveID + " does not exist");
            return;
        }
        if (objective.containsPlayer(profile)) {
            log.debug(objectiveID.getPackage(), profile + " already has the " + objectiveID + " objective!");
            return;
        }
        objective.resumeObjectiveForPlayer(profile, instruction);
    }

    /**
     * Returns the list of objectives of this player.
     *
     * @param profile the {@link Profile} of the player
     * @return list of this player's active objectives
     */
    public List<NewObjective> getActive(final Profile profile) {
        final List<NewObjective> list = new ArrayList<>();
        for (final NewObjective objective : values.values()) {
            if (objective.containsPlayer(profile)) {
                list.add(objective);
            }
        }
        return list;
    }

    /**
     * Starts all unstarted global objectives for the player.
     *
     * @param profile     the {@link Profile} of the player
     * @param dataStorage the storage providing player data
     */
    public void startAll(final Profile profile, final PlayerDataStorage dataStorage) {
        final PlayerData data = dataStorage.get(profile);
        for (final NewObjID id : globalObjectiveIds) {
            final NewObjective objective = values.get(id);
            final String tag = getTag(id);
            if (objective == null || data.hasTag(tag)) {
                continue;
            }
            objective.newPlayer(profile);
            data.addTag(tag);
        }
    }
}
