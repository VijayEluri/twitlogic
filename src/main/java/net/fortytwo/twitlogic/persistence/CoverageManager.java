package net.fortytwo.twitlogic.persistence;

import info.aduna.iteration.CloseableIteration;
import net.fortytwo.twitlogic.TwitLogic;
import net.fortytwo.twitlogic.services.twitter.TwitterClientException;
import net.fortytwo.twitlogic.model.User;
import net.fortytwo.twitlogic.util.intervals.Interval;
import net.fortytwo.twitlogic.util.intervals.IntervalSequence;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * User: josh
 * Date: Oct 2, 2009
 * Time: 4:42:08 PM
 */
public class CoverageManager {
    private final Logger LOGGER = TwitLogic.getLogger(CoverageManager.class);

    private final PersistenceContext persistenceContext;
    private final ValueFactory valueFactory;

    public CoverageManager(final PersistenceContext persistenceContext,
                           final ValueFactory valueFactory) {
        this.persistenceContext = persistenceContext;
        this.valueFactory = valueFactory;
    }

    public IntervalSequence<Date> getCoverage(final User user,
                                              final SailConnection sc) throws SailException, TwitterClientException {
        IntervalSequence<Date> sequence = new IntervalSequence<Date>();

        for (Resource r : findIntervalResources(findUserURI(user), sc)) {
            Date start, end;

            CloseableIteration<? extends Statement, SailException> iter
                    = sc.getStatements(r, SesameTools.START_DATE, null, false, TwitLogic.AUTHORITATIVE_GRAPH);
            try {
                start = dateFromLiteral((Literal) iter.next().getObject());
            } finally {
                iter.close();
            }

            CloseableIteration<? extends Statement, SailException> iter2
                    = sc.getStatements(r, SesameTools.END_DATE, null, false, TwitLogic.AUTHORITATIVE_GRAPH);
            try {
                end = dateFromLiteral((Literal) iter2.next().getObject());
            } finally {
                iter2.close();
            }

            sequence.extendTo(new Interval<Date>(start, end));
        }

        return sequence;
    }

    public void setCoverage(final User user,
                            final IntervalSequence<Date> coverage,
                            final SailConnection sc) throws SailException, TwitterClientException {
        URI userURI = findUserURI(user);

        for (Resource r : findIntervalResources(userURI, sc)) {
            sc.removeStatements(r, null, null, TwitLogic.AUTHORITATIVE_GRAPH);
        }
        sc.removeStatements(userURI, SesameTools.COVERED_INTERVAL, null, TwitLogic.AUTHORITATIVE_GRAPH);

        for (Interval<Date> interval : coverage.getIntervals()) {
            Resource r = valueFactory.createBNode();
            sc.addStatement(r, RDF.TYPE, SesameTools.INTERVAL, TwitLogic.AUTHORITATIVE_GRAPH);
            sc.addStatement(r, SesameTools.START_DATE, SesameTools.createLiteral(interval.getStart(), valueFactory), TwitLogic.AUTHORITATIVE_GRAPH);
            sc.addStatement(r, SesameTools.END_DATE, SesameTools.createLiteral(interval.getEnd(), valueFactory), TwitLogic.AUTHORITATIVE_GRAPH);
            sc.addStatement(userURI, SesameTools.COVERED_INTERVAL, r, TwitLogic.AUTHORITATIVE_GRAPH);
        }
    }

    private Collection<Resource> findIntervalResources(final URI userURI,
                                                       final SailConnection sc) throws SailException {
        Collection<Resource> resources = new LinkedList<Resource>();
        CloseableIteration<? extends Statement, SailException> iter
                = sc.getStatements(userURI, SesameTools.COVERED_INTERVAL, null, false, TwitLogic.AUTHORITATIVE_GRAPH);
        try {
            resources.add(iter.next().getSubject());
        } finally {
            iter.close();
        }

        return resources;
    }

    private Date dateFromLiteral(final Literal lit) {
        return lit.calendarValue().toGregorianCalendar().getTime();
    }

    private URI findUserURI(final User user) throws TwitterClientException {
        return valueFactory.createURI(PersistenceContext.uriOf(user));
    }

    /*
    private final Map<String, IntervalSequence<Date>> userIntervals;

    public TrackerThingy(final Properties properties) throws PropertyException {
        TypedProperties props = new TypedProperties(properties);
        userIntervals = new HashMap<String, IntervalSequence<Date>>();

        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(TwitLogic.COVERAGE_INTERVAL_START)) {
                // Note: no error-tolerance here.
                String[] parts = key.split("-");
                String userId = parts[1];
                String intervalId = parts[2];
                Date start = props.getDate(startProperty(userId, intervalId));
                Date end = props.getDate(endProperty(userId, intervalId));
                Interval interval = new Interval(start, end);
                extend(userId, interval);
            } else {
                LOGGER.warning("unrecognized property: " + key);
            }
        }
    }

    public void writeTo(final Properties properties) {
        long count = 0;
        for (String userId : userIntervals.keySet()) {
            IntervalSequence<Date> sequence = userIntervals.get(userId);
            for (Interval<Date> interval : sequence.getIntervals()) {

            }
        }
    }

    public void extend(final String userId,
                       final Interval<Date> interval) {
        IntervalSequence<Date> sequence = userIntervals.get(userId);
        if (null == sequence) {
            sequence = new IntervalSequence<Date>();
            userIntervals.put(userId, sequence);
        }
        sequence.extendTo(interval);
    }

    private String startProperty(final String userId,
                                 final String intervalId) {
        return TwitLogic.COVERAGE_INTERVAL_START + "-" + userId + "-" + intervalId;
    }

    private String endProperty(final String userId,
                               final String intervalId) {
        return TwitLogic.COVERAGE_INTERVAL_END + "-" + userId + "-" + intervalId;
    }*/
}
