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

package xss.it.nfx.responsive.layout;

import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.Arrays;
import java.util.List;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.nfx.responsive.layout package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 05, 2025
 * <p>
 * Created on 09/05/2025 at 01:11
 */
@DefaultProperty("children")
public final class NfxGridLayout extends Pane {

    /**
     * Maximum number of columns supported by this container's grid system.
     * <p>
     * This value is fixed at 12, following the convention of popular
     * responsive frameworks (e.g., Bootstrap).
     */
    private static final int MAX_COLS = 12;

    /**
     * The fixed breakpoint threshold for the extra-small (XS) range.
     * <p>
     * This value is always {@code 0} and is immutable. It represents
     * the baseline for mobile-first layouts: any width below the
     * {@link #getSmBreakpoint() SM threshold} is considered "XS".
     */
    private static final double XS_BREAKPOINT = 0;

    /**
     * Creates a new {@code NfxGridLayout}.
     * <p>
     * The constructor installs a listener on the {@link #getChildren()} list
     * to ensure that only {@link NfxGridItem} instances can be added. Any attempt
     * to add a different node type will be automatically rejected and the node
     * will be removed immediately.
     */
    public NfxGridLayout() {
        super();
        initialize();
    }

    /**
     * Installs a listener on this container's children list that enforces
     * type safety for its contents.
     * <p>
     * Whenever nodes are added to {@link #getChildren()}:
     * <ul>
     *   <li>If the node is an {@link NfxGridItem}, it is accepted and kept.</li>
     *   <li>If the node is <em>not</em> an {@code NfxGridItem}, it is removed
     *       immediately from the list, effectively preventing unsupported
     *       child types from being hosted in this container.</li>
     * </ul>
     * <p>
     * This guarantees that the layout algorithm can safely assume that
     * all children are valid {@code NfxGridItem} instances.
     */
    private void initialize() {
        getChildren().addListener((ListChangeListener<? super Node>) ch->{
            while (ch.next()){
                if(ch.wasAdded()){
                    var added = ch.getAddedSubList();
                    for (Node node : added) {
                        if (!(node instanceof NfxGridItem)){
                            getChildren().remove(node);
                        }
                    }
                }
            }
        });
    }

    /**
     * Gets the extra-small (XS) breakpoint threshold.
     * <p>
     * This method always returns {@code 0}, as the XS breakpoint
     * is not configurable.
     *
     * @return the XS breakpoint threshold (always 0)
     */
    public double getXsBreakpoint() {
        return XS_BREAKPOINT;
    }

// ===== SM (clamped between XS and MD) =====

    /**
     * Backing property for the small (SM) breakpoint threshold.
     * <p>
     * This value represents the minimum width, in pixels, at which the
     * "small" breakpoint becomes active. The default value is 576, which
     * follows common responsive design conventions (e.g., Bootstrap).
     * <p>
     * The value is automatically clamped to ensure consistency:
     * <ul>
     *   <li>It can never be lower than {@link #getXsBreakpoint()} (always 0).</li>
     *   <li>It can never exceed the current {@link #getMdBreakpoint()} value.</li>
     * </ul>
     */
    private DoubleProperty smBreakpoint;

    /**
     * Gets the small (SM) breakpoint threshold.
     *
     * @return the current SM breakpoint threshold in pixels
     */
    public double getSmBreakpoint() { return smBreakpointProperty().get(); }

    /**
     * Property accessor for the SM breakpoint threshold.
     * <p>
     * The property is lazily instantiated on first access.
     * Its setter is overridden to enforce clamping between
     * {@link #getXsBreakpoint()} and {@link #getMdBreakpoint()}.
     *
     * @return the {@link DoubleProperty} representing the SM breakpoint
     */
    public DoubleProperty smBreakpointProperty() {
        if (smBreakpoint == null) {
            smBreakpoint = new SimpleDoubleProperty(this, "smBreakpoint", 576) {
                @Override public void set(double v) {
                    double min = getXsBreakpoint();
                    double max = getMdBreakpoint(); // neighbor getter
                    super.set(clamp(v, min, max));
                }
            };
        }
        return smBreakpoint;
    }

