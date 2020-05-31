package ch.epfl.rigel.storage;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.util.FileUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

/**
 * Holds the favorite objects of the users (in a set, actually, to guarantee
 * uniqueness).
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 28/05/2020
 */
public class FavoritesList implements Serializable {

    /*
    The way we chose to store the data is somewhat unclean, but
    it was a pretty practical solution to not conflict with the code
    base we already had.
     */

    private static final String FAVORITES_PATH = "rigel/favorites.data";

    /**
     * @param object the object to identify
     * @return an Object that allows to differentiate a CelestialObject of a certain type
     * from the other objects of the same type. Returns {@code null} in the case of {@link ch.epfl.rigel.astronomy.Sun}
     * an {@link ch.epfl.rigel.astronomy.Moon}, since there is only a single instance of them
     * per {@link ch.epfl.rigel.astronomy.ObservedSky}. In the case of a {@link ch.epfl.rigel.astronomy.Planet},
     * the code returns its name. In the case of a {@link Star}, it returns its hipparcos (unique) ID.
     */
    private static Serializable identify(CelestialObject object) {
        //TODO: note that the Hipparcos ID is not unique for zero,
        // Neither is the name (for the same hipparcos)
        // So we need to add the ID, name and position
        switch (object.getType()) {
            case STAR:
                return ((Star) object).hipparcosId();
            case PLANET:
                return object.name();
            default:
                return null;
        }
    }

    // here we declare the collection as a HashSet (instead of Set), because we need the fact
    // that it is Serializable
    private final ObservableSet<FavoriteItem<?>> identifiers;

    /**
     * Reads the data from the favorites file, if it exists. Otherwise,
     * initializes an empty HashSet of data.
     *
     * @throws IOException if the data could not have been read or initialized
     */
    @SuppressWarnings("unchecked")
    public FavoritesList() throws IOException {
        final File file = new File(FAVORITES_PATH);
        if (file.exists()) {
            // the objects we get are not generic, so we allow ourselves
            // here to make this unchecked cast, that is safe given that
            // we know the contents of our file
            identifiers = FXCollections.observableSet(FileUtil.readObject(HashSet.class, FAVORITES_PATH));
        } else {
            identifiers = FXCollections.observableSet();
        }
    }

    /**
     * Adds the provided object to the favorites list.
     *
     * @param object a {@link CelestialObject}
     * @throws NullPointerException if the provided Object is null
     */
    public void add(CelestialObject object) {
        identifiers.add(new FavoriteItem<>(Objects.requireNonNull(object).getType(), identify(object)));
    }

    /**
     * Removes the provided object from the favorites list.
     *
     * @param object a {@link CelestialObject}
     * @throws NullPointerException if the provided Object is null
     */
    public void remove(CelestialObject object) {
        identifiers.remove(new FavoriteItem<>(Objects.requireNonNull(object).getType(), identify(object)));
    }

    /**
     * @param object a {@link CelestialObject}
     * @return {@code true} if the favorites list contains the provided {@link CelestialObject}.
     * @throws NullPointerException if the provided Object is null
     */
    public boolean contains(CelestialObject object) {
        return identifiers.contains(new FavoriteItem<>(Objects.requireNonNull(object).getType(), identify(object)));
    }

    /**
     * @return a property containing the favorites with read-only access.
     */
    public ObservableSet<FavoriteItem<?>> favoritesProperty() {
        return FXCollections.unmodifiableObservableSet(identifiers);
    }

    /**
     * @return {@code true} if the user has set no favorites at all.
     */
    public boolean isEmpty() {
        return identifiers.isEmpty();
    }

    /**
     * Saves the data of the favorites list.
     *
     * @throws IOException if the data could not have been saved
     */
    public synchronized void save() throws IOException {
        FileUtil.writeObject(new HashSet<>(identifiers), FAVORITES_PATH, true);
    }

}
