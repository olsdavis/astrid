package ch.epfl.rigel.astronomy;

/**
 * This class solely holds all the existing cardinal and intercardinal
 * (<em>e.g.</em> South-West) points.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 21/04/2020
 */
public enum CardinalPoint {

    /**
     * North
     */
    N(0),
    /**
     * North-East
     */
    NE(45d),
    /**
     * East
     */
    E(90d),
    /**
     * South-East
     */
    SE(135d),
    /**
     * South
     */
    S(180d),
    /**
     * South-West
     */
    SW(225d),
    /**
     * West
     */
    W(270d),
    /**
     * North-West
     */
    NW(315d);

    private final double azimuth;

    /**
     * @param azimuth the azimuth of the cardinal point in degrees, using the horizontal
     *                coordinates system.
     */
    CardinalPoint(double azimuth) {
        this.azimuth = azimuth;
    }

    /**
     * @return the azimuth of the current cardinal point in degrees, using the horizontal
     * coordinates system.
     */
    public double azDeg() {
        return azimuth;
    }

}
