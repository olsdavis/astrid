package ch.epfl.rigel.storage;

import ch.epfl.rigel.astronomy.*;
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

    private static final String FAVORITES_PATH = "rigel/favorites.data";

    // here we declare the collection as a HashSet (instead of Set), because we need the fact
    // that it is Serializable
    private final ObservableSet<Object> identifiers;

    /**
     * Reads the data from the favorites file, if it exists. Otherwise,
     * initializes an empty HashSet of data.
     *
     * @throws IOException if the data could not have been read or initialized
     */
    @SuppressWarnings("unchecked")
    public FavoritesList() throws IOException {
        // the objects we get are not generic, so we allow ourselves
        // here to make this unchecked cast, that is safe as long
        // as we know the contents of our file
        final File file = new File(FAVORITES_PATH);
        if (file.exists()) {
            identifiers = FXCollections.observableSet(FileUtil.read(HashSet.class, FAVORITES_PATH));
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
        identifiers.add(identifier(Objects.requireNonNull(object)));
    }

    /**
     * Removes the provided object from the favorites list.
     *
     * @param object a {@link CelestialObject}
     * @throws NullPointerException if the provided Object is null
     */
    public void remove(CelestialObject object) {
        identifiers.remove(identifier(Objects.requireNonNull(object)));
    }

    /**
     * @param object a {@link CelestialObject}
     * @return {@code true} if the favorites list contains the provided {@link CelestialObject}.
     * @throws NullPointerException if the provided Object is null
     */
    public boolean contains(CelestialObject object) {
        return identifiers.contains(identifier(Objects.requireNonNull(object)));
    }

    /**
     * @return a property containing the favorites with read-only access.
     */
    public ObservableSet<Object> favoritesProperty() {
        return FXCollections.unmodifiableObservableSet(identifiers);
    }

    /**
     * @return {@code true} if the user has set no favorites at all.
     */
    public boolean isEmpty() {
        return identifiers.isEmpty();
    }

    /**
     * @param object a {@link CelestialObject}
     * @return the identifier that should be used for the provided Object.
     */
    private Object identifier(CelestialObject object) {
        if (object instanceof Star) {
            return ((Star) object).hipparcosId();
        } else if (object instanceof Sun) {
            return "Soleil";
        } else if (object instanceof Moon) {
            return "Lune";
        } else if (object instanceof Planet) {
            return object.name();
        }
        return null; // should not occur
    }

    /**
     * Saves the data of the favorites list.
     *
     * @throws IOException if the data could not have been saved
     */
    public synchronized void save() throws IOException {
        FileUtil.write(new HashSet<>(identifiers), FAVORITES_PATH, true);
    }

}
