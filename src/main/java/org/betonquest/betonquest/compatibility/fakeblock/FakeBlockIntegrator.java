package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.fakeblock.event.FakeBlockEventFactory;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integrates with FakeBlock.
 */
public class FakeBlockIntegrator implements Integrator {

    /**
     * GroupService to search for existing Groups from FakeBlock.
     */
    private final RegisteredServiceProvider<GroupService> groupService;

    /**
     * PlayerGroupService to change group states for the player.
     */
    private final RegisteredServiceProvider<PlayerGroupService> playerGroupService;

    /**
     * Create the FakeBlock integration.
     *
     * @param groupService       the {@link GroupService} service.
     * @param playerGroupService the {@link PlayerGroupService}.
     */
    public FakeBlockIntegrator(final RegisteredServiceProvider<GroupService> groupService,
                               final RegisteredServiceProvider<PlayerGroupService> playerGroupService) {
        this.groupService = groupService;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {

        api.getQuestRegistries().event().register("fakeblock",
                new FakeBlockEventFactory(groupService, playerGroupService, api.getPrimaryServerThreadData()));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
