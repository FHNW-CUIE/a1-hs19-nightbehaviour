package cuie.nightbehaviour;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

/**
 * Mit dem Happiness-Index Control soll ein "Glücklichkeits-Grad" zwischen 0 und 1 angegeben werden können.
 * Dargestellt wird der Glücklichkeitsgrad durch ein abstrahiertes Smiley welches die Range von Mundwinkel nach unten bis Mundwinkel nach oben hat.
 * Die Double Value kann durch Ziehen am Mund verändert werden.
 *

 * @author Hanna Lisa Franz
 */

public class HappinessIndexControl extends Region {
    private static final double ARTBOARD_WIDTH  = 250;
    private static final double ARTBOARD_HEIGHT = 250;

    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;

    private static final double MINIMUM_WIDTH  = 75;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;

    private static final double MAXIMUM_WIDTH = 1000;
    private static final double BORDER_PADDING = 25;
    private static final double CONTROL_POINT_OFFSET = 55;

    private static final Color RED_COLOR = Color.web("#F34849");
    private static final Color YELLOW_COLOR = Color.web("#FFCE20");
    private static final Color GREEN_COLOR = Color.web("#6CD67E");


    private CubicCurve curve;


    private final DoubleProperty value = new SimpleDoubleProperty();

    // needed for resizing
    private Pane drawingPane;

    public HappinessIndexControl() {
        initializeSelf();
        initializeParts();
        initializeDrawingPane();
        layoutParts();
        setupEventHandlers();
        setupValueChangeListeners();
        setupBinding();
    }

    private void initializeSelf() {
        String fonts = getClass().getResource("/fonts/fonts.css").toExternalForm();
        getStylesheets().add(fonts);

        String stylesheet = getClass().getResource("style.css").toExternalForm();
        getStylesheets().add(stylesheet);

        getStyleClass().add("happiness-index");  // Todo: an den Namen der Klasse (des CustomControls) anpassen
    }

    private void initializeParts() {
        //ToDo: alle deklarierten Parts initialisieren
        double center = ARTBOARD_WIDTH * 0.5;

        double startX = BORDER_PADDING;
        double startY = center;
        double endX = ARTBOARD_WIDTH - BORDER_PADDING;
        double endY = center;

        curve = new CubicCurve(startX, startY, center - CONTROL_POINT_OFFSET, center, center + CONTROL_POINT_OFFSET, center, endX, endY);
        curve.getStyleClass().add("curve");
    }

    private void initializeDrawingPane() {
        drawingPane = new Pane();
        drawingPane.getStyleClass().add("drawing-pane");
        drawingPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
    }

    private void layoutParts() {
        // ToDo: alle Parts zur drawingPane hinzufügen
        drawingPane.getChildren().addAll(curve);

        getChildren().add(drawingPane);
    }

    private void setupEventHandlers() {
        //ToDo: bei Bedarf ergänzen

        curve.setOnMouseDragged((t) -> {
            double mouseY = t.getY();

            if (mouseY > BORDER_PADDING && mouseY < drawingPane.getHeight() - BORDER_PADDING) {
                mouseY -= BORDER_PADDING;
                value.setValue(mouseY / (drawingPane.getHeight() - 2 * BORDER_PADDING));
            }
        });
    }

    private void setupValueChangeListeners() {
        //ToDo: bei Bedarf ergänzen

    }

    private void setupBinding() {
        //ToDo dieses Binding ersetzen

        curve.startYProperty().bind(drawingPane.heightProperty().divide(2));
        curve.endYProperty().bind(drawingPane.heightProperty().divide(2));
        curve.endXProperty().bind(drawingPane.widthProperty().subtract(BORDER_PADDING));

        DoubleBinding controlYValue = Bindings.createDoubleBinding(
                () -> BORDER_PADDING + value.get() * (drawingPane.heightProperty().get() - 2 * BORDER_PADDING),
                value,
                drawingPane.heightProperty()
        );

        curve.controlY1Property().bind(controlYValue);
        curve.controlY2Property().bind(controlYValue);

        curve.strokeProperty().bind(Bindings.createObjectBinding(() -> {
            double absValue = value.getValue();

            if (absValue <= 0.5) {
                return RED_COLOR.interpolate(YELLOW_COLOR, absValue * 2);
            } else {
                return YELLOW_COLOR.interpolate(GREEN_COLOR, (absValue - 0.5) * 2);
            }
        }, value));
    }

    //resize by scaling
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    private void resize() {
        Insets padding         = getPadding();
        double availableWidth  = getWidth() - padding.getLeft() - padding.getRight();
        double availableHeight = getHeight() - padding.getTop() - padding.getBottom();

        double width = Math.max(Math.min(Math.min(availableWidth, availableHeight * ASPECT_RATIO), MAXIMUM_WIDTH), MINIMUM_WIDTH);

        double scalingFactor = width / ARTBOARD_WIDTH;

        if (availableWidth > 0 && availableHeight > 0) {
            drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, (getHeight() - ARTBOARD_HEIGHT) * 0.5);
            drawingPane.setScaleX(scalingFactor);
            drawingPane.setScaleY(scalingFactor);
        }
    }

    // compute sizes

    @Override
    protected double computeMinWidth(double height) {
        Insets padding           = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return MINIMUM_WIDTH + horizontalPadding;
    }

    @Override
    protected double computeMinHeight(double width) {
        Insets padding         = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return MINIMUM_HEIGHT + verticalPadding;
    }

    @Override
    protected double computePrefWidth(double height) {
        Insets padding           = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return ARTBOARD_WIDTH + horizontalPadding;
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets padding         = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return ARTBOARD_HEIGHT + verticalPadding;
    }

    // alle getter und setter  (generiert via "Code -> Generate... -> Getter and Setter)

    // ToDo: ersetzen durch die Getter und Setter Ihres CustomControls
    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }
}
