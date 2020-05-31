package ch.epfl.rigel.storage;

import ch.epfl.rigel.astronomy.CelestialObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents any object that has been added to the favorites list.
 *
 * @param <T> the type of the identifier of the object
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 31/05/2020
 */
public final class FavoriteItem<T extends Serializable> implements Serializable {

    private final CelestialObject.Type type;
    private final T identifier;

    /**
     * @param type       the type of CelestialObject this favorite is
     * @param identifier a unique object that allows to distinguish the CelestialObject
     *                   of a certain type, from the others of the same type.
     * @throws NullPointerException if the type is {@code null}
     */
    public FavoriteItem(CelestialObject.Type type, T identifier) {
        this.type = Objects.requireNonNull(type);
        this.identifier = identifier;
    }

    /**
     * @return the type of CelestialObject this favorite is.
     */
    public CelestialObject.Type getType() {
        return type;
    }

    /**
     * @return a unique object that allows to distinguish the CelestialObject
     * of a certain type, from the others of the same type.
     */
    public T getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FavoriteItem)) {
            return false;
        }

        final FavoriteItem<?> other = (FavoriteItem<?>) o;
        if (other.getType() != type) {
            return false;
        }

        return Objects.equals(other.identifier, identifier);
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + (identifier == null ? 0 : identifier.hashCode());
    }

}
