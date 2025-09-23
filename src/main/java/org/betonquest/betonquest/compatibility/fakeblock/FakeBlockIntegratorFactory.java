package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * Factory for creating {@link FakeBlockIntegrator} instances.
 */
public class FakeBlockIntegratorFactory implements IntegratorFactory {

    /**
     * The minimum required version of FakeBlock.
     */
    public static final String REQUIRED_VERSION = "2.0.1";

    /**
     * Creates a new instance of the factory.
     */
    public FakeBlockIntegratorFactory() {
    }

    @Override
    public Integrator getIntegrator(final Plugin plugin) throws HookException {
        checkRequiredVersion(plugin);

        final ServicesManager manager = plugin.getServer().getServicesManager();
        final RegisteredServiceProvider<GroupService> groupService = manager.getRegistration(GroupService.class);
        if (groupService == null) {
            throw new HookException(plugin, "Could not find service provider for GroupService");
        }
        final RegisteredServiceProvider<PlayerGroupService> playerGroupService = manager.getRegistration(PlayerGroupService.class);
        if (playerGroupService == null) {
            throw new HookException(plugin, "Could not find service provider for PlayerGroupService");
        }
        return new FakeBlockIntegrator(groupService, playerGroupService);
    }

    private void checkRequiredVersion(final Plugin plugin) throws UnsupportedVersionException {
        final Version version = new Version(plugin.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
        if (comparator.isOtherNewerThanCurrent(version, new Version(REQUIRED_VERSION))) {
            throw new UnsupportedVersionException(plugin, REQUIRED_VERSION);
        }
    }
}
