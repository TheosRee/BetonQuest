package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.bukkit.Location;

/**
 * MythicMobs Compatibility Adapter for general BetonQuest NPC behaviour.
 *
 * @param activeMob The ActiveMob instance.
 */
public record MMBQAdapter(ActiveMob activeMob) implements BQNPCAdapter<ActiveMob> {
    @Override
    public ActiveMob getOriginal() {
        return activeMob;
    }

    @Override
    public String getId() {
        return activeMob.getUniqueId().toString();
    }

    @Override
    public String getName() {
        return activeMob.getName();
    }

    @Override
    public String getFormattedName() {
        return activeMob.getDisplayName();
    }

    @Override
    public Location getLocation() {
        return activeMob.getEntity().getBukkitEntity().getLocation();
    }

    @Override
    public void teleport(final Location location) {
        activeMob.getEntity().teleport(new AbstractLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
    }
}
