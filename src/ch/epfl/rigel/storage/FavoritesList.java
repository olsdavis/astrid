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
    it is a pretty practical solution to not conflict with the code
    base we already had.
     */

    /**
     * @param object the object to identify
     * @return an Object that allows to distinguish a CelestialObject of a certain type
     * from the other objects of the same type. Returns the name of the object for any type,
     * except for the Stars, where multiple stars can have the same Hipparcos ID and the same
     * name (at the same time: <em>e.g.</em> for Hipparcos 0); thus, we return a String containing
     * more data about the star.
     */
    public static Serializable identify(CelestialObject object) {
        if (object.getType() == CelestialObject.Type.STAR) {
            return ((Star) object).hipparcosId() + "," + object.name() + "," + object.equatorialPos().ra() + "," + object.equatorialPos().dec();
        }
        return object.name();
    }

    private final String path;
    // here we declare the collection as a HashSet (instead of Set), because we need the fact
    // that it is Serializable
    private final ObservableSet<FavoriteItem<?>> identifiers;

    /**
     * Reads the data from the favorites file, if it exists. Otherwise,
     * initializes an empty HashSet of data.
     *
     * @param path the path to the file that stores the favorites data. If the String
     *             is empty, does not use any file I/O feature. (Not persistent data
     *             mode.)
     *
     * @throws IOException if the data could not have been read or initialized
     */
    @SuppressWarnings("unchecked")
    public FavoritesList(String path) throws IOException {
        this.path = path;
        if (path != null && !path.isEmpty()) {
            final File file = new File(path);
            if (file.exists()) {
                // the objects we get are not generic, so we allow ourselves
                // here to make this unchecked cast, that is safe given that
                // we know the contents of our file
                identifiers = FXCollections.observableSet(FileUtil.readObject(HashSet.class, path));
            } else {
                identifiers = FXCollections.observableSet();
            }
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
        if (path != null && !path.isEmpty()) {
            FileUtil.writeObject(new HashSet<>(identifiers), path, true);
        }
    }

}
