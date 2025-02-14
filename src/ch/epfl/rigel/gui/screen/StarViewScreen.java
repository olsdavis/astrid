package ch.epfl.rigel.gui.screen;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.gui.*;
import ch.epfl.rigel.storage.FavoritesList;
import ch.epfl.rigel.util.Texts;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static ch.epfl.rigel.util.Fonts.ICONS_FONT;

/**
 * Represents the screen where the user can see/simulate the sky
 * (the core of the program).
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class StarViewScreen extends Screen {

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
     * Holds the character used in BUTTONS_FONT font for the favorites tab of the sidebar
     * and the "add to favorites" button.
     */
    private static final String FAVORITES_CHARACTER = "\uF004";
    /**
     * Holds the character used in BUTTONS_FONT font for the "target object" button.
     */
    private static final String TARGET_CHARACTER = "\uF05B";
    /**
     * Holds the character used in BUTTONS_FONT font for the button that allows toggling
     * the sidebar view.
     */
    private static final String SIDEBAR_CHARACTER = "\uF0AE";
    /**
     * Holds the width of the side bar.
     */
    private static final double SIDEBAR_WIDTH = 300d;
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
    /**
     * Holds the number of elements per page when using the search tab.
     */
    private static final int ELEMENTS_PER_PAGE = 20;

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
    private final DisplayParametersBean displayParameters = new DisplayParametersBean();
    private final TimeAnimator animator = new TimeAnimator(date);
    private final SkyCanvasManager manager;
    // the following list holds the objects that the user will see in his search tab
    // we do not use here an ObservableList, because we often need to modify the entire
    // collection, and we do that by using the set() method (to update the held value)
    private final SimpleObjectProperty<List<CelestialObject>> searchObjects = new SimpleObjectProperty<>();
    private final List<CelestialObject> allObjects;
    private final FavoritesList favoritesList;
    private final BorderPane mainPane = new BorderPane();
    private final BorderPane finalPane = new BorderPane();

    /**
     * Initializes the "star view" screen --- the core of the program.
     *
     * @param catalogue     the catalogue of stars to display.
     * @param favoritesList the list of favorites ({@code null} if it could not have been initialized)
     */
    public StarViewScreen(StarCatalogue catalogue, FavoritesList favoritesList) {
        super(ScreenNames.STAR_VIEW_SCREEN);
        this.catalogue = catalogue;
        this.favoritesList = favoritesList;
        // initialize beans
        position.setCoordinates(INIT_COORDINATES);
        date.setZonedDateTime(ZonedDateTime.now());
        viewingParameters.setCenter(INIT_PROJ_CENTER);
        viewingParameters.setFieldOfViewDeg(INIT_FOV);

        final List<CelestialObject> all = new ArrayList<>();
        // give dummy values, we are not interested in them for the display (EqCoordinates, angular size, magnitude...)
        all.add(new Sun(EclipticCoordinates.of(0, 0), EquatorialCoordinates.of(0, 0), 0f, 0f));
        all.add(new Moon(EquatorialCoordinates.of(0, 0), 0f, 0f, 0f));
        all.addAll(PlanetModel.ALL.stream()
                .filter(p -> p != PlanetModel.EARTH)
                .map(PlanetModel::empty)
                .collect(Collectors.toList()));
        all.addAll(catalogue.stars());
        allObjects = Collections.unmodifiableList(all);

        manager = new SkyCanvasManager(
                catalogue,
                date,
                position,
                viewingParameters,
                displayParameters
        );

        searchObjects.set(allObjects);

        manager.canvas().widthProperty().bind(mainPane.widthProperty());
        manager.canvas().heightProperty().bind(mainPane.heightProperty());
        final VBox sideBar = sideBar();
        mainPane.setTop(controlBar(sideBar));
        mainPane.setCenter(new Pane(manager.canvas()));
        mainPane.setRight(sideBar);
        mainPane.setBottom(bottomPane());
        finalPane.setCenter(mainPane);
        finalPane.setTop(createMenu());
    }

    @Override
    public Pane getPane() {
        return finalPane;
    }

    @Override
    public void onEnter() {
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
    private HBox controlBar(VBox sideBar) {
        final HBox controlBar = new HBox(createPositionBox(), new Separator(Orientation.VERTICAL),
                createTimeBox(), new Separator(Orientation.VERTICAL), createAnimatorBox(),
                new Separator(Orientation.VERTICAL), createSideBarButton(sideBar));
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
        play.setFont(ICONS_FONT);
        play.textProperty().bind(
                Bindings.when(animator.runningProperty()).then(PAUSE_CHARACTER).otherwise(PLAY_CHARACTER)
        );
        // set up the reset button
        final Button reset = new Button(RESET_CHARACTER);
        reset.setOnAction(event -> {
            date.setZonedDateTime(ZonedDateTime.now());
            viewingParameters.setCenter(INIT_PROJ_CENTER);
        });
        reset.disableProperty().bind(animator.runningProperty());
        chooseAnimator.disableProperty().bind(animator.runningProperty());
        reset.setFont(ICONS_FONT);
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

        final List<ZoneId> zonesAvailable = new ArrayList<>(
                ZoneId.getAvailableZoneIds()
                        .stream()
                        .sorted()
                        .map(ZoneId::of)
                        .collect(Collectors.toUnmodifiableList())
        );
        final ComboBox<ZoneId> zonesChoice = new ComboBox<>(FXCollections.observableList(zonesAvailable));
        zonesChoice.setStyle("-fx-pref-width: 180;");

        zonesChoice.valueProperty().bindBidirectional(date.zoneProperty());

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
        final UnaryOperator<TextFormatter.Change> lonFilter = coordinatesFilter(GeographicCoordinates::isValidLonDeg,
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
     * @param sideBar the side bar (used for toggling its display)
     * @return a VBox containing the button that toggles the display
     * of the side bar.
     */
    private VBox createSideBarButton(VBox sideBar) {
        final VBox box = new VBox();
        final Button button = new Button(SIDEBAR_CHARACTER);
        button.setFont(ICONS_FONT);
        button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (sideBar.getTranslateX() == 0d) { // if not moved
                    sideBar.setTranslateX(SIDEBAR_WIDTH);
                } else {
                    sideBar.setTranslateX(0d);
                }
            }
        });
        box.getChildren().add(button);
        return box;
    }

    /**
     * @return the sidebar that allows searching and adding to favorites
     * the available CelestialObjects.
     */
    private VBox sideBar() {
        // setup the sidebar (slide in, slide out)
        final VBox menu = new VBox();
        menu.getStyleClass().add("sideBar");
        menu.prefHeightProperty().bind(mainPane.heightProperty());
        menu.setPrefWidth(SIDEBAR_WIDTH);
        menu.setTranslateX(SIDEBAR_WIDTH);

        // finally, add everything
        final TabPane tabPane = new TabPane(searchTab(menu), favoritesTab(menu));
        menu.getChildren().add(tabPane);

        return menu;
    }

    /**
     * @param menu the menu that contains this tab
     * @return the search tab used to display the catalogue searches, etc.
     */
    private Tab searchTab(VBox menu) {
        final Tab searchTab = new Tab();
        searchTab.setClosable(false);
        searchTab.getStyleClass().add("sideBar-tab");
        final Text searchTabTitle = new Text(SEARCH_CHARACTER);
        searchTabTitle.setFont(ICONS_FONT);
        searchTab.setGraphic(searchTabTitle);

        final Pagination pagination = new Pagination();
        pagination.pageCountProperty().bind(
                Bindings.createIntegerBinding(
                        () -> Math.max((int) Math.ceil((float) searchObjects.get().size() / ELEMENTS_PER_PAGE), 1),
                        searchObjects
                )
        );
        pagination.pageFactoryProperty().bind(
                Bindings.createObjectBinding(() -> (index) -> {
                    if (searchObjects.get().isEmpty()) {
                        return new Text("Aucun résultat");
                    }
                    return makeListContent(searchObjects.get().subList(index * ELEMENTS_PER_PAGE,
                            Math.min((index + 1) * ELEMENTS_PER_PAGE, searchObjects.get().size())), menu);
                }, searchObjects)
        );

        final TextField search = new TextField();
        search.setOnKeyTyped(e -> search.requestFocus());
        search.setPromptText("Recherche...");
        searchObjects.bind(
                Bindings.createObjectBinding(() -> {
                    if (search.getText().isBlank()) {
                        return allObjects;
                    } else {
                        final String lowered = search.getText().toLowerCase();
                        return allObjects.stream()
                                .filter(s -> s.name().toLowerCase().contains(lowered)) // simple criterion
                                .collect(Collectors.toList());
                    }
                }, search.textProperty())
        );
        final BorderPane lastPane = new BorderPane();
        lastPane.setTop(search);
        lastPane.setCenter(pagination);
        searchTab.setContent(lastPane);
        return searchTab;
    }

    /**
     * @return the tab containing the favorite elements of the user.
     */
    private Tab favoritesTab(VBox menu) {
        // generate favorites tab
        final Tab favoritesTab = new Tab();
        favoritesTab.getStyleClass().add("sideBar-tab");
        favoritesTab.setClosable(false);
        final Text favoritesTabTitle = new Text(FAVORITES_CHARACTER);
        favoritesTabTitle.setFont(ICONS_FONT);
        favoritesTab.setGraphic(favoritesTabTitle);

        final BorderPane contentPane = new BorderPane();
        contentPane.centerProperty().bind(Bindings.createObjectBinding(() -> {
            if (favoritesList.isEmpty()) {
                final Text text = new Text("Aucun favori.\n Cliquez sur un coeur à côté\n d'un objet pour l'ajouter.");
                text.setTextAlignment(TextAlignment.CENTER);
                return text;
            }
            return makeListContent(favoritesList.favoritesProperty()
                    .stream()
                    .map(c -> {
                        switch (c.getType()) {
                            case STAR:
                                for (Star star : catalogue.stars()) {
                                    // we use equals, here, to avoid unchecked casts
                                    if (c.getIdentifier().equals(FavoritesList.identify(star))) {
                                        return star;
                                    }
                                }
                                break;
                            case PLANET:
                                for (PlanetModel planet : PlanetModel.values()) {
                                    if (planet.getFrenchName().equals(c.getIdentifier())) {
                                        return planet.empty();
                                    }
                                }
                                break;
                            case SUN:
                                return new Sun(EclipticCoordinates.of(0, 0), EquatorialCoordinates.of(0, 0), 0f, 0f);
                            case MOON:
                                return new Moon(EquatorialCoordinates.of(0, 0), 0f, 0f, 0f);
                        }
                        return null; // should not be reached, since all types are covered
                        // and we store correctly our data
                    })
                    .collect(Collectors.toUnmodifiableList()), menu);
        }, favoritesList.favoritesProperty()));
        favoritesTab.setContent(contentPane);
        return favoritesTab;
    }

    /**
     * @param objects the collection to display
     * @param menu    the menu that will hold this data
     * @return a pane containing all the provided content.
     */
    private ScrollPane makeListContent(List<CelestialObject> objects, VBox menu) {
        // this was first done with a map() call, React-like style,
        // but we changed this to a C-style for loop to easily add
        // vertical separators
        final List<Node> starComponents = new ArrayList<>(2 * objects.size() - 1);
        // generate all components
        for (int i = 0; i < objects.size(); ++i) {
            final CelestialObject s = objects.get(i);
            if (s == null) {
                continue; // to avoid unexpected NullPointerExceptions
                // these never occur, and never should occur, but it is
                // here to make sure
            }
            final VBox card = new VBox();
            final BorderPane firstLine = new BorderPane();
            // setup target button
            final Button targetButton = new Button(TARGET_CHARACTER);
            targetButton.setFont(ICONS_FONT);
            targetButton.setFocusTraversable(false);
            targetButton.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (!manager.focus(s)) {
                        new Alert(Alert.AlertType.INFORMATION, "L'objet n'est pas actuellement visible !").show();
                    }
                    e.consume();
                }
            });
            // setup add to favorites button
            final Button favoriteButton = new Button(FAVORITES_CHARACTER);
            if (favoritesList.contains(s)) {
                favoriteButton.setTextFill(Color.RED);
            }
            favoriteButton.setFont(ICONS_FONT);
            // the following line allowed us to prevent the favorite button
            // to set the focus on the "longitude" field, whenever it was clicked
            // in the favorites tab. This problem occurred because of the fact
            // that this same item is, after the click, removed
            favoriteButton.setFocusTraversable(false);
            favoriteButton.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (favoritesList.contains(s)) {
                        favoritesList.remove(s);
                        favoriteButton.setTextFill(Color.BLACK);
                    } else {
                        favoritesList.add(s);
                        favoriteButton.setTextFill(Color.RED);
                    }
                    menu.requestFocus();
                    e.consume();
                }
            });
            // add the text info
            firstLine.setLeft(Texts.parse("*Nom de l'objet* : " + s.name()));
            firstLine.setRight(new HBox(targetButton, favoriteButton));
            card.getChildren().add(firstLine);
            if (s.getType() == CelestialObject.Type.STAR) {
                card.getChildren().add(Texts.parse("*Identifiant Hipparcos* : " +
                        ((Star) s).hipparcosId()));
            }
            card.getStyleClass().add("sideBar-star-card");
            starComponents.add(card);
            // do not add a separator after the last item
            if (i != objects.size() - 1) {
                final Separator sep = new Separator(Orientation.HORIZONTAL);
                sep.prefWidthProperty().bind(menu.widthProperty());
                starComponents.add(sep);
            }
        }
        final VBox elements = new VBox();
        elements.getChildren().addAll(starComponents);
        final ScrollPane pane = new ScrollPane(elements) {
            @Override
            public void requestFocus() {
                /*
                we disallow the scroll pane to request the focus
                because of the same problem of the favorites button:
                the focusTraversable property set to false fixed the problem,
                except when the pane was focused; it then produced the same problem
                of focus change
                */
            }
        };
        pane.setFitToWidth(true);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        pane.prefHeightProperty().bind(menu.heightProperty());
        return pane;
    }

    /**
     * @return the top menu bar for some miscellaneous settings.
     */
    private MenuBar createMenu() {
        final Menu menu = new Menu("Affichage");
        menu.getItems().addAll(
                createMenuItem("Astérismes", displayParameters.displayAsterismsProperty()),
                createMenuItem("Étoiles", displayParameters.displayStarsProperty()),
                createMenuItem("Ligne d'horizon", displayParameters.displayHorizonProperty()),
                createMenuItem("Planètes", displayParameters.displayPlanetsProperty()),
                createMenuItem("Soleil", displayParameters.displaySunProperty()),
                createMenuItem("Lune", displayParameters.displayMoonProperty())
        );
        return new MenuBar(menu);
    }

    /**
     * @param name     the name of the item
     * @param property the property associated to the item
     * @return a {@link MenuItem} for the top menu that toggles displayed properties.
     */
    private MenuItem createMenuItem(String name, BooleanProperty property) {
        final CheckMenuItem item = new CheckMenuItem(name);
        item.setSelected(true); // default value
        property.bind(item.selectedProperty());
        return item;
    }

}
