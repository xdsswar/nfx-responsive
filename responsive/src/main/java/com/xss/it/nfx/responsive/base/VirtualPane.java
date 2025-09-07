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

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

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
 * @since September 05, 2025
 * <p>
 * Created on 09/05/2025 at 17:59
 * <p>
 * Key characteristics:
 * <ul>
 *   <li>No internal layout work — {@link #layoutChildren()} is a no-op.</li>
 *   <li>Pixel-snapped and clipped to the viewport to avoid shimmer or overdraw.</li>
 *   <li>Size setters are guarded to reduce layout churn and redundant passes.</li>
 *   <li>Optional batch mode to coalesce {@link #requestLayout()} calls while
 *       updating children, reducing redundant invalidations.</li>
 * </ul>
 */
public final class VirtualPane extends Pane {
    /**
     * Clip node that always matches the pane's bounds.
     * <p>
     * Ensures content is not drawn outside the viewport, preventing
     * visual artifacts and flicker at the edges.
     */
    private final Rectangle clip = new Rectangle();

    /**
     * Tracks the last content height applied.
     * <p>
     * Used to avoid redundant {@code setPrefHeight} and
     * {@code setMinHeight} calls when the height has not meaningfully changed.
     */
    private double lastContentHeight = -1;

    /**
     * Flag indicating whether batch mode is active.
     * <p>
     * While {@code true}, calls to {@link #requestLayout()} become cheap
     * no-ops. Layout will only be requested once when {@link #endBatch()} is called.
     */
    private boolean batching;

    /**
     * Listener that keeps the clip rectangle in sync with the pane's size.
     * <p>
     * Updates width and height whenever the pane's dimensions change.
     */
    private final InvalidationListener sizeListener = obs -> {
        clip.setWidth(getWidth());
        clip.setHeight(getHeight());
    };


    /**
     * Weak wrapper around {@link #sizeListener} to prevent memory leaks.
     */
    private final WeakInvalidationListener weakSizeListener;

    /**
     * Creates a new {@code VirtualPane}.
     * <p>
     * Configures pixel snapping, disables event picking in empty space,
     * and installs the clip/size listeners.
     */
    public VirtualPane() {
        weakSizeListener = new WeakInvalidationListener(sizeListener);
        initialize();
    }

    /**
     * Initializes this {@code VirtualPane} after construction.
     * <p>
     * Responsibilities:
     * <ul>
     *   <li>Adds a custom style class ({@code "virtual-pane"}) so CSS can target it.</li>
     *   <li>Configures pixel snapping for crisp rendering of child nodes.</li>
     *   <li>Disables picking on empty bounds so only actual children respond to mouse events.</li>
     *   <li>Installs a {@link javafx.scene.shape.Rectangle} clip and binds its size
     *       to the pane’s width/height to prevent children from painting outside
     *       the viewport (avoids edge flicker during scroll/resize).</li>
     * </ul>
     */
    private void initialize(){
        getStyleClass().add("virtual-pane");
        // We position/size children manually; Pane lets us expose getChildren() publicly.
        setSnapToPixel(true);           // snap to pixel grid for crisp visuals
        setPickOnBounds(false);         // don't consume events in empty areas

        // Install and bind clip to our size; this is critical to avoid edge flicker.
        setClip(clip);
        widthProperty().addListener(weakSizeListener);
        heightProperty().addListener(weakSizeListener);
    }


    /**
     * Efficiently sets the virtual content height (e.g., totalRows * cellHeight).
     * <p>
     * Applies the new value only if it differs significantly from the last applied
     * height, to prevent layout thrashing on small floating-point deltas.
     *
     * @param contentHeight the total virtual content height
     */
    public void setContentHeight(double contentHeight) {
        // guard & threshold to avoid thrashing on tiny floating deltas
        final double h = Math.max(0.0, contentHeight);
        if (Math.abs(h - lastContentHeight) > 0.5) {
            setMinHeight(h);
            setPrefHeight(h);
            lastContentHeight = h;
        }
    }

    /**
     * Begins a batch mutation session.
     * <p>
     * While batching, calls to {@link #requestLayout()} are suppressed.
     * Call {@link #endBatch()} to finish the batch and trigger a single layout pass.
     */
    public void beginBatch() { batching = true; }

    /**
     * Ends a batch mutation session and requests a single layout pass.
     */
    public void endBatch() {
        if (!batching) return;
        batching = false;
        super.requestLayout();
    }


    /**
     * Overrides {@link Pane#requestLayout()} to suppress redundant
     * layout requests while in batch mode.
     */
    @Override
    public void requestLayout() {
        if (batching) return;
        super.requestLayout();
    }

    /**
     * Provides public access to the list of child nodes.
     * <p>
     * Although {@link Pane} already exposes {@code getChildren()}, this
     * explicit override improves clarity and allows for future customization.
     *
     * @return the observable list of child nodes
     */
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }


    /**
     * No-op layout method.
     * <p>
     * Children are positioned manually by the parent logic outside of this class,
     * so the default layout mechanism is intentionally bypassed.
     */
    @Override
    protected void layoutChildren() {
        // no-op; cells are positioned by the parent logic
    }

    /**
     * Clears all child nodes and resets internal counters.
     * <p>
     * Useful for hard resets or when reloading content.
     * Resets the content height and requests a layout pass at the end of the batch.
     */
    public void reset() {
        beginBatch();
        try {
            getChildren().clear();
            lastContentHeight = -1;
            setMinHeight(0);
            setPrefHeight(0);
        } finally {
            endBatch();
        }
    }
}