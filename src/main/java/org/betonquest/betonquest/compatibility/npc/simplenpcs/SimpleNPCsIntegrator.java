package org.betonquest.betonquest.compatibility.npc.simplenpcs;

import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class SimpleNPCsIntegrator implements Integrator {

    public SimpleNPCsIntegrator() {
    }

    @Override
    public void hook() {
        simpleNPCsListener = new SimpleNPCsListener(loggerFactory, loggerFactory.create(SimpleNPCsListener.class));
    }

    @Override
    public void reload() {
        simpleNPCsListener.reload();
    }

    @Override
    public void close() {
        // Empty
    }
}
