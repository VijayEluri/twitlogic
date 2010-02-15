package net.fortytwo.twitlogic.persistence;

import net.fortytwo.twitlogic.TwitLogic;
import org.openrdf.elmo.ElmoManager;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import java.util.logging.Logger;

/**
 * User: josh
 * Date: Nov 30, 2009
 * Time: 6:00:43 PM
 */
public class TweetStoreConnection {
    private static final Logger LOGGER = TwitLogic.getLogger(TweetStoreConnection.class);

    private final TweetStore tweetStore;
    private final SailConnection sailConnection;
    private ElmoManager elmoManager;

    public TweetStoreConnection(final TweetStore tweetStore) throws TweetStoreException {
        this.tweetStore = tweetStore;

        try {
            this.sailConnection = tweetStore.getSail().getConnection();
        } catch (SailException e) {
            throw new TweetStoreException(e);
        }

        createElmoManager();
    }

    private void createElmoManager() {
        elmoManager = tweetStore.getElmoManagerFactory().createElmoManager();

        // Use an active transaction (rather than using auto-commit mode).
        // We will explicitly call commit() and rollback().
        elmoManager.getTransaction().begin();
    }

    private void closeElmoManager() {
        elmoManager.close();
    }

    public SailConnection getSailConnection() {
        return sailConnection;
    }

    public ElmoManager getElmoManager() {
        return elmoManager;
    }

    public void commit() throws TweetStoreException {
        try {
            if (!elmoManager.getTransaction().isActive()) {
                LOGGER.warning("Elmo transaction is not active.  Creating a new Elmo manager.");
                createElmoManager();
            }
            
            elmoManager.getTransaction().commit();
        } finally {
            try {
                sailConnection.commit();
            } catch (SailException e) {
                throw new TweetStoreException(e);
            }
        }
    }

    public void rollback() throws TweetStoreException {
        try {
        elmoManager.getTransaction().rollback();
        } finally {
            try {
                sailConnection.rollback();
            } catch (SailException e) {
                throw new TweetStoreException(e);
            }
        }
    }

    public void close() throws TweetStoreException {
        try {
            closeElmoManager();
        } finally {
            try {
                sailConnection.close();
            } catch (SailException e) {
                throw new TweetStoreException(e);
            }
        }
    }
}