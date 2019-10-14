package cuie.nightbehaviour.demo;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import cuie.nightbehaviour.HappinessIndexControl;

/**
 *
 */
public class DemoPane extends BorderPane {

    private final DemoPM pm;

    // declare the custom control
    private HappinessIndexControl cc;

    // all controls you need to show the features of the custom control
    private Slider slider;

    public DemoPane(DemoPM pm) {
        this.pm = pm;
        initializeControls();
        layoutControls();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        cc = new HappinessIndexControl();

        slider = new Slider(0.0,1.0,0.5);
        slider.setShowTickLabels(true);
    }

    private void layoutControls() {
        VBox controlPane = new VBox(new Label("SimpleControl Properties"),
                                    slider);
        controlPane.setPadding(new Insets(0, 50, 0, 50));
        controlPane.setSpacing(10);

        setCenter(cc);
        setRight(controlPane);
    }

    private void setupBindings() {
        //bindings for the "demo controls"
        slider.valueProperty().bindBidirectional(pm.someValueProperty());


        //bindings for the Custom Control
        cc.valueProperty().bindBidirectional(pm.someValueProperty());
    }

}
