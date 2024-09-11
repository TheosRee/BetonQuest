package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.api.quest.npc.conversation.NpcConversation;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.type.NpcTypeRegistry;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Stores Npcs and starts Npc conversations.
 */
public class NpcProcessor extends TypedQuestProcessor<NpcID, NpcWrapper<?>> {
    /**
     * The section in which the assignments from Npcs inside conversations are stored.
     */
    private static final String NPC_SECTION = "npcs";

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Plugin to load config.
     */
    private final BetonQuest plugin;

    /**
     * Stores the last time the player interacted with an NPC.
     */
    private final Map<UUID, Long> npcInteractionLimiter = new HashMap<>();

    /**
     * Stores the conversations assigned to NPCs via the configuration.
     * The key could either be an NPC's name or its ID, depending on the configuration.
     */
    private final Map<String, ConversationID> assignedConversations = new HashMap<>();

    /**
     * The minimum time between two interactions with an NPC.
     */
    private int interactionLimit;

    /**
     * Create a new Quest Npc Processor to store them.
     *
     * @param log           the custom logger for this class
     * @param npcTypes      the available npc types
     * @param loggerFactory the logger factory used to create logger for the started conversations
     * @param plugin        the plugin to load config
     */
    public NpcProcessor(final BetonQuestLogger log, final NpcTypeRegistry npcTypes, final BetonQuestLoggerFactory loggerFactory, final BetonQuest plugin) {
        super(log, npcTypes, "Npcs", "npc_definitions");
        this.loggerFactory = loggerFactory;
        this.plugin = plugin;
        interactionLimit = plugin.getPluginConfig().getInt("npcInteractionLimit", 500);
    }

    /**
     * Loads the npc references to start the conversation on interaction with them.
     *
     * @param convID      the conversation to start
     * @param convSection the section to load the references
     */
    public void loadConversation(final ConversationID convID, final ConfigurationSection convSection) {
        if (convSection.isString(NPC_SECTION)) {
            for (final String string : Objects.requireNonNull(convSection.getString(NPC_SECTION)).split(",")) {
                try {
                    final NpcID identifier = new NpcID(convID.getPackage(), string);
                    assignedConversations.put(identifier.getInstruction().toString(), convID);
                } catch (final ObjectNotFoundException exception) {
                    log.warn(convID.getPackage(), "Error while loading Npc in conversation " + convID.getFullID() + "': " + exception.getMessage()
                            + "! The conversation will still load but the Npc won't start it.", exception);
                }
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        interactionLimit = plugin.getPluginConfig().getInt("npcInteractionLimit", 500);
    }

    @Override
    protected NpcID getIdentifier(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        return new NpcID(pack, identifier);
    }

    /**
     * Gets a Npc by its id.
     *
     * @param npcID the id of the Npc
     * @return the wrapper to get the actual
     * @throws QuestRuntimeException when there is no Npc with that id
     */
    public Npc<?> getNpc(final NpcID npcID) throws QuestRuntimeException {
        final NpcWrapper<?> npcWrapper = values.get(npcID);
        if (npcWrapper == null) {
            throw new QuestRuntimeException("Tried to get npc '" + npcID.getFullID() + "' but it is not loaded! Check for errors on /bq reload!");
        }
        return npcWrapper.getNpc();
    }

    /**
     * The logic that determines if an NPC interaction starts a conversation.
     *
     * @param clicker    the player who clicked the NPC
     * @param npcFactory the factory used to create types of the clicked npc
     * @param npc        the npc which was interacted with
     * @param <T>        the original type of the npc
     * @return if a conversation is started and the interact event should be cancelled
     */
    public <T> boolean interactLogic(final Player clicker, final NpcFactory<T> npcFactory, final Npc<T> npc) {
        if (!clicker.hasPermission("betonquest.conversation")) {
            return false;
        }
        final UUID playerUUID = clicker.getUniqueId();

        final Long lastClick = npcInteractionLimiter.get(playerUUID);
        final long currentClick = new Date().getTime();
        if (lastClick != null && lastClick + interactionLimit >= currentClick) {
            return false;
        }
        npcInteractionLimiter.put(playerUUID, currentClick);

        final OnlineProfile onlineProfile = PlayerConverter.getID(clicker);
        if (CombatTagger.isTagged(onlineProfile)) {
            try {
                Config.sendNotify(null, onlineProfile, "busy", "busy,error");
            } catch (final QuestRuntimeException e) {
                log.warn("The notify system was unable to play a sound for the 'busy' category. Error was: '" + e.getMessage() + "'", e);
            }
            return false;
        }

        return startConversation(clicker, npcFactory, npc, onlineProfile);
    }

    private <T> boolean startConversation(final Player clicker, final NpcFactory<T> npcFactory, final Npc<T> npc, final OnlineProfile onlineProfile) {
        final boolean npcsByName = Boolean.parseBoolean(Config.getConfigString("citizens_npcs_by_name"));
        final String selector;
        if (npcsByName) {
            selector = npc.getName();
        } else {
            // TODO find a better way for this…
            selector = ((NpcTypeRegistry) types).getFactoryIdentifier(npcFactory) + " " + npcFactory.npcToInstructionString(npc);
        }
        final ConversationID conversationID = assignedConversations.get(selector);

        if (conversationID == null) {
            log.debug("Player '" + clicker.getName() + "' clicked Npc '" + selector + "' but there is no conversation assigned to it.");
            return false;
        } else {
            new NpcConversation<>(loggerFactory.create(NpcConversation.class), onlineProfile, conversationID, npc.getLocation(), npc);
            return true;
        }
    }
}
