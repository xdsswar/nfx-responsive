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

package com.xss.it.nfx.responsive.base;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import xss.it.nfx.responsive.control.NfxDoughnutChart;
import xss.it.nfx.responsive.misc.DonutData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the com.xss.it.nfx.responsive.base package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 08, 2025
 * <p>
 * Created on 09/08/2025 at 15:20
 */
public final class NfxDoughnutDelegate<T extends DonutData<? extends Number>> extends Region {
    /**
     * Reference to the owning {@link NfxDoughnutChart} chart.
     * Used to access chart properties and configuration.
     */
    private final NfxDoughnutChart<T> chart;

    /**
     * Canvas used for drawing the doughnut slices and other visuals.
     */
    private final Canvas canvas;

    /**
     * List of legend swatch regions associated with the chart data.
     */
    private final List<Region> swatches = new ArrayList<>();

    /**
     * Listener for changes in the underlying data list.
     */
    private final ListChangeListener<T> dataListener;

    /**
     * Listener for changes in an individual item's name property.
     */
    private final ChangeListener<String> itemNameListener;

    /**
     * Listener for changes in an individual item's numeric value property.
     */
    private final ChangeListener<Number> itemValueListener;

    /**
     * Last mouse position in screen coordinates.
     * <p>
     * Stored as screen-space X/Y to keep tooltips in sync across redraws
     * and layout changes. Initialized to {@code NaN} to indicate "unknown".
     * </p>
     */
    private double lastMouseScreenX = Double.NaN,
            lastMouseScreenY = Double.NaN;

    /**
     * Last mouse position in local (control/canvas) coordinates.
     * <p>
     * Cached local-space X/Y used for hit-testing slices without requiring
     * a new event. Initialized to {@code NaN} to indicate "unknown".
     * </p>
     */
    private double lastMouseLocalX  = Double.NaN,
            lastMouseLocalY  = Double.NaN;

    /**
     * Timeline used for slice transition animations (tweening).
     */
    private Timeline tween;

    /**
     * Previous slice fractions (before animation).
     */
    private List<Double> prevFracs = List.of();

    /**
     * Target slice fractions (end state of animation).
     */
    private List<Double> targetFracs = List.of();

    /**
     * Last fractions actually drawn on the canvas (used for incremental redraw).
     */
    private List<Double> lastDrawnFracs = List.of();

    /**
     * Animation progress property, ranging from {@code 0.0} to {@code 1.0}.
     * Defaults to {@code 1.0} (fully complete).
     */
    private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress", 1.0);

    /**
     * Returns the current animation progress value.
     *
     * @return progress in the range {@code [0,1]}
     */
    public double getProgress() { return progress.get(); }

    /**
     * Index of the slice currently being hovered, or {@code -1} if none.
     */
    private int hoverSliceIndex = -1;

    /**
     * Last center X coordinate of the doughnut, cached for hit testing.
     */
    private double lastCx;

    /**
     * Last center Y coordinate of the doughnut, cached for hit testing.
     */
    private double lastCy;

    /**
     * Last outer radius of the doughnut, cached for hit testing.
     */
    private double lastOuterR;

    /**
     * Last inner radius of the doughnut, cached for hit testing.
     */
    private double lastInnerR;

    /**
     * Popup offset
     */
    private static final int POPUP_OFFSET = 14;

    /**
     * Cached list of last slice start angles (degrees).
     */
    private final List<Double> lastStartAngles = new ArrayList<>();

    /**
     * Cached list of last slice extents (degrees).
     */
    private final List<Double> lastExtents = new ArrayList<>();

    /**
     * Listener for changes to the animation progress property.
     *
     * <p>This listener is typically attached to {@code progress} so that
     * each update in the progress value (ranging from 0.0 to 1.0) triggers
     * a redraw of the doughnut chart, allowing smooth slice animations.</p>
     */
    private final ChangeListener<Number> progressChangeListener;

    /**
     * Number of distinct default colors defined in CSS (e.g., .default-colorN).
     * Used for cycling through slice colors.
     */
    private static final int COLOR_CYCLE = 20;

    /**
     * Reusable popup instance for displaying per-datum details (e.g., tooltip/overlay).
     *
     * <p>Constructed once and reused to minimize allocations and reduce flicker.
     * The content is provided by the current popup factory and applied via
     * {@link NfxPopup#setRoot(Parent)} before showing.</p>
     *
     * <p><b>Lifecycle:</b> Owned by the chart; shown/hidden on interactions.
     * Must be accessed on the JavaFX Application Thread.</p>
     */
    private final NfxPopup popup;

