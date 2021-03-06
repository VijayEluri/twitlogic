package net.fortytwo.twitlogic.persistence.beans;

import net.fortytwo.twitlogic.vocabs.FOAF;
import org.openrdf.concepts.owl.Thing;
import org.openrdf.elmo.annotations.inverseOf;
import org.openrdf.elmo.annotations.rdf;

import java.util.Set;

/**
 * @author Joshua Shinavier (http://fortytwo.net).
 */
@rdf(FOAF.AGENT)
public interface Agent extends Thing {
    
    @rdf(FOAF.NAME)
    String getName();
    void setName(String name);

    @rdf(FOAF.DEPICTION)
    Image getDepiction();
    void setDepiction(Image depiction);

    @rdf(FOAF.HOMEPAGE)
    Document getHomepage();
    void setHomepage(Document homepage);
    
    // Note: in terms of Java interfaces and methods, this is a simplification
    // of the FOAF ontology for the purpose of this application.  However, the
    // generated RDF graphs are correct according to the ontology.
    @rdf(FOAF.BASEDNEAR)
    SpatialThing getBasedNear();
    void setBasedNear(SpatialThing basedNear);

    @rdf(FOAF.KNOWS)
    Set<Agent> getKnows();
    void setKnows(Set<Agent> knows);

    @inverseOf(FOAF.KNOWS)
    Set<Agent> getKnownBy();
    void setKnownBy(Set<Agent> knownBy);
}
