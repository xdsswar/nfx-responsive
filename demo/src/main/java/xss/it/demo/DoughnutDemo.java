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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import xss.it.nfx.responsive.control.NfxDoughnutChart;
import xss.it.nfx.responsive.misc.DonutData;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
 * @since September 08, 2025
 * <p>
 * Created on 09/08/2025 at 16:02
 */
public class DoughnutDemo extends Application {

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
        NfxDoughnutChart<MyData> chart = new NfxDoughnutChart<>();
        chart.setTitle("NfxDoughnutChart Test");
        chart.setSubtitle("Auto-updating Data and Inner Radius...");

        // look & feel you already use
        chart.setLegendMarkerShape(NfxDoughnutChart.LegendMarkerShape.CIRCLE);
        chart.setInnerRadius(60);       // 0..100 (your property clamps/scales)
        //chart.setShowTitle(false);
        //chart.setShowSubtitle(false);
        // chart.setPopupEnabled(false);

        // initial populate after 1s (animates empty -> data)
        chart.setData(generateRandomData());

        // keep updating forever
        startRandomUpdates(chart);

        StackPane root = new StackPane(chart);
        Scene scene = new Scene(root, 740, 540);
        stage.setTitle("NfxDoughnutChart Demo (Live Updates)");
        stage.setScene(scene);
        stage.show();
    }

    /* =================== helpers =================== */

    private static final String[] NAME_POOL = {
            "Alpha","Beta","Gamma","Delta","Epsilon","Zeta","Eta","Theta","Iota","Kappa",
            "Lambda","Mu","Nu","Xi","Omicron","Pi","Rho","Sigma","Tau","Upsilon","Phi","Chi","Psi","Omega"
    };

    private final java.util.Random rnd = new java.util.Random();


    private ObservableList<MyData> generateRandomData() {
        int count = 3 + rnd.nextInt(6); // 3..8
        Set<String> used = new LinkedHashSet<>();
        List<MyData> items = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            String name = uniqueName(used, i);
            // random value in a reasonable range
            double value = 5 + rnd.nextDouble() * 60; // 5..65
            items.add(new MyData(name, value));
        }
        return FXCollections.observableArrayList(items);
    }


    private String uniqueName(Set<String> used, int indexHint) {
        // try a few random picks from pool
        for (int k = 0; k < 10; k++) {
            String candidate = NAME_POOL[rnd.nextInt(NAME_POOL.length)];
            if (used.add(candidate)) return candidate;
        }
        // fallback: deterministic from pool + suffix to ensure uniqueness
        String base = NAME_POOL[indexHint % NAME_POOL.length];
        String candidate = base;
        int suffix = 2;
        while (!used.add(candidate)) candidate = base + " " + suffix++;
        return candidate;
    }


    private void startRandomUpdates(NfxDoughnutChart<MyData> chart) {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(6.0), e -> {
            if (rnd.nextDouble() < 0.25) {
                chart.setData(FXCollections.observableArrayList()); // empty -> tests your animate-out path
            } else {
                chart.setData(generateRandomData());
            }
            if (rnd.nextDouble() < 0.5) {
                int inner = 30 + rnd.nextInt(51); // 30..80
                chart.setInnerRadius(inner);
            }
        }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }


    static class MyData extends DonutData<Double> {
        public MyData(String name, Double value) {
            super(name, value);
        }
    }
}
