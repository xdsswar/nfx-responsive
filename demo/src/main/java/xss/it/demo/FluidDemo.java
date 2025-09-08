/*
 * Copyright © 2025. XTREME SOFTWARE SOLUTIONS
 *
 * All rights reserved. Unauthorized use, reproduction, or distribution
 * of this software or any portion of it is strictly prohibited and may
 * result in severe civil and criminal penalties. This code is the sole
 * proprietary of XTREME SOFTWARE SOLUTIONS.
 *
 * Commercialization, redistribution, and use without explicit permission
 * from XTREME SOFTWARE SOLUTIONS, are expressly forbidden.
 */

package xss.it.demo;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import xss.it.nfx.responsive.layout.NfxFluidPane;

import java.util.*;
import java.util.stream.Collectors;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.demo package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 07, 2025
 * <p>
 * Created on 09/07/2025 at 13:56
 */
public class FluidDemo extends Application {

    /**
     * The entry point of the Java application.
     * This method calls the launch method to start a JavaFX application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method is called after the application has been launched.
     * Override this method to create and set up the primary stage of the application.
     *
     * @param stage The primary stage for this application, onto which
     *              the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        // ---------- Controls ----------
        Slider widthSlider = new Slider(360, 1600, 1100);
        widthSlider.setBlockIncrement(40);
        widthSlider.setMajorTickUnit(200);
        widthSlider.setShowTickMarks(true);
        widthSlider.setShowTickLabels(true);

        Label widthLabel = new Label("Container width:");
        widthLabel.setStyle("-fx-font-size: 16;");

        Button shuffle = new Button("Shuffle");
        shuffle.setStyle("-fx-font-size: 16; -fx-padding: 6 12 6 12;");

        // NEW: toggle orientation button
        Button toggleDir = new Button("RTL: OFF");
        toggleDir.setStyle("-fx-font-size: 16; -fx-padding: 6 12 6 12;");

        Label info = new Label();
        info.setStyle("-fx-font-size: 15; -fx-opacity: .9; -fx-font-weight: bold;");

        HBox controls = new HBox(14, widthLabel, widthSlider, shuffle, toggleDir, info);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10, 14, 10, 14));
        controls.getStyleClass().add("topbar");

        // ---------- Grid ----------
        NfxFluidPane grid = new NfxFluidPane();
        grid.getStyleClass().add("nfx-grid");
        grid.setMaxWidth(Region.USE_PREF_SIZE);
        grid.prefWidthProperty().bind(widthSlider.valueProperty());

        // Boxes with varied spans/offsets; all share the same fixed height
        List<StackPane> boxes = makeBoxes();
        grid.getChildren().setAll(boxes);

        // Live readout (now also shows orientation)
        info.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String bp = grid.getCurrentBreakpoint();
                    String w  = String.format("%.0f", grid.getWidth());
                    String orient = (grid.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) ? "RTL" : "LTR";
                    return "BP=" + bp + " • W=" + w + " • " + orient;
                },
                grid.widthProperty(), grid.currentBreakpointProperty(), grid.nodeOrientationProperty()
        ));

        // Shuffle order
        shuffle.setOnAction(e -> {
            List<Node> copy = new ArrayList<>(grid.getChildren());
            Collections.shuffle(copy);
            grid.getChildren().setAll(copy);
            grid.requestLayout();
        });

        // NEW: toggle grid node orientation
        toggleDir.setOnAction(e -> {
            boolean toRTL = grid.getEffectiveNodeOrientation() != NodeOrientation.RIGHT_TO_LEFT;
            grid.setNodeOrientation(toRTL ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
            toggleDir.setText(toRTL ? "RTL: ON" : "RTL: OFF");
            grid.requestLayout();
        });

        // ---------- Layout root ----------
        ScrollPane scroller = new ScrollPane(grid);
        scroller.setFitToWidth(false);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        BorderPane root = new BorderPane(scroller);
        root.setTop(controls);

        Scene scene = new Scene(root, 1280, 860);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/responsive.css")).toExternalForm()
        );

        stage.setTitle("NfxFluidPane • Stress Test (uniform height, spans & offsets)");
        stage.setScene(scene);

        widthSlider.maxProperty().bind(stage.widthProperty());
        stage.show();
    }


    /** Single fixed height for all tiles. */
    private static final double BOX_H = 140;