    /**
     * Sets the small (SM) breakpoint threshold.
     * <p>
     * The new value will be automatically clamped between
     * {@link #getXsBreakpoint()} and {@link #getMdBreakpoint()}.
     *
     * @param v the new SM breakpoint value in pixels
     */
    public void setSmBreakpoint(double v) { smBreakpointProperty().set(v); }


// ===== MD (clamped between SM and LG) =====

    /**
     * Backing property for the medium (MD) breakpoint threshold.
     * <p>
     * This value represents the minimum width, in pixels, at which the
     * "medium" breakpoint becomes active. The default value is 768,
     * consistent with common responsive design systems (e.g., Bootstrap).
     * <p>
     * The value is automatically clamped to ensure consistency:
     * <ul>
     *   <li>It can never be lower than the current {@link #getSmBreakpoint()} value.</li>
     *   <li>It can never exceed the current {@link #getLgBreakpoint()} value.</li>
     * </ul>
     */
    private DoubleProperty mdBreakpoint;

    /**
     * Gets the medium (MD) breakpoint threshold.
     *
     * @return the current MD breakpoint threshold in pixels
     */
    public double getMdBreakpoint() { return mdBreakpointProperty().get(); }

    /**
     * Property accessor for the MD breakpoint threshold.
     * <p>
     * The property is lazily instantiated on first access.
     * Its setter is overridden to enforce clamping between
     * {@link #getSmBreakpoint()} and {@link #getLgBreakpoint()}.
     *
     * @return the {@link DoubleProperty} representing the MD breakpoint
     */
    public DoubleProperty mdBreakpointProperty() {
        if (mdBreakpoint == null) {
            mdBreakpoint = new SimpleDoubleProperty(this, "mdBreakpoint", 768) {
                @Override public void set(double v) {
                    double min = getSmBreakpoint();
                    double max = getLgBreakpoint();
                    super.set(clamp(v, min, max));
                }
            };
        }
        return mdBreakpoint;
    }

    /**
     * Sets the medium (MD) breakpoint threshold.
     * <p>
     * The new value will be automatically clamped between
     * {@link #getSmBreakpoint()} and {@link #getLgBreakpoint()}.
     *
     * @param v the new MD breakpoint value in pixels
     */
    public void setMdBreakpoint(double v) { mdBreakpointProperty().set(v); }


// ===== LG (clamped between MD and XL) =====

    /**
     * Backing property for the large (LG) breakpoint threshold.
     * <p>
     * This value represents the minimum width, in pixels, at which the
     * "large" breakpoint becomes active. The default value is 992,
     * following common responsive design conventions (e.g., Bootstrap).
     * <p>
     * The value is automatically clamped to ensure consistency:
     * <ul>
     *   <li>It can never be lower than the current {@link #getMdBreakpoint()} value.</li>
     *   <li>It can never exceed the current {@link #getXlBreakpoint()} value.</li>
     * </ul>
     */
    private DoubleProperty lgBreakpoint;

    /**
     * Gets the large (LG) breakpoint threshold.
     *
     * @return the current LG breakpoint threshold in pixels
     */
    public double getLgBreakpoint() { return lgBreakpointProperty().get(); }

    /**
     * Property accessor for the LG breakpoint threshold.
     * <p>
     * The property is lazily instantiated on first access.
     * Its setter is overridden to enforce clamping between
     * {@link #getMdBreakpoint()} and {@link #getXlBreakpoint()}.
     *
     * @return the {@link DoubleProperty} representing the LG breakpoint
     */
    public DoubleProperty lgBreakpointProperty() {
        if (lgBreakpoint == null) {
            lgBreakpoint = new SimpleDoubleProperty(this, "lgBreakpoint", 992) {
                @Override public void set(double v) {
                    double min = getMdBreakpoint();
                    double max = getXlBreakpoint();
                    super.set(clamp(v, min, max));
                }
            };
        }
        return lgBreakpoint;
    }

