package ch.epfl.rigel.util;

import ch.epfl.rigel.Preconditions;

import java.io.*;
import java.util.Objects;

/**
 * Some utils for files. Named "FileUtil" to avoid conflicts
 * with NIO's "Files" class.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class FileUtil {

    /**
     * Creates the file if it does not exist, and writes the provided data {@code object} to it.
     *
     * @param object   the Object to serialize
     * @param path     the path to the file
     * @param override {@code true} if the file at the provided path exists and should then be overriden
     * @param <T>      the type of the {@link Serializable} Object
     * @throws IOException if an {@link IOException} occurred for any reason during the writing process
     */
    public static <T extends Serializable> void store(T object, String path, boolean override) throws IOException {
        Objects.requireNonNull(object);
        Preconditions.checkArgument(!Objects.requireNonNull(path).isBlank());


        final File file = initFile(path, override, false, true);
        if (file == null) {
            return; // this means that the file was not to override
        }
        try (final ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(object);
        }
    }

    /**
     * Initializes the file at the provided path: creates it, as well as the parent folders,
     * if needed.
     *
     * @param path     the path of the file
     * @param override {@code true} if the file should be overridden for the use writing
     * @param read     {@code true} if the file will be used for reading
     * @param write    {@code true} if the file will be used for writing
     * @return the file at the provided path. {@code null} if and only if the file should not been overridden
     * while being used for writing.
     * @throws IOException if the file or the parent folder could not have been created, or if
     *                     the required access has not been provided
     */
    private static File initFile(String path, boolean override, boolean read, boolean write) throws IOException {
        final File file = new File(path);
        if (file.exists()) {
            if (!override && write) {
                return null;
            }

            if (!file.delete()) {
                throw new IOException("asking for override, but cannot delete previous file '" + path + "'");
            }
        } else {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new IOException("could not create folder(s) for '" + path + "'");
                }
            }

            if (!file.createNewFile()) {
                throw new IOException("could not create file at '" + path + "'");
            }
        }

        if (write && !file.canWrite()) {
            throw new IOException("cannot write to file at '" + path + "'");
        }

        if (read && !file.canRead()) {
            throw new IOException("cannot read from file at '" + path + "'");
        }

        return file;
    }

    private FileUtil() {
    }

}
