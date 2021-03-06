package net.fortytwo.twitlogic.persistence.sail;

import net.fortytwo.twitlogic.TwitLogic;
import net.fortytwo.twitlogic.persistence.SailFactory;
import net.fortytwo.twitlogic.util.properties.PropertyException;
import net.fortytwo.twitlogic.util.properties.TypedProperties;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.nativerdf.NativeStore;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class NativeStoreFactory extends SailFactory {

    private static final Logger LOGGER = TwitLogic.getLogger(NativeStoreFactory.class);

    public NativeStoreFactory(final TypedProperties conf) {
        super(conf);
    }

    public Sail makeSail() throws SailException, PropertyException {
        File dir = conf.getFile(TwitLogic.NATIVESTORE_DIRECTORY);
        String indexes = conf.getString(TwitLogic.NATIVESTORE_INDEXES, null);

        LOGGER.info("instantiating NativeStore in directory: " + dir);
        Sail sail = (null == indexes)
                ? new NativeStore(dir)
                : new NativeStore(dir, indexes.trim());
        sail.initialize();

        return sail;
    }
}
