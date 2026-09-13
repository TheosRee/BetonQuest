package org.betonquest.betonquest.compatibility.holograms.fancyholograms3;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.BetonHologramFactory;
import org.betonquest.betonquest.compatibility.holograms.PapiHologramFactory;
import org.bukkit.Location;

/**
 * Hologram Factory implementation for FancyHolograms.
 */
public class FancyHologramsHologramFactory extends PapiHologramFactory {

    /**
     * Creates a new {@link BetonHologramFactory} for FancyHolograms.
     *
     * @param log               the custom logger for this class
     * @param identifierFactory the identifier factory for placeholders
     * @param instructionApi    the instruction api to use
     */
    public FancyHologramsHologramFactory(final BetonQuestLogger log, final Instructions instructionApi,
                                         final IdentifierFactory<PlaceholderIdentifier> identifierFactory) {
        super(log, instructionApi, identifierFactory);
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        return new FancyHologramsHologram(FancyHolograms.get().getHologramFactory(), location);
    }
}
