package org.betonquest.betonquest.compatibility.mythicmobs.npc;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * MythicMobs {@link Npc} Adapter.
 *
 * @param activeMob The ActiveMob instance.
 */
public record MythicMobsNpcAdapter(ActiveMob activeMob) implements Npc<ActiveMob> {
    @Override
    public ActiveMob getOriginal() {
        return activeMob;
    }

    @Override
    @Nullable
    public Entity getEntity() {
        return activeMob.getEntity().getBukkitEntity();
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

    @Override
    public boolean isSpawned() {
        return activeMob.getEntity().isValid();
    }

    @Override
    public void show(final OnlineProfile onlineProfile) {
        // TODO hider or however MEG does that?
    }

    @Override
    public void hide(final OnlineProfile onlineProfile) {
        // TODO hider or however MEG does that?

    }
}
