package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement"})
@SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
public class AsyncSaver extends Thread implements Listener, Saver {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The queue of records to be saved to the database.
     */
    private final Queue<Record> queue;

    /**
     * The amount of time, until the AsyncSaver tries to reconnect if there was a connection loss.
     */
    private final long reconnectInterval;

    /**
     * Whether the saver is currently running or not.
     */
    private boolean running;

    /**
     * Creates new database saver thread.
     *
     * @param log    the logger that will be used for logging
     * @param config the plugin configuration file
     */
    public AsyncSaver(final BetonQuestLogger log, final ConfigAccessor config) {
        super();
        this.log = log;
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = true;
        this.reconnectInterval = config.getLong("mysql.reconnect_interval");
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    @SuppressFBWarnings({"UW_UNCOND_WAIT", "DCN_NULLPOINTER_EXCEPTION"})
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD"})
    public void run() {
        while (true) {
            while (queue.isEmpty()) {
                if (!running) {
                    return;
                }
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        log.warn("AsyncSaver got interrupted!");
                    }
                }
            }
            final Record rec = queue.poll();
            try {
                if (BetonQuest.getInstance().getDB().isShuttingDown()) {
                    log.warn("Database is shutting down. Skipping update for record: " + rec);
                    continue;
                }
                final Connector con = Connector.getInstance();
                con.updateSQL(rec.type(), rec.args());
            } catch (final NullPointerException e) {
                log.error("Failed to update database: " + e.getMessage(), e);
            } catch (final RuntimeException e) {
                log.error("Unexpected error during database update: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void add(final Record rec) {
        synchronized (this) {
            queue.add(rec);
            notifyAll();
        }
    }

    @Override
    public void end() {
        synchronized (this) {
            running = false;
            notifyAll();
        }
    }
}