    /**
     * Sets the large (LG) breakpoint threshold.
     * <p>
     * The new value will be automatically clamped between
     * {@link #getMdBreakpoint()} and {@link #getXlBreakpoint()}.
     *
     * @param v the new LG breakpoint value in pixels
     */
    public void setLgBreakpoint(double v) { lgBreakpointProperty().set(v); }


// ===== XL (clamped between LG and XXL) =====

    /**
     * Backing property for the extra-large (XL) breakpoint threshold.
     * <p>
     * This value represents the minimum width, in pixels, at which the
     * "extra-large" breakpoint becomes active. The default value is 1200,
     * following common responsive design systems (e.g., Bootstrap).
     * <p>
     * The value is automatically clamped to ensure consistency:
     * <ul>
     *   <li>It can never be lower than the current {@link #getLgBreakpoint()} value.</li>
     *   <li>It can never exceed the current {@link #getXxlBreakpoint()} value.</li>
     * </ul>
     */
    private DoubleProperty xlBreakpoint;

    /**
     * Gets the extra-large (XL) breakpoint threshold.
     *
     * @return the current XL breakpoint threshold in pixels
     */
    public double getXlBreakpoint() { return xlBreakpointProperty().get(); }

    /**
     * Property accessor for the XL breakpoint threshold.
     * <p>
     * The property is lazily instantiated on first access.
     * Its setter is overridden to enforce clamping between
     * {@link #getLgBreakpoint()} and {@link #getXxlBreakpoint()}.
     *
     * @return the {@link DoubleProperty} representing the XL breakpoint
     */
    public DoubleProperty xlBreakpointProperty() {
        if (xlBreakpoint == null) {
            xlBreakpoint = new SimpleDoubleProperty(this, "xlBreakpoint", 1200) {
                @Override public void set(double v) {
                    double min = getLgBreakpoint();
                    double max = getXxlBreakpoint();
                    super.set(clamp(v, min, max));
                }
            };
        }
        return xlBreakpoint;
    }

    /**
     * Sets the extra-large (XL) breakpoint threshold.
     * <p>
     * The new value will be automatically clamped between
     * {@link #getLgBreakpoint()} and {@link #getXxlBreakpoint()}.
     *
     * @param v the new XL breakpoint value in pixels
     */
    public void setXlBreakpoint(double v) { xlBreakpointProperty().set(v); }


// ===== XXL (clamped between XL and +∞) =====

    /**
     * Backing property for the extra-extra-large (XXL) breakpoint threshold.
     * <p>
     * This value represents the minimum width, in pixels, at which the
     * "extra-extra-large" breakpoint becomes active. The default value is 1400,
     * matching conventions from common responsive frameworks (e.g., Bootstrap).
     * <p>
     * The value is automatically clamped to ensure consistency:
     * <ul>
     *   <li>It can never be lower than the current {@link #getXlBreakpoint()} value.</li>
     *   <li>It has no strict upper limit and may grow arbitrarily large.</li>
     * </ul>
     */
    private DoubleProperty xxlBreakpoint;

    /**
     * Gets the extra-extra-large (XXL) breakpoint threshold.
     *
     * @return the current XXL breakpoint threshold in pixels
     */
    public double getXxlBreakpoint() { return xxlBreakpointProperty().get(); }

    /**
     * Property accessor for the XXL breakpoint threshold.
     * <p>
     * The property is lazily instantiated on first access.
     * Its setter is overridden to enforce clamping with a lower bound
     * of {@link #getXlBreakpoint()} and no finite upper bound.
     *
     * @return the {@link DoubleProperty} representing the XXL breakpoint
     */
    public DoubleProperty xxlBreakpointProperty() {
        if (xxlBreakpoint == null) {
            xxlBreakpoint = new SimpleDoubleProperty(this, "xxlBreakpoint", 1400) {
                @Override public void set(double v) {
                    double min = getXlBreakpoint();
                    double max = Double.MAX_VALUE; // no upper neighbor
                    super.set(clamp(v, min, max));
                }
            };
        }
        return xxlBreakpoint;
    }

