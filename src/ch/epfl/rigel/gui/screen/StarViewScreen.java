package ch.epfl.rigel.gui.screen;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.gui.*;
import ch.epfl.rigel.util.Fonts;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Represents the screen where the user can see/simulate the sky
 * (the core of the program).
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class StarViewScreen implements Screen {

    /**
     * Holds the font used for buttons, such as the resume/pause button.
     */
    private static final Font BUTTONS_FONT;
    /**
     * Holds the character used in the BUTTONS_FONT font for the play button of the animator.
     */
    private static final String PLAY_CHARACTER = "\uf04b";
    /**
     * Holds the character used in the BUTTONS_FONT font for the pause button of the animator.
     */
    private static final String PAUSE_CHARACTER = "\uf04c";
    /**
     * Holds the character used in the BUTTONS_FONT font for the reset button of the animator.
     */
    private static final String RESET_CHARACTER = "\uf0e2";
    /**
     * Holds the character used in BUTTONS_FONT font for the search tab of the sidebar.
     */
    private static final String SEARCH_CHARACTER = "\uF002";
    /**
     * Holds the character used in BUTTONS_FONT font for the favorites tab of the sidebar.
     */
    private static final String FAVORITES_CHARACTER = "\uF004";
    /**
     * Initial field of view.
     */
    private static final double INIT_FOV = 100d;
    /**
     * Initial coordinates of the user.
     */
    private static final GeographicCoordinates INIT_COORDINATES = GeographicCoordinates.ofDeg(6.57d, 46.52d);
    /**
     * The initial projection center.
     */
    private static final HorizontalCoordinates INIT_PROJ_CENTER = HorizontalCoordinates.ofDeg(180.000000000001d, 15d);
    /**
     * Holds the default longitude for the observer's position.
     */
    private static final Number DEFAULT_LON = 6.57d;
    /**
     * Holds the default latitude for the observer's position.
     */
    private static final Number DEFAULT_LAT = 46.52d;

    static {
        BUTTONS_FONT = Fonts.loadUnsafe("/Font Awesome 5 Free-Solid-900.otf", 15d);
    }

    /**
     * @param checker   a function that returns {@code true} if the passed argument (a double)
     *                  is a valid coordinate.
     * @param converter a converter that converts Strings to numbers
     * @return a filter that only allows valid coordinates.
     */
    private static UnaryOperator<TextFormatter.Change> coordinatesFilter(Function<Double, Boolean> checker,
                                                                         NumberStringConverter converter) {
        return change -> {
            try {
                final String newText =
                        change.getControlNewText();
                final double newLatDeg =
                        converter.fromString(newText).doubleValue();
                return checker.apply(newLatDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        };
    }

    private final StarCatalogue catalogue;
    private final ObserverLocationBean position = new ObserverLocationBean();
    private final DateTimeBean date = new DateTimeBean();
    private final ViewingParametersBean viewingParameters = new ViewingParametersBean();
    private final TimeAnimator animator = new TimeAnimator(date);
    private final SkyCanvasManager manager;
    private final BorderPane root = new BorderPane();

    /**
     * Initializes the "star view" screen --- the core of the program.
     *
     * @param catalogue the catalogue of stars to display.
     */
    public StarViewScreen(StarCatalogue catalogue) {
        this.catalogue = catalogue;
        // initialize beans
        position.setCoordinates(INIT_COORDINATES);
        date.setZonedDateTime(ZonedDateTime.now());
        viewingParameters.setCenter(INIT_PROJ_CENTER);
        viewingParameters.setFieldOfViewDeg(INIT_FOV);

        manager = new SkyCanvasManager(
                catalogue,
                date,
                position,
                viewingParameters
        );

        animator.runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                animator.start();
            } else {
                animator.stop();
            }
        });

        manager.canvas().widthProperty().bind(root.widthProperty());
        manager.canvas().heightProperty().bind(root.heightProperty());
        root.setTop(controlBar());
        root.setCenter(new Pane(manager.canvas()));
        root.setRight(sideBar());
        root.setBottom(bottomPane());
    }

    @Override
    public String getName() {
        return ScreenNames.STAR_VIEW_SCREEN;
    }

    @Override
    public Pane getPane() {
        return root;
    }

    @Override
    public void onChange() {
        manager.canvas().requestFocus();
    }

    /**
     * @return the bottom pane of the window containing the object under the mouse,
     * the field of view and the corresponding horizontal coordinates of the mouse.
     */
    private BorderPane bottomPane() {
        // set up the celestial object under mouse
        final Text underMouse = new Text("");
        underMouse.textProperty().bind(Bindings.createStringBinding(() -> {
            final CelestialObject object = manager.objectUnderMouseProperty().get();
            return object == null ? "" : object.info();
        }, manager.objectUnderMouseProperty()));
        final BorderPane bottom = new BorderPane(underMouse);
        bottom.setStyle("-fx-padding: 4; -fx-background-color: #ffffff;");
        bottom.setCenter(underMouse);
        // set up the field of view text
        final Text fov = new Text("");
        fov.textProperty().bind(
                Bindings.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParameters.fieldOfViewProperty())
        );
        bottom.setLeft(fov);
        // set up the mouse position in horizontal coordinates text
        final Text mouseHorizontal = new Text("");
        mouseHorizontal.textProperty().bind(
                Bindings.format(
                        Locale.ROOT, "Azimut : %.2f°, hauteur : %.2f°",
                        manager.mouseAzimuthProperty(),
                        manager.mouseAltitudeProperty()
                )
        );
        bottom.setRight(mouseHorizontal);
        return bottom;
    }

    /**
     * @return the upper control bar containing the time box and the animator box.
     */
    private HBox controlBar() {
        final HBox controlBar = new HBox(createPositionBox(), new Separator(Orientation.VERTICAL),
                createTimeBox(), new Separator(Orientation.VERTICAL), createAnimatorBox());
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    /**
     * @return the animator box containing the animator selection (speed of simulation),
     * the resume/pause button and the reset button.
     */
    private HBox createAnimatorBox() {
        // set up the animator choice button
        final ChoiceBox<NamedTimeAccelerator> animatorChoice = new ChoiceBox<>();
        animatorChoice.setItems(FXCollections.observableList(Arrays.asList(NamedTimeAccelerator.values())));
        animatorChoice.valueProperty().addListener((observable, oldValue, newValue) ->
                animator.setAccelerator(newValue.getAccelerator()));
        animatorChoice.setValue(NamedTimeAccelerator.TIMES_300);
        final HBox chooseAnimator = new HBox(animatorChoice);
        chooseAnimator.setStyle("-fx-spacing: inherit;");
        // set up the play button
        final Button play = new Button(PLAY_CHARACTER);
        play.setOnMouseClicked(event -> {
            if (animator.runningProperty().get()) {
                animator.stop();
            } else {
                animator.start();
            }
        });
        play.setFont(BUTTONS_FONT);
        play.textProperty().bind(
                Bindings.when(animator.runningProperty()).then(PAUSE_CHARACTER).otherwise(PLAY_CHARACTER)
        );
        // set up the reset button
        final Button reset = new Button(RESET_CHARACTER);
        reset.setOnMouseClicked(event -> date.setZonedDateTime(ZonedDateTime.now()));
        reset.disableProperty().bind(animator.runningProperty());
        chooseAnimator.disableProperty().bind(animator.runningProperty());
        reset.setFont(BUTTONS_FONT);
        // finally, assemble all the elements
        final HBox animatorBox = new HBox(chooseAnimator, reset, play);
        animatorBox.setStyle("-fx-spacing: inherit;");
        return animatorBox;
    }

    /**
     * @return the time box, allowing to change the date, time and zone.
     */
    private HBox createTimeBox() {
        // Date setup
        final Label dateLabel = new Label("Date :");
        final DatePicker dateText = new DatePicker();
        dateText.valueProperty().bindBidirectional(date.dateProperty());
        dateText.setStyle("-fx-pref-width: 120;");

        // Hour setup
        final Label hourLabel = new Label("Heure :");
        final TextField hourText = new TextField();
        hourText.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");

        // text formatters for date and hour
        final DateTimeFormatter hmsFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");
        final LocalTimeStringConverter stringConverter =
                new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        final TextFormatter<LocalTime> timeFormatter =
                new TextFormatter<>(stringConverter);

        hourText.setTextFormatter(timeFormatter);
        timeFormatter.valueProperty().bindBidirectional(date.timeProperty());

        final List<String> zonesAvailable = new ArrayList<>(ZoneId.getAvailableZoneIds());
        // alphabetical (default) order
        Collections.sort(zonesAvailable);
        final ComboBox<String> zonesChoice = new ComboBox<>(FXCollections.observableList(zonesAvailable));
        zonesChoice.setStyle("-fx-pref-width: 180;");

        // update zone changes without binding it, because otherwise the animator
        // will not be able to change the value
        zonesChoice.valueProperty().addListener((observable, oldValue, newValue) -> date.zoneProperty().set(ZoneId.of(newValue)));
        zonesChoice.valueProperty().set(date.getZone().getId()); // set up initial value to avoid the blank box

        dateText.disableProperty().bind(animator.runningProperty());
        hourText.disableProperty().bind(animator.runningProperty());
        zonesChoice.disableProperty().bind(animator.runningProperty());

        final HBox timeBox = new HBox(dateLabel, dateText, hourLabel, hourText, zonesChoice);
        timeBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");
        return timeBox;
    }

    /**
     * @return the position box, allowing to change user's position on Earth.
     */
    private HBox createPositionBox() {
        final Label lon = new Label("Longitude (°) :");
        final Label lat = new Label("Latitude (°) :");
        final TextField lonVal = new TextField();
        final TextField latVal = new TextField();
        lonVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");
        latVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");

        // text formatters for longitude and latitude
        final NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        // longitude
        final UnaryOperator<TextFormatter.Change> lonFilter = coordinatesFilter(GeographicCoordinates::isValidLatDeg,
                stringConverter);
        final TextFormatter<Number> lonTextFormatter =
                new TextFormatter<>(stringConverter, DEFAULT_LON, lonFilter);

        // latitude
        final UnaryOperator<TextFormatter.Change> latFilter = coordinatesFilter(GeographicCoordinates::isValidLatDeg,
                stringConverter);
        final TextFormatter<Number> latTextFormatter =
                new TextFormatter<>(stringConverter, DEFAULT_LAT, latFilter);

        lonVal.setTextFormatter(lonTextFormatter);
        latVal.setTextFormatter(latTextFormatter);
        position.longitudeProperty().bind(lonTextFormatter.valueProperty());
        position.latitudeProperty().bind(latTextFormatter.valueProperty());

        final HBox posControl = new HBox(lon, lonVal, lat, latVal);
        posControl.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");

        return posControl;
    }

    /**
     * @return the sidebar that allows searching and adding to favorites
     * the available CelestialObjects.
     */
    private VBox sideBar() {
        // setup the sidebar (slide in, slide out)
        final VBox menu = new VBox();
        menu.getStyleClass().add("sidebar");
        menu.prefHeightProperty().bind(root.heightProperty());
        menu.setPrefWidth(300d);
        menu.setTranslateX(280d);
        final TranslateTransition menuTranslation = new TranslateTransition(Duration.millis(500d), menu);
        menuTranslation.setFromX(280d);
        menuTranslation.setToX(0);
        menu.setOnMouseEntered(event -> {
            menuTranslation.setRate(1d);
            menuTranslation.play();
        });
        menu.setOnMouseExited(event -> {
            menuTranslation.setRate(-1d);
            menuTranslation.play();
        });

        // search tab
        final Tab searchTab = new Tab();
        searchTab.setClosable(false);
        searchTab.getStyleClass().add("sidebar-tab");
        final Text searchTabTitle = new Text(SEARCH_CHARACTER);
        searchTabTitle.setFont(BUTTONS_FONT);
        searchTab.setGraphic(searchTabTitle);

        final List<Star> stars = catalogue.stars().subList(0, 20);
        // this was first done with a map() call, React-like style,
        // but we changed this to a C-style for loop to add easily
        // vertical separators
        final List<Node> starComponents = new ArrayList<>(2 * stars.size() - 1);
        for (int i = 0; i < stars.size(); i++) {
            final Star s = stars.get(i);
            final VBox card = new VBox();
            card.getChildren().addAll(new Text("Nom de l'objet : " + s.name()),
                    new Text("Identifiant Hipparcos : " + s.hipparcosId()));
            card.getStyleClass().add("sidebar-star-card");
            starComponents.add(card);
            if (i != stars.size() - 1) {
                final Separator sep = new Separator(Orientation.HORIZONTAL);
                sep.prefWidthProperty().bind(menu.widthProperty());
                starComponents.add(sep);
            }
        }
        final VBox elements = new VBox();
        elements.getChildren().addAll(starComponents);
        searchTab.setContent(new ScrollPane(elements));

        final Tab favoritesTab = new Tab();
        favoritesTab.getStyleClass().add("sidebar-tab");
        favoritesTab.setClosable(false);
        final Text favoritesTabTitle = new Text(FAVORITES_CHARACTER);
        favoritesTabTitle.setFont(BUTTONS_FONT);
        favoritesTab.setGraphic(favoritesTabTitle);

        // finally, add everything
        final TabPane tabPane = new TabPane(searchTab, favoritesTab);
        menu.getChildren().add(tabPane);

        return menu;
    }

}
