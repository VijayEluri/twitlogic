package net.fortytwo.twitlogic.model;

/**
 * User: josh
 * Date: Sep 5, 2009
 * Time: 1:23:07 AM
 */
public interface Resource {
    enum Type {
        HASHTAG, USER, PLAIN_LITERAL, TYPED_LITERAL, URI_REFERENCE
    }

    Type getType();
}