    /**
     * Sets the extra-extra-large (XXL) breakpoint threshold.
     * <p>
     * The new value will be automatically clamped so it cannot
     * be lower than {@link #getXlBreakpoint()}, but has no upper bound.
     *
     * @param v the new XXL breakpoint value in pixels
     */
    public void setXxlBreakpoint(double v) { xxlBreakpointProperty().set(v); }



// --- currentBreakpoint ---

    /**
     * Read-only property representing the currently active breakpoint label.
     * <p>
     * The value is one of: {@code "XS"}, {@code "SM"}, {@code "MD"},
     * {@code "LG"}, {@code "XL"}, or {@code "XXL"} depending on the
     * container's current width relative to the configured thresholds.
     * <p>
     * This property is automatically updated inside {@link #resize(double, double)}.
     * <ul>
     *   <li>Default value is {@code "XS"}.</li>
     *   <li>Can be observed for layout changes when the container is resized.</li>
     * </ul>
     */
    private final ReadOnlyStringWrapper currentBreakpoint =
            new ReadOnlyStringWrapper(this, "currentBreakpoint", "XS");

    /**
     * Property accessor for the current breakpoint label.
     * <p>
     * Returns a read-only view of the internal {@link ReadOnlyStringWrapper}.
     *
     * @return a {@link ReadOnlyStringProperty} with values like
     *         {@code "XS"}, {@code "SM"}, {@code "MD"}, {@code "LG"},
     *         {@code "XL"}, or {@code "XXL"}
     */
    public ReadOnlyStringProperty currentBreakpointProperty() {
        return currentBreakpoint.getReadOnlyProperty();
    }

    /**
     * Gets the current breakpoint label.
     *
     * @return the current breakpoint string
     */
    public String getCurrentBreakpoint() {
        return currentBreakpoint.get();
    }