    /** ~22 boxes covering spans/offsets across breakpoints. All use BOX_H. */
    private List<StackPane> makeBoxes() {
        List<StackPane> list = new ArrayList<>();

        list.add(box("A", "box", "s-xs-12","s-sm-12","s-md-6","s-lg-4","s-xl-3","s-xxl-3"));
        list.add(box("B", "box", "s-xs-12","s-sm-12","s-md-4","s-lg-3","s-xl-3","s-xxl-2","o-lg-1" , "test"));
        list.add(box("C", "box", "s-xs-12","s-sm-6","s-md-6","s-lg-6","s-xl-4","s-xxl-4"));
        list.add(box("D", "box", "s-xs-12","s-sm-6","s-md-4","s-lg-4","s-xl-4","s-xxl-3","o-md-2"));
        list.add(box("E", "box", "s-xs-12","s-sm-12","s-md-12","s-lg-8","s-xl-6","s-xxl-6","o-lg-2", "test"));
        list.add(box("F", "box", "s-xs-6","s-sm-6","s-md-6","s-lg-3","s-xl-3","s-xxl-3"));
        list.add(box("G", "box", "s-xs-6","s-sm-6","s-md-4","s-lg-4","s-xl-3","s-xxl-2","o-xl-1"));
        list.add(box("H", "box", "s-xs-12","s-sm-12","s-md-8","s-lg-8","s-xl-6","s-xxl-6"));
        list.add(box("I", "box", "s-xs-12","s-sm-12","s-md-3","s-lg-3","s-xl-3","s-xxl-3","o-md-0","o-lg-0","o-xl-0"));
        list.add(box("J", "box", "s-xs-12","s-sm-12","s-md-3","s-lg-3","s-xl-3","s-xxl-3","o-md-3", "test"));

        // offsets@md sweep 0..5 (same span)
        list.add(box("K off0", "box", "s-xs-12","s-md-3","o-md-0"));
        list.add(box("L off1", "box", "s-xs-12","s-md-3","o-md-1"));
        list.add(box("M off2", "box", "s-xs-12","s-md-3","o-md-2"));
        list.add(box("N off3", "box", "s-xs-12","s-md-3","o-md-3"));
        list.add(box("O off4", "box", "s-xs-12","s-md-3","o-md-4"));
        list.add(box("P off5", "box", "s-xs-12","s-md-3","o-md-5"));

        // tiny spans to test packing
        list.add(box("Q span1", "box", "s-xs-6","s-md-1","s-lg-1","s-xl-1"));
        list.add(box("R span2", "box", "s-xs-6","s-md-2","s-lg-2","s-xl-2"));
        list.add(box("S span4", "box", "s-xs-12","s-md-4","s-lg-4"));
        list.add(box("T span5", "box", "s-xs-12","s-md-5","s-lg-5"));
        list.add(box("U span7 off2", "box", "s-xs-12","s-md-7","o-md-2"));
        list.add(box("V span9 off1", "box", "s-xs-12","s-md-9","o-md-1"));

        return list;
    }

    /** A colored box with larger label text showing current span/offset dynamically. */
    private StackPane box(String name, String... styleClasses) {
        StackPane p = new StackPane();
        p.getStyleClass().addAll(styleClasses);
        p.setMinHeight(BOX_H);
        p.setPrefHeight(BOX_H);
        p.setMaxHeight(Region.USE_PREF_SIZE);
        p.getProperties().put("baseH", BOX_H);

        VBox inner = new VBox(6);
        inner.setAlignment(Pos.CENTER);

        Label title = new Label(name);
        title.setStyle("-fx-font-size: 18; -fx-font-weight: 800;");

        Label details = new Label();
        details.setStyle("-fx-font-size: 14; -fx-opacity: 0.95;");

        inner.getChildren().addAll(title, details);
        p.getChildren().add(inner);

        // Bind details once parent is the NfxFluidPane, then keep it live on breakpoint changes.
        p.parentProperty().addListener((obs, oldParent, newParent) -> bindDetails(details, p, newParent));
        bindDetails(details, p, p.getParent());
        p.getStyleClass().addAll("box", "w-responsive-card", "m-comfy");

        Tooltip t = new Tooltip();
        t.setStyle("-fx-font-size: 15;");
        t.textProperty().bind(details.textProperty());
        Tooltip.install(p, t);

        return p;
    }

    private void bindDetails(Label details, StackPane p, Parent parent) {
        details.textProperty().unbind();
        if (parent instanceof NfxFluidPane container) {
            details.textProperty().bind(Bindings.createStringBinding(() -> {
                        String bp = container.getCurrentBreakpoint(); // "xs","sm","md","lg","xl","xxl"
                        int span = switch (bp) {
                            case "xs" -> NfxFluidPane.getXsColSpan(p);
                            case "sm" -> NfxFluidPane.getSmColSpan(p);
                            case "md" -> NfxFluidPane.getMdColSpan(p);
                            case "lg" -> NfxFluidPane.getLgColSpan(p);
                            case "xl" -> NfxFluidPane.getXlColSpan(p);
                            case "xxl"-> NfxFluidPane.getXxlColSpan(p);
                            default   -> 0;
                        };
                        int off = switch (bp) {
                            case "xs" -> NfxFluidPane.getXsColOffset(p);
                            case "sm" -> NfxFluidPane.getSmColOffset(p);
                            case "md" -> NfxFluidPane.getMdColOffset(p);
                            case "lg" -> NfxFluidPane.getLgColOffset(p);
                            case "xl" -> NfxFluidPane.getXlColOffset(p);
                            case "xxl"-> NfxFluidPane.getXxlColOffset(p);
                            default   -> 0;
                        };
                        return "bp=" + bp + " • span=" + span + " • off=" + off;
                    },
                    // re-evaluate when the container declares a new breakpoint OR when width changes
                    container.currentBreakpointProperty(), container.widthProperty()
            ));
        } else {
            details.setText("");
        }
    }


    /**
     * The initialization method for the application.
     * This method is called immediately after the application class is loaded and
     * constructed. An application can override this method to perform initialization
     * tasks before the application is shown.
     *
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void init() throws Exception {
        super.init();
    }

    /**
     * This method is called when the application should stop, and provides a
     * convenient place to prepare for application exit and destroy resources.
     *
     * @throws Exception if an error occurs during stopping the application.
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
