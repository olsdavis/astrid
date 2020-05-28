package ch.epfl.rigel.storage;

import ch.epfl.rigel.astronomy.CelestialObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 28/05/2020
 */
public class FavoritesList implements Serializable {

    private final List<CelestialObject> objects = new ArrayList<>();

}
