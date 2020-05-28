package ch.epfl.rigel.util;

import ch.epfl.rigel.Preconditions;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simple util class that allows building more complex texts (namely bold,
 * italic text).
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class Texts {

    private static final char ITALIC_CHAR = '_';
    private static final char BOLD_CHAR = '*';
    private static final char ESCAPE_CHAR = '\\';

    /**
     * Parses the provided text and builds a {@link Text} JavaFX object,
     * using the following syntax:
     * <p>
     * Surround a part of your text with "*" (asterisks) to make it bold:
     * <pre>
     *     Texts.parse("Hello *world*!);
     * </pre>
     * This code will make the "world" word bold.
     * <p>
     * Surround a part of your text with "_" (underscores) to make it italic:
     * <pre>
     *     Texts.parse("Hello _world_!");
     * </pre>
     * This code will make the "world" world italic.
     * <p>
     * If you want to use the reserved characters, you may also add an antislash
     * before them --- it is used as the escape character.
     *
     * @param expr the expression to parse.
     * @return a text containing the wanted style.
     */
    public static TextFlow parse(String expr) {
        Preconditions.checkArgument(!Objects.requireNonNull(expr).equals(""));
        final List<Text> parts = new ArrayList<>();
        final TextFlow flow = new TextFlow();
        boolean skipNext = false;
        boolean opened = false;
        int lastMarker = 0;
        for (int i = 0; i < expr.length(); ++i) {
            final char current = expr.charAt(i);
            if (current == ESCAPE_CHAR) { // skip next marker
                skipNext = true;
            } else if (current == ITALIC_CHAR || current == BOLD_CHAR) { // if style detected
                if (skipNext) { // if has to skip
                    skipNext = false;
                } else if (opened) {
                    opened = false; // reset expression opened
                    // +1 to remove the marker character
                    final String part = expr.substring(lastMarker + 1, i);
                    // skip the current character
                    ++i;
                    final Text add = new Text(part);
                    if (current == ITALIC_CHAR) {
                        add.setStyle("-fx-font-style: italic;");
                    } else {
                        add.setStyle("-fx-font-weight: bold;");
                    }
                    lastMarker = i; // setup for the next part
                    parts.add(add);
                } else {
                    opened = true; // the expression is now opened
                    lastMarker = i;
                }
            } else {
                skipNext = false; // there was no character to skip
            }
        }
        // add the last bit
        if (lastMarker != expr.length()) {
            parts.add(new Text(expr.substring(lastMarker)));
        }
        flow.getChildren().addAll(parts);
        return flow;
    }

    private Texts() {
    }

}
