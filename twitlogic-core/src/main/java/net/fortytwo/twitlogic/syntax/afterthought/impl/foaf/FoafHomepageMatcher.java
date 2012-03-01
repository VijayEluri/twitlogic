package net.fortytwo.twitlogic.syntax.afterthought.impl.foaf;

import net.fortytwo.twitlogic.vocabs.FOAF;
import net.fortytwo.twitlogic.syntax.afterthought.ObjectPropertyAfterthoughtMatcher;

/**
 * Expression of third-party's interest in a topic.  Possibly intrusive (?).
 *
 * @author Joshua Shinavier (http://fortytwo.net).
 */
public class FoafHomepageMatcher extends ObjectPropertyAfterthoughtMatcher {
    protected String getPropertyURI() {
        return FOAF.HOMEPAGE;
    }

    protected boolean predicateMatches(final String predicate) {
        String p = predicate.toLowerCase();
        return p.equals("homepage") | p.equals("home page");
    }
}