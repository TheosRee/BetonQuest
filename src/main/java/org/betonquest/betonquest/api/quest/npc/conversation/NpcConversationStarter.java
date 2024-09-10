package org.betonquest.betonquest.api.quest.npc.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/**
 * Starts new conversations by interacting with a Npc.
 *
 * @param <T> the original npc type
 */
public abstract class NpcConversationStarter<T> {
    /**
     * Plugin to register listener and load config.
     */
    private final BetonQuest plugin;

    /**
     * Factory to identify the clicked Npc.
     */
    private final NpcFactory<T> npcFactory;

    /**
     * Processor to start conversations on Npc interaction.
     */
    private final NpcProcessor npcProcessor;

    /**
     * Identifier used to register the {@link #npcFactory}.
     */
    private final String prefix;

    /**
     * A listener for right-clicking a Citizens NPC.
     */
    @Nullable
    private Listener rightClick;

    /**
     * A listener for left-clicking a Citizens NPC.
     */
    @Nullable
    private Listener leftClick;

    /**
     * Initializes the conversation starter.
     *
     * @param plugin       the plugin to register listener and load config
     * @param npcFactory   the factory to identify the clicked Npc
     * @param npcProcessor the processor to start conversations on Npc interaction
     * @param prefix       the same as used to register the {@code npcFactory}
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public NpcConversationStarter(final BetonQuest plugin, final NpcFactory<T> npcFactory, final NpcProcessor npcProcessor, final String prefix) {
        this.plugin = plugin;
        this.npcFactory = npcFactory;
        this.npcProcessor = npcProcessor;
        this.prefix = prefix + " "; // TODO das ist verdammt hässlich!
        reload();
    }

    /**
     * Reloads the listeners.
     */
    public final void reload() {
        if (rightClick != null) {
            HandlerList.unregisterAll(rightClick);
        }
        if (leftClick != null) {
            HandlerList.unregisterAll(leftClick);
            leftClick = null;
        }

        rightClick = newRightClickListener();
        Bukkit.getPluginManager().registerEvents(rightClick, plugin);

        if (plugin.getPluginConfig().getBoolean("acceptNPCLeftClick")) {
            leftClick = newLeftClickListener();
            Bukkit.getPluginManager().registerEvents(leftClick, plugin);
        }
    }

    /**
     * The logic that determines if a Npc interaction starts a conversation.
     *
     * @param clicker the player who clicked the Npc
     * @param npc     the supplier for lazy instantiation when the Npc is needed
     * @return if a conversation is started and the interact event should be cancelled
     */
    protected boolean interactLogic(final Player clicker, final Npc<T> npc) {
        return npcProcessor.interactLogic(clicker, npcFactory, npc);
    }

    /**
     * Gets a listener to get left-clicks on a Npc.
     *
     * @return a new left click listener
     */
    protected abstract Listener newLeftClickListener();

    /**
     * Gets a listener to get right-clicks on a Npc.
     *
     * @return a new right click listener
     */
    protected abstract Listener newRightClickListener();
}