    /**
     * Resizes this container to the specified width and height.
     * <p>
     * In addition to delegating to {@link Region#resize(double, double)},
     * this method also recalculates and updates the {@link #currentBreakpoint}
     * property based on the new width.
     * <p>
     * The active breakpoint is resolved by calling
     * {@link #resolveBreakpointLabel(double)} with the provided width.
     * This ensures that whenever the container is resized, the current
     * responsive breakpoint is always kept in sync.
     *
     * @param width  the new width of the container
     * @param height the new height of the container
     */
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        currentBreakpoint.set(resolveBreakpointLabel(width));
    }

    /**
     * Lays out the managed children of this container according to the current
     * responsive breakpoint and the configured grid system.
     * <p>
     * The layout algorithm uses a 12-column grid (see {@link #MAX_COLS})
     * and distributes {@link NfxGridItem} children into rows:
     * <ul>
     *   <li>Each child specifies a <em>column span</em> and <em>column offset</em>
     *       for the active breakpoint via its properties.</li>
     *   <li>If a child cannot fit in the remaining space of the current row,
     *       the row is flushed and a new row is started.</li>
     *   <li>Each row’s height is determined by the tallest child in that row.</li>
     *   <li>Children are positioned using {@code resizeRelocate} with their
     *       computed x-coordinate, width (span × column width), and height.</li>
     * </ul>
     * <p>
     * Insets (padding) are respected, and the container’s preferred height
     * is updated based on the cumulative row heights plus bottom inset.
     * <p>
     * Only {@link NfxGridItem} children are laid out; other node types are ignored.
     */
    @Override
    protected void layoutChildren() {
        final Insets in = getInsets();
        final double left   = in.getLeft();
        final double top    = in.getTop();
        final double right  = in.getRight();
        final double bottom = in.getBottom();

        final double contentW = Math.max(0, getWidth() - left - right);
        final double colW     = (MAX_COLS > 0) ? contentW / MAX_COLS : 0;

        final String bp = getCurrentBreakpoint();
        final List<Node> kids = getManagedChildren();

        // Column "skyline": bottom Y per column
        final double[] colHeights = new double[MAX_COLS];
        Arrays.fill(colHeights, top);

        for (Node child : kids) {
            if (!(child instanceof NfxGridItem item)) continue;

            // responsive span/offset
            int span = Math.max(1, Math.min(MAX_COLS, colSpan(item, bp)));
            int minStart = Math.max(0, Math.min(MAX_COLS - 1, colOffset(item, bp))); // treat offset as MIN start col
            int maxStart = Math.max(0, MAX_COLS - span);                              // last valid start
            final int startFrom = Math.min(minStart, maxStart);

            // per-item 4-sided gaps (clamped to >= 0)
            final double leftGap   = Math.max(0, item.getLeftGap());
            final double rightGap  = Math.max(0, item.getRightGap());
            final double topGap    = Math.max(0, item.getTopGap());
            final double bottomGap = Math.max(0, item.getBottomGap());

            // choose best start column >= minStart that yields the lowest y across span
            int bestCol = -1;
            double bestY = Double.MAX_VALUE;

            for (int c = startFrom; c <= maxStart; c++) {
                double y = 0;
                for (int k = c; k < c + span; k++) y = Math.max(y, colHeights[k]);
                if (y < bestY || (y == bestY && (bestCol < 0 || c < bestCol))) {
                    bestY = y;
                    bestCol = c;
                }
            }
            if (bestCol < 0) { // fallback (shouldn't happen)
                bestCol = startFrom;
                bestY   = colHeights[bestCol];
            }

            final double trackW = span * colW;

            // effective width inside the track after left/right gaps
            final double lrGaps = leftGap + rightGap;
            final double nodeW  = (lrGaps >= trackW) ? 0 : (trackW - lrGaps);

            // position includes left/top gaps
            final double x = left + bestCol * colW + leftGap;
            final double y = bestY + topGap;

            final double nodeH = item.prefHeight(nodeW);
            item.resizeRelocate(x, y, nodeW, nodeH);

            // update skyline for all spanned columns (include bottom gap)
            final double newBottom = y + nodeH + bottomGap;
            for (int k = bestCol; k < bestCol + span; k++) {
                colHeights[k] = Math.max(colHeights[k], newBottom);
            }
        }

        // container height = tallest column + bottom inset
        double maxBottom = top;
        for (double ch : colHeights) maxBottom = Math.max(maxBottom, ch);
        setPrefHeight(maxBottom + bottom);
    }



    /*
     * ==========================================HELPERS================================================================
     */

    /**
     * Resolves the column span of a given {@link NfxGridItem} for the specified breakpoint.
     * <p>
     * Each item defines separate span properties for each breakpoint tier.
     * This method selects the appropriate property based on the active breakpoint label:
     * <ul>
     *   <li>{@code "XS"} → {@link NfxGridItem#getXsColSpan()}</li>
     *   <li>{@code "SM"} → {@link NfxGridItem#getSmColSpan()}</li>
     *   <li>{@code "MD"} → {@link NfxGridItem#getMdColSpan()}</li>
     *   <li>{@code "LG"} → {@link NfxGridItem#getLgColSpan()}</li>
     *   <li>{@code "XL"} → {@link NfxGridItem#getXlColSpan()}</li>
     *   <li>{@code "XXL"} → {@link NfxGridItem#getXxlColSpan()}</li>
     * </ul>
     * <p>
     * If the provided breakpoint label does not match one of the known values,
     * the extra-small (XS) span is returned by default.
     *
     * @param item the {@link NfxGridItem} whose span is to be resolved
     * @param bp   the current breakpoint label ("XS", "SM", "MD", "LG", "XL", or "XXL")
     * @return the column span value for the given item and breakpoint
     */
    private int colSpan(NfxGridItem item, String bp) {
        return switch (bp) {
            case "SM"  -> item.getSmColSpan();
            case "MD"  -> item.getMdColSpan();
            case "LG"  -> item.getLgColSpan();
            case "XL"  -> item.getXlColSpan();
            case "XXL" -> item.getXxlColSpan();
            default    -> item.getXsColSpan();
        };
    }

    /**
     * Resolves the column offset of a given {@link NfxGridItem} for the specified breakpoint.
     * <p>
     * Each item defines separate offset properties for each breakpoint tier.
     * This method selects the appropriate property based on the active breakpoint label:
     * <ul>
     *   <li>{@code "XS"} → {@link NfxGridItem#getXsColOffset()}</li>
     *   <li>{@code "SM"} → {@link NfxGridItem#getSmColOffset()}</li>
     *   <li>{@code "MD"} → {@link NfxGridItem#getMdColOffset()}</li>
     *   <li>{@code "LG"} → {@link NfxGridItem#getLgColOffset()}</li>
     *   <li>{@code "XL"} → {@link NfxGridItem#getXlColOffset()}</li>
     *   <li>{@code "XXL"} → {@link NfxGridItem#getXxlColOffset()}</li>
     * </ul>
     * <p>
     * If the provided breakpoint label does not match one of the known values,
     * the extra-small (XS) offset is returned by default.
     *
     * @param item the {@link NfxGridItem} whose offset is to be resolved
     * @param bp   the current breakpoint label ("XS", "SM", "MD", "LG", "XL", or "XXL")
     * @return the column offset value for the given item and breakpoint
     */
    private int colOffset(NfxGridItem item, String bp) {
        return switch (bp) {
            case "SM"  -> item.getSmColOffset();
            case "MD"  -> item.getMdColOffset();
            case "LG"  -> item.getLgColOffset();
            case "XL"  -> item.getXlColOffset();
            case "XXL" -> item.getXxlColOffset();
            default    -> item.getXsColOffset();
        };
    }

    /**
     * Resolves the active breakpoint label based on the given container width.
     * <p>
     * The method applies a <em>mobile-first</em> strategy: it selects the
     * largest breakpoint whose threshold is less than or equal to the width.
     * <ul>
     *   <li>{@code "XXL"} if width ≥ {@link #getXxlBreakpoint()}</li>
     *   <li>{@code "XL"}  if width ≥ {@link #getXlBreakpoint()}</li>
     *   <li>{@code "LG"}  if width ≥ {@link #getLgBreakpoint()}</li>
     *   <li>{@code "MD"}  if width ≥ {@link #getMdBreakpoint()}</li>
     *   <li>{@code "SM"}  if width ≥ {@link #getSmBreakpoint()}</li>
     *   <li>{@code "XS"}  otherwise (default, width ≥ 0)</li>
     * </ul>
     *
     * @param w the current container width in pixels
     * @return a string label representing the resolved breakpoint
     *         ({@code "XS"}, {@code "SM"}, {@code "MD"},
     *          {@code "LG"}, {@code "XL"}, or {@code "XXL"})
     */
    private String resolveBreakpointLabel(double w) {
        if (w >= getXxlBreakpoint()) return "XXL";
        if (w >= getXlBreakpoint())  return "XL";
        if (w >= getLgBreakpoint())  return "LG";
        if (w >= getMdBreakpoint())  return "MD";
        if (w >= getSmBreakpoint())  return "SM";
        return "XS";
    }

    /**
     * Utility method to constrain a numeric value within the given bounds.
     * <p>
     * If {@code v} is less than {@code min}, the result will be {@code min}.
     * If {@code v} is greater than {@code max}, the result will be {@code max}.
     * Otherwise, {@code v} is returned unchanged.
     *
     * @param v   the value to clamp
     * @param min the minimum allowable value
     * @param max the maximum allowable value
     * @return {@code v} constrained to the range [{@code min}, {@code max}]
     */
    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
