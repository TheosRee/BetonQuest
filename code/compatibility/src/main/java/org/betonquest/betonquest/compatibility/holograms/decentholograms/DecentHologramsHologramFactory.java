package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.BetonHologramFactory;
import org.betonquest.betonquest.compatibility.holograms.PapiHologramFactory;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Hologram Factory implementation for DecentHolograms.
 */
public class DecentHologramsHologramFactory extends PapiHologramFactory {

    /**
     * Creates a new {@link BetonHologramFactory} for DecentHolograms.
     *
     * @param log               the custom logger for this class
     * @param identifierFactory the identifier factory for placeholders
     * @param instructionApi    the instruction api to use
     */
    public DecentHologramsHologramFactory(final BetonQuestLogger log, final IdentifierFactory<PlaceholderIdentifier> identifierFactory,
                                          final Instructions instructionApi) {
        super(log, instructionApi, identifierFactory);
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), location);
        hologram.enable();
        return new DecentHologramsHologram(hologram);
    }
}
