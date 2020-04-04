package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Abstract model for all variants of spherical systems of coordinates.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 20/02/2020
 */
abstract class SphericalCoordinates {
    private double lon;
    private double lat;

    /**
     * @param lon the longitude (in radians)
     * @param lat the latitude (in radians)
     */
    SphericalCoordinates(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * @return the longitude in radians.
     */
    double lon() {
        return lon;
    }

    /**
     * @return the latitude in radians.
     */
    double lat() {
        return lat;
    }

    /**
     * @return the longitude in degrees.
     */
    double lonDeg() {
        return Angle.toDeg(lon);
    }

    /**
     * @return the latitude in degrees.
     */
    double latDeg() {
        return Angle.toDeg(lat);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException("unsupported operation");
    }
}
