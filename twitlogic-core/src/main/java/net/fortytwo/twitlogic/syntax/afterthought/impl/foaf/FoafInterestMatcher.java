package net.fortytwo.twitlogic.syntax.afterthought.impl.foaf;

import net.fortytwo.twitlogic.vocabs.FOAF;
import net.fortytwo.twitlogic.syntax.afterthought.ObjectPropertyAfterthoughtMatcher;

/**
 * Expression of third-party's interest in a topic.  Possibly intrusive (?).
 * <p/>
 * User: josh
 * Date: Sep 29, 2009
 * Time: 10:29:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FoafInterestMatcher extends ObjectPropertyAfterthoughtMatcher {
    protected String getPropertyURI() {
        return FOAF.INTEREST;
    }

    protected boolean predicateMatches(final String predicate) {
        String p = predicate.toLowerCase();
        return p.equals("likes")
                | p.equals("like");
    }
}