package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saves the data to the database asynchronously.
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidSynchronizedStatement"})
@SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
public class AsyncSaver extends Thread implements Saver {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The connector that connects to the database.
     */
    private final Connector con;

    /**
     * The queue of records to be saved to the database.
     */
    private final Queue<Record> queue;

    /**
     * Whether the saver is currently running or not.
     */
    private boolean running;

    /**
     * Creates new database saver thread.
     *
     * @param log       the logger that will be used for logging
     * @param connector the connector for database access
     */
    public AsyncSaver(final BetonQuestLogger log, final Connector connector) {
        super();
        this.log = log;
        this.con = connector;
        this.queue = new ConcurrentLinkedQueue<>();
        this.running = true;
    }

    @Override
    @SuppressFBWarnings({"UW_UNCOND_WAIT", "DCN_NULLPOINTER_EXCEPTION"})
    @SuppressWarnings("PMD")
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
                if (con.getDatabase().isShuttingDown()) {
                    log.warn("Database is shutting down. Skipping update for record: " + rec);
                    continue;
                }
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