    /**
     * Creates a new delegate responsible for rendering and managing
     * the behavior of a {@link NfxDoughnutChart} chart.
     *
     * <p>This constructor sets up listeners for data changes,
     * name changes, and value changes to ensure the chart redraws
     * and tooltips remain consistent with the underlying data.</p>
     *
     * @param chart the {@link NfxDoughnutChart} control that this delegate serves
     */
    public NfxDoughnutDelegate(NfxDoughnutChart<T> chart) {
        super();
        this.chart = chart;
        this.canvas = new Canvas();
        popup = new NfxPopup();

        progressChangeListener = (obs, ov, nv) -> {
            draw();
            // while animating, keep tooltip values fresh if hovering
            refreshHoverAtLastMouse();
        };

        /*
         * Listener for item name changes.
         * Redraws the chart and refreshes the tooltip if the mouse
         * is currently hovering over a slice.
         */
        itemNameListener  = (obs, ov, nv) -> {
            draw();
            refreshHoverAtLastMouse();
        };

        /*
         * Listener for item value changes.
         * Triggers animation (or direct redraw if disabled) and
         * refreshes the tooltip if hovering.
         */
        itemValueListener = (obs, ov, nv) -> {
            animateOrDraw();
            refreshHoverAtLastMouse();
        };

        /*
         * Listener for changes in the observable data list.
         * Attaches/detaches item listeners on add/remove, ensures swatches
         * are kept in sync, triggers animation or redraw, and maintains
         * tooltip consistency when slices are added or removed.
         */
        dataListener = change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    for (T item : change.getRemoved()) detachItemListeners(item);
                }
                if (change.wasAdded()) {
                    for (T item : change.getAddedSubList()) attachItemListeners(item);
                }
            }
            ensureSwatches();
            animateOrDraw();
        };



        initialize();
    }

    /**
     * Initializes the delegate by setting up the canvas, listeners,
     * data hooks, and tooltip behavior.
     *
     * <p>This method wires up property listeners so that changes to the
     * chart's size, properties, or data automatically trigger a redraw
     * of the doughnut chart. It also installs tooltip handling for
     * slice hover interactions.</p>
     */
    private void initialize(){
        getChildren().add(canvas);


        widthProperty().addListener((o, a, b) -> draw());
        heightProperty().addListener((o, a, b) -> draw());
        canvas.widthProperty().addListener((o, a, b) -> draw());
        canvas.heightProperty().addListener((o, a, b) -> draw());
        chart.titleProperty().addListener((o, a, b) -> draw());
        chart.titleFontProperty().addListener((o,a,b)->draw());
        chart.subtitleProperty().addListener((o, a, b) -> draw());
        chart.subtitleFontProperty().addListener((o,a,b)->draw());
        chart.innerRadiusProperty().addListener((o, a, b) -> draw());
        chart.legendSquareSizeProperty().addListener((o, a, b) -> draw());
        chart.legendGapProperty().addListener((o, a, b) -> draw());
        chart.legendItemGapProperty().addListener((o, a, b) -> draw());
        chart.legendLineGapProperty().addListener((o, a, b) -> draw());
        chart.legendMarkerShapeProperty().addListener((o,a,b)->draw());
        chart.legendMarkerCornerRadiusProperty().addListener((o,a,b)-> draw());
        chart.legendLabelFontProperty().addListener((o,a,b)->draw());
        chart.legendLabelFillProperty().addListener((o,a,b)->draw());
        chart.legendOrderProperty().addListener((o,a,b)->draw());
        chart.legendAutoHideProperty().addListener((o,a,b)->draw());
        chart.legendAutoHideMinWidthProperty().addListener((o,a,b)->draw());
        chart.legendAutoHideMinHeightProperty().addListener((o,a,b)->draw());
        chart.showLegendProperty().addListener((o,a,b)->draw());
        chart.showTitleProperty().addListener((o,a,b)->draw());
        chart.showSubtitleProperty().addListener((o,a,b)->draw());
        chart.popupEnabledProperty().addListener((o,a,b)->{
            if (!b && popup.isShowing()){
                popup.hide();
            }
            draw();
        });
        chart.chartBackgroundProperty().addListener((o, a, b)-> {
            syncBackground(b);
            draw();
        });
        chart.borderGapProperty().addListener((obs, o, g) -> {
            requestLayout();
            draw();
        });

        chart.popupFactoryProperty().addListener((
                o,
                a,
                b)->{
            draw();
            refreshHoverAtLastMouse();
        });

        chart.dataProperty().addListener((obs, oldList, newList)
                -> handleData(oldList, newList));
        handleData(null, chart.getData());


        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            lastMouseScreenX = e.getScreenX();
            lastMouseScreenY = e.getScreenY();
            lastMouseLocalX  = e.getX();
            lastMouseLocalY  = e.getY();
            onMouseMoved(e);
        });

        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            hoverSliceIndex = -1;
            popup.hide();
            lastMouseScreenX = lastMouseScreenY = lastMouseLocalX = lastMouseLocalY = Double.NaN;
        });

        popup.attach(this, null);
        sceneProperty().addListener((obs, o, s) -> {
            popup.attach(this, s);
        });
    }

    /**
     * Re-evaluates hover state and tooltip using the last known mouse screen position.
     *
     * <p>Uses {@code lastMouseScreenX} and {@code lastMouseScreenY} to convert the
     * cursor position back into canvas/local coordinates, performs a hit test against
     * the cached doughnut geometry, updates {@code hoverSliceIndex}, and refreshes
     * the tooltip text/position if a slice is still under the cursor.</p>
     *
     * <p>If the last mouse position is undefined (NaN) or the point no longer hits any
     * slice (e.g., after data/layout changes), this method clears the hover state and
     * hides the tooltip.</p>
     *
     * <p>Typically called after redraws/animations or data/property updates to keep
     * hover feedback consistent while the mouse is stationary.</p>
     */
    @SuppressWarnings("all")
    private void refreshHoverAtLastMouse() {
        // Convert saved screen to current local in case layout moved
        Point2D pLocal = canvas.screenToLocal(lastMouseScreenX, lastMouseScreenY);
        double mx = (pLocal != null) ? pLocal.getX() : lastMouseLocalX;
        double my = (pLocal != null) ? pLocal.getY() : lastMouseLocalY;

        int hit = hitTestSlice(mx, my);
        hoverSliceIndex = hit;

        //Again we store these
        if (!chart.isPopupEnabled()){
            return;
        }

        if (hit >= 0 && hit < chart.getData().size()) {
            DonutData<?> dx = chart.getData().get(hit);
            if (chart.getPopupFactory() != null) {
                Parent root = chart.getPopupFactory().call((T) dx, chart);
                popup.setRoot(root);
                popup.attach(this, getScene());
                popup.show(chart,lastMouseScreenX + POPUP_OFFSET, lastMouseScreenY + POPUP_OFFSET);
            }
        } else {
            popup.hide();
        }
    }


    /**
     * Handles replacement of the chart's data list.
     *
     * <p>This method detaches listeners from the old list (if present),
     * attaches listeners to the new list, ensures swatches are updated,
     * and triggers an animation or redraw of the chart. It also keeps
     * the tooltip state consistent after the data swap.</p>
     *
     * @param o the old data list (may be {@code null})
     * @param n the new data list (may be {@code null})
     */
    private void handleData(ObservableList<T> o, ObservableList<T> n){
        if (o != null){
            o.removeListener(dataListener);
            for (T t : o) detachItemListeners(t);
        }
        if (n != null){
            for (T t : n) attachItemListeners(t);
            n.addListener(dataListener);
        }
        ensureSwatches();
        animateOrDraw();
    }

    /**
     * Attaches listeners to a data item.
     *
     * <p>This method adds the configured listeners for {@code name}
     * and {@code value} properties so that changes in the item trigger
     * redraws or animations in the chart.</p>
     *
     * @param item the data item to attach listeners to
     */
    private void attachItemListeners(T item) {
        item.nameProperty().addListener(itemNameListener);
        item.valueProperty().addListener(itemValueListener);
    }

    /**
     * Detaches listeners from a data item.
     *
     * <p>This method removes the configured listeners from the
     * {@code name} and {@code value} properties to prevent memory
     * leaks or redundant updates when the item is no longer part
     * of the chart.</p>
     *
     * @param item the data item to detach listeners from
     */
    private void detachItemListeners(T item) {
        item.nameProperty().removeListener(itemNameListener);
        item.valueProperty().removeListener(itemValueListener);
    }

    /**
     * Triggers either an animated transition or an immediate redraw
     * depending on the chart's animation setting.
     *
     * <p>If {@link NfxDoughnutChart#isAnimated()} is {@code true}, the chart
     * transitions smoothly to the new data state by calling
     * {@code animateToCurrentData()}. Otherwise, the chart is redrawn
     * immediately with the current data fractions, and the progress is
     * set to {@code 1.0} (complete).</p>
     */
    private void animateOrDraw() {
        if (chart.isAnimated()) {
            animateToCurrentData();
        } else {
            progress.set(1.0);
            lastDrawnFracs = computeFractions(chart.getData());
            prevFracs = lastDrawnFracs;
            targetFracs = lastDrawnFracs;
            draw();
        }
    }

    /**
     * Animates the chart from the last drawn slice fractions
     * to the current data slice fractions.
     *
     * <p>This method computes the new fractions based on the chart's
     * data, aligns them with the previous fractions for a smooth
     * transition, and then creates a {@link Timeline} that interpolates
     * the {@code progress} property from {@code 0.0} to {@code 1.0}.
     * The {@code progressChangeListener} is reattached to update the
     * chart as the animation progresses.</p>
     *
     * <p>The duration of the animation is controlled by
     * {@link NfxDoughnutChart#getAnimationDelay()}, and the interpolation
     * uses {@link Interpolator#EASE_BOTH} for smooth acceleration and
     * deceleration.</p>
     */
    private void animateToCurrentData() {
        List<Double> newFracs = computeFractions(chart.getData());

        if (lastDrawnFracs.isEmpty() && !newFracs.isEmpty()) {
            prevFracs = zeros(newFracs.size());
        } else if (!lastDrawnFracs.isEmpty() && newFracs.isEmpty()) {
            prevFracs = new ArrayList<>(lastDrawnFracs);
            newFracs  = zeros(prevFracs.size());
        } else {
            prevFracs = alignFractions(lastDrawnFracs, newFracs.size());
        }

        targetFracs = newFracs;

        if (tween != null) tween.stop();
        progress.set(0.0);

        Duration d = Duration.millis(chart.getAnimationDelay());
        tween = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress, 0.0, Interpolator.EASE_BOTH)),
                new KeyFrame(d, new KeyValue(progress, 1.0, Interpolator.EASE_BOTH))
        );
        progress.removeListener(progressChangeListener);
        progress.addListener(progressChangeListener);
        tween.play();
    }


    /**
     * Lays out the children of this region.
     *
     * <p>This method resizes and repositions the {@code canvas}
     * to fill the entire available area of the control. After
     * updating the canvas size and position, it reapplies CSS
     * and redraws the chart.</p>
     *
     * NOTE : DO NOT TOUCH THE ORDER
     */
    @Override
    protected void layoutChildren() {
        double gap = chart.getBorderGap();
        canvas.setLayoutX(gap);
        canvas.setLayoutY(gap);
        canvas.setWidth(getWidth() - 2 * gap);
        canvas.setHeight(getHeight() - 2 * gap);
        applyCss();
        draw();
    }


    /**
     * Synchronizes the control's background with the specified paint.
     *
     * <p>If the given {@code paint} is non-null, a new {@link Background}
     * is created using a {@link BackgroundFill} with no corner radii
     * and no insets, effectively filling the entire region. If the
     * {@code paint} is {@code null}, the background is reset to
     * {@link Background#EMPTY}.</p>
     *
     * @param paint the {@link Paint} to apply as the background fill,
     *              or {@code null} to clear the background
     */
    private void syncBackground(Paint paint) {
        setBackground(paint != null
                ? new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY))
                : Background.EMPTY);
    }


    /**
     * Computes the preferred width of this control.
     *
     * <p>This implementation returns a fixed preferred width of {@code 560}
     * pixels, regardless of the given height. The layout system uses this
     * value when no explicit width is set.</p>
     *
     * @param h the height constraint (ignored in this implementation)
     * @return the preferred width in pixels
     */
    @Override
    protected double computePrefWidth(double h)  {
        return 560;
    }

    /**
     * Computes the preferred height of this control.
     *
     * <p>This implementation returns a fixed preferred height of {@code 400}
     * pixels, regardless of the given width. The layout system uses this
     * value when no explicit height is set.</p>
     *
     * @param w the width constraint (ignored in this implementation)
     * @return the preferred height in pixels
     */
    @Override
    protected double computePrefHeight(double w) {
        return 400;
    }

    /**
     * Redraws the doughnut chart on the canvas.
     *
     * <p>This method clears the canvas and renders the chart
     * based on the current data, properties, and animation
     * progress. It handles drawing of slices, labels, legends,
     * and background as needed. The rendering is typically
     * triggered automatically when properties or data change,
     * or during animations.</p>
     *
     * <p>Called internally by layout, animation, and property
     * listeners; not intended to be invoked directly by user
     * code in most cases.</p>
     */
    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();
        g.clearRect(0, 0, W, H);
        // background
        Paint bg = chart.getChartBackground();
        if (bg != null) {
            g.setFill(bg);
            g.fillRect(0, 0, W, H);
        }

        Insets pad = getPadding();
        double x0 = pad.getLeft(),  y0 = pad.getTop();
        double x1 = W - pad.getRight(), y1 = H - pad.getBottom();

        // titles
        double titleSpace = 0;
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.TOP);

        if (chart.isShowTitle()) {
            String title = chart.getTitle();
            if (title != null && !title.isEmpty()) {
                Font f = chart.getTitleFont();
                g.setFont(f);
                g.setFill(chart.getTitleFill());
                g.fillText(title, (x0 + x1) * 0.5, y0);
                titleSpace += f.getSize() * 1.6;
            }
        }

        if (chart.isShowSubtitle()) {
            String subtitle = chart.getSubtitle();
            if (subtitle != null && !subtitle.isEmpty()) {
                Font f = chart.getSubtitleFont();
                g.setFont(f);
                g.setFill(chart.getSubtitleFill());
                g.fillText(subtitle, (x0 + x1) * 0.5, y0 + titleSpace);
                titleSpace += f.getSize() * 1.8;
            }
        }

        // content box
        double contentTop = y0 + titleSpace + 10;
        double contentLeft = x0;
        double contentRight = x1;
        double contentHeight = Math.max(0, y1 - contentTop);

        ensureSwatches();
        applyCss();

        // legend decision (LEFT/RIGHT only)
        NfxDoughnutChart.LegendPos pos = chart.getLegendPosition();
        boolean showLegend = (pos == NfxDoughnutChart.LegendPos.LEFT || pos == NfxDoughnutChart.LegendPos.RIGHT)
                && chart.isShowLegend()
                && !chart.getData().isEmpty()
                && shouldShowLegend(contentLeft, contentTop,
                contentRight - contentLeft, contentHeight, W, H);

        // measure legend and reserve width
        LegendBlock lm = new LegendBlock(0, 0);
        if (showLegend) {
            lm = measureSideLegend(g, contentHeight);
            double colGap = Math.max(18, chart.getLegendItemGap() * 2.0);
            if (pos == NfxDoughnutChart.LegendPos.LEFT)   contentLeft  = Math.min(contentRight - 40, contentLeft  + lm.width + colGap);
            else /* RIGHT */             contentRight = Math.max(contentLeft  + 40, contentRight - (lm.width + colGap));
        }

        // donut bounds
        double plotW = Math.max(0, contentRight - contentLeft);
        double plotH = Math.max(0, y1 - contentTop);
        double size  = Math.max(0, Math.min(plotW, plotH));
        double cx = contentLeft + plotW * 0.5;
        double cy = contentTop  + plotH * 0.5;
        double r  = size * 0.5;

        // animated arcs
        final double tt = clamp(getProgress(), 0.0, 1.0);
        int n = Math.max(chart.getData().size(), Math.max(prevFracs.size(), targetFracs.size()));

        double start = 90;
        lastStartAngles.clear();
        lastExtents.clear();

        for (int i = 0; i < n; i++) {
            double fromF = (i < prevFracs.size())   ? prevFracs.get(i)   : 0.0;
            double toF   = (i < targetFracs.size()) ? targetFracs.get(i) : 0.0;
            double extent = 360 * lerp(fromF, toF, tt);

            g.setFill(colorForIndex(i));
            g.fillArc(cx - r, cy - r, r * 2, r * 2, start, extent, ArcType.ROUND);

            lastStartAngles.add(start);
            lastExtents.add(extent);
            start += extent;
        }

        // hole
        double innerR = r * (chart.getInnerRadius() /100);
        g.setFill(bg != null ? bg : backgroundBaseColorOrWhite());
        g.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

        // cache for hit-testing
        lastCx = cx; lastCy = cy; lastOuterR = r; lastInnerR = innerR;

        // legend (vertically centered)
        if (showLegend && lm.width > 0 && lm.height > 0) {
            double legendX = (pos == NfxDoughnutChart.LegendPos.LEFT) ? x0 : (x1 - lm.width);
            double legendY = contentTop + Math.max(0, (contentHeight - lm.height) * 0.5);
            drawSideLegend(g, legendX, legendY, lm.height);
        }

        if (popup.isShowing() && hoverSliceIndex >= 0 && !Double.isNaN(lastMouseScreenX)) {
            refreshHoverAtLastMouse();
        }
        if (tt >= 1.0) lastDrawnFracs = targetFracs;
    }


    /**
     * Measures the dimensions of the side legend based on the available height
     * and the current chart legend entries.
     *
     * <p>This method iterates over the legend items, taking into account
     * font metrics and swatch (shape) sizes, to determine the total
     * width and height required to render the legend vertically
     * alongside the chart. The resulting {@link LegendBlock} provides
     * layout information that can be used when positioning the legend.</p>
     *
     * @param g         the {@link GraphicsContext} used for measuring text width
     *                  and rendering considerations
     * @param maxHeight the maximum vertical space available for the legend
     * @return a {@link LegendBlock} containing the calculated width and height
     *         of the side legend
     */
    private LegendBlock measureSideLegend(GraphicsContext g, double maxHeight) {
        var data = chart.getData();
        if (data == null || data.isEmpty() || maxHeight <= 0) return new LegendBlock(0, 0);

        Font font = chart.getLegendLabelFont();
        g.setFont(font);

        // reference metrics from Text (public API)
        Text ref = new Text("Ayg"); ref.setFont(font);
        double lineH = ref.getLayoutBounds().getHeight();

        double square    = chart.getLegendSquareSize();
        double rowH      = Math.max(square, lineH);
        double markGap   = chart.getLegendGap();
        double itemGap   = chart.getLegendItemGap();
        double lineGap   = chart.getLegendLineGap();
        double innerPad  = 8;                 // same as LEGEND_SIDE_PADDING
        double contentMaxH = Math.max(0, maxHeight - 2 * innerPad);
        double colGap    = Math.max(18, itemGap * 2.0);

        List<Item> items = make(data, font, square, markGap);

        double colH = 0, colW = 0, totalW = 0, totalH = 0;
        for (Item it : items) {
            if (colH > 0 && (colH + rowH) > contentMaxH) {
                totalW += colW + colGap;
                totalH = Math.max(totalH, colH);
                colH = 0; colW = 0;
            }
            colW = Math.max(colW, it.iw());
            colH += rowH + lineGap;
        }
        if (colW > 0) {
            totalW += colW;
            totalH = Math.max(totalH, colH);
        }

        double finalW = Math.ceil(totalW + 2 * innerPad);
        double finalH = Math.ceil(Math.min(totalH + 2 * innerPad, maxHeight));
        return new LegendBlock(finalW, finalH);
    }

    /**
     * Draws the side legend at the specified position using the provided graphics context.
     *
     * <p>This method renders each legend entry consisting of a swatch (shape) and its
     * corresponding label. The swatch and text are aligned so that the label is vertically
     * centered with respect to the swatch, taking into account font ascent and descent.
     * The legend is drawn in a vertical layout, with each entry stacked within the given
     * block height.</p>
     *
     * @param g           the {@link GraphicsContext} used for drawing shapes and text
     * @param x           the x-coordinate where the legend block should be drawn
     * @param y           the y-coordinate where the legend block should start
     * @param blockHeight the total height allocated for the legend block
     */
    private void drawSideLegend(GraphicsContext g, double x, double y, double blockHeight) {
        var data = chart.getData();
        if (data == null || data.isEmpty() || blockHeight <= 0) return;

        Font font = chart.getLegendLabelFont();
        g.setFont(font);
        g.setTextAlign(TextAlignment.LEFT);
        g.setTextBaseline(VPos.BASELINE);

        Text ref = new Text("Ayg");
        ref.setFont(font);
        double lineH   = ref.getLayoutBounds().getHeight();
        double ascent  = ref.getBaselineOffset();
        double descent = Math.max(0, lineH - ascent);

        double square   = chart.getLegendSquareSize();
        double rowH     = Math.max(square, lineH);
        double markGap  = chart.getLegendGap();
        double itemGap  = chart.getLegendItemGap();
        double lineGap  = chart.getLegendLineGap();
        double innerPad = 8;
        double colGap   = Math.max(18, itemGap * 2.0);
        double contentMaxH = Math.max(0, blockHeight - 2 * innerPad);

        List<Item> items = make(data, font, square, markGap);

        double colX = x + innerPad;
        double colY = y + innerPad;
        double colMaxW = 0;

        for (Item it : items) {
            // wrap column if needed
            if ((colY - (y + innerPad)) + rowH > contentMaxH && (colY > y + innerPad)) {
                colX += Math.max(colMaxW, it.iw()) + colGap;
                colY = y + innerPad;
                colMaxW = 0;
            }

            // row center
            double rowCenterY = colY + rowH * 0.5;

            // marker centered to row
            double markY = rowCenterY - square * 0.5;
            g.setFill(colorForIndex(it.idx()));
            switch (chart.getLegendMarkerShape()) {
                case CIRCLE -> {
                    g.fillOval(colX, markY, square, square);
                    g.setStroke(Color.gray(0, 0.15));
                    g.strokeOval(colX, markY, square, square);
                }
                case ROUNDED -> {
                    double cr = Math.min(chart.getLegendMarkerCornerRadius(), square / 2.0);
                    g.fillRoundRect(colX, markY, square, square, cr * 2, cr * 2);
                    g.setStroke(Color.gray(0, 0.15));
                    g.strokeRoundRect(colX, markY, square, square, cr * 2, cr * 2);
                }
                case SQUARE -> {
                    g.fillRect(colX, markY, square, square);
                    g.setStroke(Color.gray(0, 0.15));
                    g.strokeRect(colX, markY, square, square);
                }
            }

            // baseline so text center == marker center:
            // center(text) = baseline + (descent - ascent)/2  => baseline = rowCenter - (descent - ascent)/2
            double textX = colX + square + markGap;
            double baselineY = rowCenterY + (ascent - descent) * 0.4;

            g.setFill(chart.getLegendLabelFill());
            g.fillText(it.text(), textX, baselineY);

            colY += rowH + lineGap;
            colMaxW = Math.max(colMaxW, it.iw());
        }
    }

    /**
     * Builds the list of legend {@code Item}s with precomputed text and entry widths,
     * then orders them according to the chart's legend ordering policy.
     *
     * <p>For each datum:
     * <ul>
     *   <li>Resolves the label as {@code Optional.ofNullable(name).orElse("")}.</li>
     *   <li>Measures the label width ({@code tw}) using a temporary {@link Text} with the given {@link Font}.</li>
     *   <li>Computes the entry width ({@code iw}) as {@code square + markGap + tw}, where
     *       {@code square} is the swatch size and {@code markGap} is the gap between the swatch and text.</li>
     * </ul>
     * The resulting list is then sorted based on {@code chart.getLegendOrder()}:
     * <ul>
     *   <li><b>BY_NAME_ASC</b> — alphabetical by {@code Item.text()}</li>
     *   <li><b>BY_VALUE_ASC</b> — ascending by datum value (null treated as 0.0)</li>
     *   <li><b>BY_VALUE_DESC</b> — descending by datum value (null treated as 0.0)</li>
     *   <li><b>AS_IS</b> — preserves incoming order</li>
     * </ul>
     *
     * <p><b>Notes:</b> Measuring with a new {@link Text} per datum is O(n) and may allocate;
     * reuse/caching can be considered for very large legends.</p>
     *
     * @param data     the observable list of data items to show in the legend
     * @param font     the font used to measure label widths
     * @param square   the swatch size (width/height) in pixels
     * @param markGap  the horizontal gap between the swatch and the label, in pixels
     * @return a list of {@link Item} entries, possibly re-ordered per legend policy
     */
    private List<Item> make(ObservableList<T> data, Font font, double square, double markGap ){
        List<Item> items = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            String label = Optional.ofNullable(data.get(i).getName()).orElse("");
            Text tx = new Text(label); tx.setFont(font);
            double tw = tx.getLayoutBounds().getWidth();
            double iw = square + markGap + tw;
            items.add(new Item(i, label, tw, iw));
        }
        switch (chart.getLegendOrder()) {
            case BY_NAME_ASC  -> items.sort(Comparator.comparing(Item::text));
            case BY_VALUE_ASC -> items.sort(Comparator.comparingDouble(it -> {
                Number v = data.get(it.idx()).getValue(); return v == null ? 0.0 : v.doubleValue();
            }));
            case BY_VALUE_DESC -> items.sort(Comparator.comparingDouble((Item it) -> {
                Number v = data.get(it.idx()).getValue(); return v == null ? 0.0 : v.doubleValue();
            }).reversed());
            case AS_IS -> { }
        }
        return items;
    }

    /**
     * Determines whether the legend should be shown given the chart's
     * current dimensions and thresholds.
     *
     * <p>This method checks the chart's visibility rules, including
     * the {@code showLegend} flag and auto-hide settings, against the
     * provided layout bounds. Typically used during layout and drawing
     * to decide if the legend should be rendered.</p>
     *
     * @param x the x-coordinate of the chart area
     * @param y the y-coordinate of the chart area
     * @param w the width of the chart area
     * @param h the height of the chart area
     * @param W the total available width for the control
     * @param H the total available height for the control
     * @return {@code true} if the legend should be displayed,
     *         {@code false} if it should be hidden
     */
    private boolean shouldShowLegend(double x, double y, double w, double h, double W, double H) {
        boolean autoHide = chart.isLegendAutoHide();
        if (!autoHide) return true;

        double minW = chart.getLegendAutoHideMinWidth()  > 0 ? chart.getLegendAutoHideMinWidth()  : 260;
        double minH = chart.getLegendAutoHideMinHeight() > 0 ? chart.getLegendAutoHideMinHeight() : 200;

        if (W < minW || H < minH) return false;
        if (w < 180 || h < 20)    return false;

        double t = chart.getLegendLabelFont() != null ? chart.getLegendLabelFont().getSize() : 0;
        double fs = Math.max(8, t);
        double lineHeight = Math.max(chart.getLegendSquareSize(), fs * 1.2);
        return h >= lineHeight + 2;
    }

    /**
     * Handles mouse movement over the chart canvas.
     *
     * <p>This method performs hit testing to determine whether the mouse
     * is currently hovering over a doughnut slice. If so, it updates
     * {@code hoverSliceIndex} and displays or refreshes the tooltip at
     * the mouse position. If not, it clears the hover state.</p>
     *
     * @param e the {@link MouseEvent} describing the mouse movement
     */
    @SuppressWarnings("all")
    private void onMouseMoved(MouseEvent e) {
        lastMouseScreenX = e.getScreenX();
        lastMouseScreenY = e.getScreenY();
        int hit = hitTestSlice(e.getX(), e.getY());
        if (hit != hoverSliceIndex) hoverSliceIndex = hit;

        //We store prev vals
        if (!chart.isPopupEnabled()){
            return;
        }

        if (hit >= 0 && hit < chart.getData().size()) {
            DonutData<?> dx = chart.getData().get(hit);
            if (chart.getPopupFactory() != null) {
                Parent root = chart.getPopupFactory().call((T) dx, chart);
                popup.setRoot(root);
                popup.attach(this, getScene());
                popup.show(chart,lastMouseScreenX + POPUP_OFFSET, lastMouseScreenY + POPUP_OFFSET);
            }
        } else {
            popup.hide();
        }
    }


    /**
     * Performs hit testing to determine which slice (if any)
     * contains the given mouse coordinates.
     *
     * <p>This method compares the supplied mouse coordinates
     * against the cached chart geometry (center, radii, start
     * angles, extents) to identify the slice under the cursor.
     * </p>
     *
     * @param mx the mouse x-coordinate (in canvas space)
     * @param my the mouse y-coordinate (in canvas space)
     * @return the index of the slice being hovered, or {@code -1}
     *         if no slice contains the point
     */
    private int hitTestSlice(double mx, double my) {
        // radial check (point must be within the ring)
        double dx = mx - lastCx;
        double dy = my - lastCy;
        double dist = Math.hypot(dx, dy);
        if (dist < lastInnerR || dist > lastOuterR) return -1;

        // angle in Canvas/GC coordinates: 0° at +X, positive COUNTER-CLOCKWISE
        double ang = Math.toDegrees(Math.atan2(-dy, dx));
        if (ang < 0) ang += 360;            // normalize to [0, 360)
        final double mouseAngle = ang;

        for (int i = 0; i < lastStartAngles.size(); i++) {
            double s = lastStartAngles.get(i);
            double e = lastExtents.get(i);
            if (e <= 0) continue;           // nothing to hit
            if (e >= 360) return i;         // full circle (degenerate case)

            // normalize start and compute end in [0, 360)
            s = (s % 360 + 360) % 360;
            double end = (s + e) % 360;     // e is positive CCW

            final boolean hit = (s <= end)
                    ? (mouseAngle >= s && mouseAngle <= end)
                    : (mouseAngle >= s || mouseAngle <= end); // wrap around 360

            if (hit) return i;
        }
        return -1;
    }

    /**
     * Computes the normalized slice fractions for the given data list.
     *
     * <p>This method sums the values of all {@code DonutData} items
     * and divides each item’s value by the total, producing a list of
     * fractions in the range {@code [0.0, 1.0]}. The resulting fractions
     * are used for slice angles and animation transitions.</p>
     *
     * <p>If the data list is empty or the total value is {@code 0},
     * the returned list will contain zeros for each entry.</p>
     *
     * @param data the list of data items to convert into fractions
     * @return a list of normalized fractions corresponding to each item
     */
    private List<Double> computeFractions(List<T> data) {
        double total = 0.0;
        for (T dx : data) {
            Number v = dx.getValue();
            if (v != null) {
                double d = v.doubleValue();
                if (d > 0) total += d;
            }
        }
        if (total <= 0) total = 1.0;

        ArrayList<Double> fracs = new ArrayList<>(data.size());
        for (T dx : data) {
            Number v = dx.getValue();
            double d = (v == null) ? 0.0 : v.doubleValue();
            if (d < 0) d = 0.0;
            fracs.add(d / total);
        }
        return fracs;
    }

    /**
     * Aligns a source list of fractions to a desired size.
     *
     * <p>If the source list has fewer elements than {@code size},
     * the missing entries are padded with {@code 0.0}. If the source
     * list has more elements, only the first {@code size} entries
     * are copied.</p>
     *
     * @param src  the source list of fractions
     * @param size the desired target size
     * @return a new list of exactly {@code size} elements
     */
    private List<Double> alignFractions(List<Double> src, int size) {
        ArrayList<Double> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) out.add(i < src.size() ? src.get(i) : 0.0);
        return out;
    }

    /**
     * Performs linear interpolation between two values.
     *
     * @param a the start value
     * @param b the end value
     * @param t the interpolation factor in the range [0.0, 1.0]
     * @return the interpolated value {@code a + (b - a) * t}
     */
    private static double lerp(double a, double b, double t) { return a + (b - a) * t; }

    /**
     * Clamps a value to the given range.
     *
     * @param v   the input value
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return {@code v} constrained to the range [{@code min}, {@code max}]
     */
    @SuppressWarnings("all")
    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }

    /**
     * Ensures that the legend swatches are in sync with the chart data.
     *
     * <p>This method adjusts the number and state of swatch {@link Region}
     * nodes to match the current data list. If there are more data items
     * than swatches, new swatches are created. If there are fewer, extra
     * swatches may be removed or hidden. Swatches are styled using the
     * chart’s legend-related properties (color, marker shape, etc.).</p>
     *
     * <p>Called whenever the data list changes, ensuring that the legend
     * always accurately reflects the chart slices.</p>
     */
    private void ensureSwatches() {
        int desired = isAnimatingOut() ? Math.max(swatches.size(), prevFracs.size())
                : chart.getData().size();

        while (swatches.size() > desired) {
            int last = swatches.size() - 1;
            getChildren().remove(swatches.remove(last));
        }
        while (swatches.size() < desired) {
            int idx = swatches.size();
            Region r = new Region();
            r.getStyleClass().addAll("nfx-doughnut-chart", "default-color" + (idx % COLOR_CYCLE));
            r.setStyle(null);
            r.setManaged(false);
            r.setVisible(false);
            r.setPrefSize(0, 0);
            getChildren().add(r);
            swatches.add(r);
        }

        for (int i = 0; i < swatches.size(); i++) {
            Region r = swatches.get(i);
            r.setStyle(null);
            r.getStyleClass().removeIf(s -> s.startsWith("default-color"));
            if (!r.getStyleClass().contains("nfx-doughnut-chart")) {
                r.getStyleClass().add("nfx-doughnut-chart");
            }
            r.getStyleClass().add("default-color" + (i % COLOR_CYCLE));
        }

        applyCss();
        for (Region r : swatches) r.applyCss();
    }

    /**
     * Returns the color associated with the given slice index.
     *
     * <p>This method looks up a color for the slice at index {@code i},
     * typically cycling through the predefined CSS classes
     * (e.g. {@code .default-colorN.doughnut-chart}). The number of
     * available colors is controlled by {@code COLOR_CYCLE}, so the
     * index is wrapped accordingly.</p>
     *
     * @param i the slice index
     * @return the {@link Color} assigned to the slice
     */
    private Color colorForIndex(int i) {
        if (i < 0 || i >= swatches.size()) return Color.GRAY;
        Background bg = swatches.get(i).getBackground();
        if (bg == null || bg.getFills().isEmpty()) return Color.GRAY;
        BackgroundFill bf = bg.getFills().getFirst();
        Paint p = bf.getFill();
        return (p instanceof Color) ? (Color) p : Color.GRAY;
    }

    /**
     * Resolves the base background color for the chart.
     *
     * <p>If the chart has a custom {@code chartBackground} paint set
     * and it is a solid {@link Color}, that color is returned.
     * Otherwise, this method falls back to {@code Color.WHITE} as a
     * safe default. This ensures that calculations relying on a
     * concrete base color (such as deriving contrasting stroke or
     * text colors) always have a valid value.</p>
     *
     * @return the background base {@link Color}, or white if not defined
     */
    private Color backgroundBaseColorOrWhite() {
        Background bg = getBackground();
        if (bg != null && !bg.getFills().isEmpty()) {
            BackgroundFill bf = bg.getFills().getFirst();
            Paint p = bf.getFill();
            if (p instanceof Color) return (Color) p;
        }
        return Color.WHITE;
    }

    /**
     * Immutable record representing a legend block within the chart.
     *
     * <p>This record encapsulates the dimensions of a legend entry
     * (typically consisting of a colored shape and its associated label).
     * It is primarily used for layout calculations when positioning
     * legend items in relation to the chart.</p>
     *
     * @param width  the total width of the legend block
     * @param height the total height of the legend block
     */
    private record LegendBlock(double width, double height) { }

    /**
     * Immutable record representing a single legend entry item.
     *
     * <p>Each item corresponds to one legend row, consisting of an index,
     * a text label, and its pre-computed width measurements. These
     * dimensions are useful for aligning the legend swatch (icon/shape)
     * with the label during rendering.</p>
     *
     * @param idx the index of the legend item within the data set
     * @param text the label text associated with this legend entry
     * @param tw the measured text width, used for layout calculations
     * @param iw the icon (swatch) width, used for spacing and alignment
     */
    private record Item(int idx, String text, double tw, double iw) {}

    /**
     * Creates a mutable list of {@code n} zeros.
     *
     * <p>Allocates an {@link ArrayList} with initial capacity {@code n} and fills it
     * with {@code n} elements, each set to {@code 0.0}.</p>
     *
     * <p><b>Complexity:</b> O(n)</p>
     *
     * @param n the number of elements; must be non-negative
     * @return a {@link List} of length {@code n} containing only {@code 0.0}
     * @throws IllegalArgumentException if {@code n} is negative (from {@link ArrayList} capacity)
     */
    private static List<Double> zeros(int n) {
        ArrayList<Double> z = new ArrayList<>(n);
        for (int i = 0; i < n; i++) z.add(0.0);
        return z;
    }

    /**
     * Indicates whether the current transition is an "animate out" (collapsing to zero).
     *
     * <p>Returns {@code true} when a previous state exists ({@code prevFracs} is non-empty)
     * and every target fraction in {@code targetFracs} equals {@code 0.0}.</p>
     *
     * <p><b>Note on equality:</b> This uses exact comparison ({@code f == 0.0}). Ensure
     * target values are set precisely to zero; otherwise consider a small tolerance.</p>
     *
     * @return {@code true} if animating out; {@code false} otherwise
     */
    private boolean isAnimatingOut() {
        return !prevFracs.isEmpty() && targetFracs.stream().allMatch(f -> f == 0.0);
    }
}

