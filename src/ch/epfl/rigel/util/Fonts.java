package ch.epfl.rigel.util;

import javafx.scene.text.Font;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Some font utils.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class Fonts {

    /**
     * The font used for displaying icons.
     */
    public static final Font ICONS_FONT = Fonts.loadUnsafe("/Font Awesome 5 Free-Solid-900.otf", 15d);

    /**
     * Loads "unsafely" the font from the resources folder. This is useful for
     * resources since they must be present, and the operation almost certainly
     * will succeed.
     *
     * @param path the path to the font, in the resources
     * @param size the size of the font (a negative or zero value will result
     *             in the default size)
     * @return the loaded font at the provided path.
     */
    public static Font loadUnsafe(String path, double size) {
        try (final InputStream stream = Fonts.class.getResourceAsStream(path)) {
            return Font.loadFont(stream, size);
        } catch (IOException e) {
            throw new UncheckedIOException(e); // should not occur, since we use the resources
        }
    }

    /**
     * Loads "unsafely" the font from the resources folder. This is useful for
     * resources since they must be present, and the operation almost certainly
     * will succeed.
     *
     * @param path the path to the font, in the resources
     * @return the loaded font at the provided path.
     */
    public static Font loadUnsafe(String path) {
        return loadUnsafe(path, -1d);
    }

    private Fonts() {}

}
