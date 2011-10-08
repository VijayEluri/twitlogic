package net.fortytwo.twitlogic.model.geo;

import twitter4j.GeoLocation;

/**
 * User: josh
* Date: Aug 20, 2010
* Time: 7:55:17 PM
*/
public class Point {
    private final double longitude;
    private final double latitude;

    public Point(final GeoLocation l) {
        this.longitude = l.getLongitude();
        this.latitude = l.getLatitude();
    }

    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String toString() {
        return "(" + longitude + ", " + latitude + ")";
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
