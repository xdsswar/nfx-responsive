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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

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
 * Created on 09/05/2025 at 01:12
 */
@DefaultProperty("children")
public final class NfxGridItem extends StackPane {
    /**
     * Max col number
     */
    private static final int MAX_COLS = 12;

    /**
     * Creates a new {@code NfxGridItem}.
     * <p>
     * The constructor calls {@link #initialize()}, which installs a listener on the
     * {@link #parentProperty()} to enforce parent type restrictions.
     */
    public NfxGridItem() {
        super();
        setCache(false);
        initialize();
    }

    /**
     * Installs a listener on this node's {@link #parentProperty()}.
     * <p>
     * Whenever the parent of this {@code NfxGridItem} changes:
     * <ul>
     *   <li>If the new parent is a {@link Pane} (and not the expected container type),
     *       this item is immediately removed from that parent.</li>
     *   <li>Otherwise, no action is taken.</li>
     * </ul>
     * <p>
     * This pattern can be used to ensure that {@code NfxGridItem} nodes are only ever
     * placed inside specific container types (for example, {@code NfxPane} or
     * {@code NfxContainer}), and will auto-remove themselves if added elsewhere.
     */
    private void initialize(){
        parentProperty().addListener((obs, o, p) -> {
            if (!(p instanceof NfxGridLayout) && p instanceof Pane pane){
                pane.getChildren().remove(this);
            }
        });
    }


    // ===== XS =====

    /**
     * Backing property for the extra-small breakpoint column span.
     * Default value = MAX_COLS (full width).
     */
    private IntegerProperty xsColSpan;

    /**
     * Gets the number of columns this item spans at the extra-small (XS) breakpoint.
     *
     * @return the current XS column span (default MAX_COLS)
     */
    public int getXsColSpan() {
        return xsColSpanProperty().get();
    }

    /**
     * Property accessor for the XS column span.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = MAX_COLS.
     *
     * @return the {@link IntegerProperty} representing the XS column span
     */
    public IntegerProperty xsColSpanProperty() {
        if (xsColSpan == null) {
            xsColSpan = new SimpleIntegerProperty(this, "xsColSpan", MAX_COLS){
                @Override
                public void set(int i) {
                    super.set(ensure(i));
                }
            };
        }
        return xsColSpan;
    }

    /**
     * Sets the number of columns this item spans at the extra-small (XS) breakpoint.
     *
     * @param v the new column span value (typically clamped 1–MAX_COLS)
     */
    public void setXsColSpan(int v) {
        xsColSpanProperty().set(v);
    }

    /**
     * Backing property for the extra-small breakpoint column offset.
     * Default value = 0 (no offset).
     */
    private IntegerProperty xsColOffset;

    /**
     * Gets the column offset at the extra-small (XS) breakpoint.
     *
     * @return the current XS column offset (default 0)
     */
    public int getXsColOffset() {
        return xsColOffsetProperty().get();
    }

    /**
     * Property accessor for the XS column offset.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link IntegerProperty} representing the XS column offset
     */
    public IntegerProperty xsColOffsetProperty() {
        if (xsColOffset == null) {
            xsColOffset = new SimpleIntegerProperty(this, "xsColOffset", 0);
        }
        return xsColOffset;
    }

    /**
     * Sets the column offset at the extra-small (XS) breakpoint.
     *
     * @param v the new offset value (typically clamped 0–11)
     */
    public void setXsColOffset(int v) {
        xsColOffsetProperty().set(v);
    }

// ===== SM =====

    /**
     * Backing property for the small breakpoint column span.
     * Default value = MAX_COLS (full width).
     */
    private IntegerProperty smColSpan;

    /**
     * Gets the number of columns this item spans at the small (SM) breakpoint.
     *
     * @return the current SM column span (default MAX_COLS)
     */
    public int getSmColSpan() {
        return smColSpanProperty().get();
    }

    /**
     * Property accessor for the SM column span.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = MAX_COLS.
     *
     * @return the {@link IntegerProperty} representing the SM column span
     */
    public IntegerProperty smColSpanProperty() {
        if (smColSpan == null) {
            smColSpan = new SimpleIntegerProperty(this, "smColSpan", MAX_COLS){
                @Override
                public void set(int i) {
                    super.set(ensure(i));
                }
            };
        }
        return smColSpan;
    }

    /**
     * Sets the number of columns this item spans at the small (SM) breakpoint.
     *
     * @param v the new column span value (typically clamped 1–MAX_COLS)
     */
    public void setSmColSpan(int v) {
        smColSpanProperty().set(v);
    }

    /**
     * Backing property for the small breakpoint column offset.
     * Default value = 0 (no offset).
     */
    private IntegerProperty smColOffset;

    /**
     * Gets the column offset at the small (SM) breakpoint.
     *
     * @return the current SM column offset (default 0)
     */
    public int getSmColOffset() {
        return smColOffsetProperty().get();
    }

    /**
     * Property accessor for the SM column offset.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link IntegerProperty} representing the SM column offset
     */
    public IntegerProperty smColOffsetProperty() {
        if (smColOffset == null) {
            smColOffset = new SimpleIntegerProperty(this, "smColOffset", 0);
        }
        return smColOffset;
    }

    /**
     * Sets the column offset at the small (SM) breakpoint.
     *
     * @param v the new offset value (typically clamped 0–11)
     */
    public void setSmColOffset(int v) {
        smColOffsetProperty().set(v);
    }

    // ===== MD =====

    /**
     * Backing property for the medium breakpoint column span.
     * Default value = MAX_COLS (full width).
     */
    private IntegerProperty mdColSpan;

    /**
     * Gets the number of columns this item spans at the medium (MD) breakpoint.
     *
     * @return the current MD column span (default MAX_COLS)
     */
    public int getMdColSpan() {
        return mdColSpanProperty().get();
    }

    /**
     * Property accessor for the MD column span.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = MAX_COLS.
     *
     * @return the {@link IntegerProperty} representing the MD column span
     */
    public IntegerProperty mdColSpanProperty() {
        if (mdColSpan == null) {
            mdColSpan = new SimpleIntegerProperty(this, "mdColSpan", MAX_COLS){
                @Override
                public void set(int i) {
                    super.set(ensure(i));
                }
            };
        }
        return mdColSpan;
    }

    /**
     * Sets the number of columns this item spans at the medium (MD) breakpoint.
     *
     * @param v the new column span value (typically clamped 1–MAX_COLS)
     */
    public void setMdColSpan(int v) {
        mdColSpanProperty().set(v);
    }

    /**
     * Backing property for the medium breakpoint column offset.
     * Default value = 0 (no offset).
     */
    private IntegerProperty mdColOffset;

    /**
     * Gets the column offset at the medium (MD) breakpoint.
     *
     * @return the current MD column offset (default 0)
     */
    public int getMdColOffset() {
        return mdColOffsetProperty().get();
    }

    /**
     * Property accessor for the MD column offset.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link IntegerProperty} representing the MD column offset
     */
    public IntegerProperty mdColOffsetProperty() {
        if (mdColOffset == null) {
            mdColOffset = new SimpleIntegerProperty(this, "mdColOffset", 0);
        }
        return mdColOffset;
    }

    /**
     * Sets the column offset at the medium (MD) breakpoint.
     *
     * @param v the new offset value (typically clamped 0–11)
     */
    public void setMdColOffset(int v) {
        mdColOffsetProperty().set(v);
    }

    // ===== LG =====

    /**
     * Backing property for the large breakpoint column span.
     * Default value = MAX_COLS (full width).
     */
    private IntegerProperty lgColSpan;

    /**
     * Gets the number of columns this item spans at the large (LG) breakpoint.
     *
     * @return the current LG column span (default MAX_COLS)
     */
    public int getLgColSpan() {
        return lgColSpanProperty().get();
    }

    /**
     * Property accessor for the LG column span.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = MAX_COLS.
     *
     * @return the {@link IntegerProperty} representing the LG column span
     */
    public IntegerProperty lgColSpanProperty() {
        if (lgColSpan == null) {
            lgColSpan = new SimpleIntegerProperty(this, "lgColSpan", MAX_COLS){
                @Override
                public void set(int i) {
                    super.set(ensure(i));
                }
            };
        }
        return lgColSpan;
    }

    /**
     * Sets the number of columns this item spans at the large (LG) breakpoint.
     *
     * @param v the new column span value (typically clamped 1–MAX_COLS)
     */
    public void setLgColSpan(int v) {
        lgColSpanProperty().set(v);
    }

    /**
     * Backing property for the large breakpoint column offset.
     * Default value = 0 (no offset).
     */
    private IntegerProperty lgColOffset;

    /**
     * Gets the column offset at the large (LG) breakpoint.
     *
     * @return the current LG column offset (default 0)
     */
    public int getLgColOffset() {
        return lgColOffsetProperty().get();
    }

    /**
     * Property accessor for the LG column offset.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link IntegerProperty} representing the LG column offset
     */
    public IntegerProperty lgColOffsetProperty() {
        if (lgColOffset == null) {
            lgColOffset = new SimpleIntegerProperty(this, "lgColOffset", 0);
        }
        return lgColOffset;
    }

    /**
     * Sets the column offset at the large (LG) breakpoint.
     *
     * @param v the new offset value (typically clamped 0–11)
     */
    public void setLgColOffset(int v) {
        lgColOffsetProperty().set(v);
    }


    // ===== XL =====

    /**
     * Backing property for the extra-large breakpoint column span.
     * Default value = MAX_COLS (full width).
     */
    private IntegerProperty xlColSpan;

    /**
     * Gets the number of columns this item spans at the extra-large (XL) breakpoint.
     *
     * @return the current XL column span (default MAX_COLS)
     */
    public int getXlColSpan() {
        return xlColSpanProperty().get();
    }

    /**
     * Property accessor for the XL column span.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = MAX_COLS.
     *
     * @return the {@link IntegerProperty} representing the XL column span
     */
    public IntegerProperty xlColSpanProperty() {
        if (xlColSpan == null) {
            xlColSpan = new SimpleIntegerProperty(this, "xlColSpan", MAX_COLS){
                @Override
                public void set(int i) {
                    super.set(ensure(i));
                }
            };
        }
        return xlColSpan;
    }

    /**
     * Sets the number of columns this item spans at the extra-large (XL) breakpoint.
     *
     * @param v the new column span value (typically clamped 1–MAX_COLS)
     */
    public void setXlColSpan(int v) {
        xlColSpanProperty().set(v);
    }

    /**
     * Backing property for the extra-large breakpoint column offset.
     * Default value = 0 (no offset).
     */
    private IntegerProperty xlColOffset;

    /**
     * Gets the column offset at the extra-large (XL) breakpoint.
     *
     * @return the current XL column offset (default 0)
     */
    public int getXlColOffset() {
        return xlColOffsetProperty().get();
    }

    /**
     * Property accessor for the XL column offset.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link IntegerProperty} representing the XL column offset
     */
    public IntegerProperty xlColOffsetProperty() {
        if (xlColOffset == null) {
            xlColOffset = new SimpleIntegerProperty(this, "xlColOffset", 0);
        }
        return xlColOffset;
    }

    /**
     * Sets the column offset at the extra-large (XL) breakpoint.
     *
     * @param v the new offset value (typically clamped 0–11)
     */
    public void setXlColOffset(int v) {
        xlColOffsetProperty().set(v);
    }


    // ===== XXL =====

    /**
     * Backing property for the extra-extra-large breakpoint column span.
     * Default value = MAX_COLS (full width).
     */
    private IntegerProperty xxlColSpan;

    /**
     * Gets the number of columns this item spans at the extra-extra-large (XXL) breakpoint.
     *
     * @return the current XXL column span (default MAX_COLS)
     */
    public int getXxlColSpan() {
        return xxlColSpanProperty().get();
    }

    /**
     * Property accessor for the XXL column span.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = MAX_COLS.
     *
     * @return the {@link IntegerProperty} representing the XXL column span
     */
    public IntegerProperty xxlColSpanProperty() {
        if (xxlColSpan == null) {
            xxlColSpan = new SimpleIntegerProperty(this, "xxlColSpan", MAX_COLS){
                @Override
                public void set(int i) {
                    super.set(ensure(i));
                }
            };
        }
        return xxlColSpan;
    }

    /**
     * Sets the number of columns this item spans at the extra-extra-large (XXL) breakpoint.
     *
     * @param v the new column span value (typically clamped 1–MAX_COLS)
     */
    public void setXxlColSpan(int v) {
        xxlColSpanProperty().set(v);
    }

    /**
     * Backing property for the extra-extra-large breakpoint column offset.
     * Default value = 0 (no offset).
     */
    private IntegerProperty xxlColOffset;

    /**
     * Gets the column offset at the extra-extra-large (XXL) breakpoint.
     *
     * @return the current XXL column offset (default 0)
     */
    public int getXxlColOffset() {
        return xxlColOffsetProperty().get();
    }

    /**
     * Property accessor for the XXL column offset.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link IntegerProperty} representing the XXL column offset
     */
    public IntegerProperty xxlColOffsetProperty() {
        if (xxlColOffset == null) {
            xxlColOffset = new SimpleIntegerProperty(this, "xxlColOffset", 0);
        }
        return xxlColOffset;
    }

    /**
     * Sets the column offset at the extra-extra-large (XXL) breakpoint.
     *
     * @param v the new offset value (typically clamped 0–11)
     */
    public void setXxlColOffset(int v) {
        xxlColOffsetProperty().set(v);
    }


    // --- Left gap ---------------------------------------------------------------

    /**
     * The left gap (in pixels) for this item.
     * <p>
     * Default value = 0.
     */
    private DoubleProperty leftGap;

    /**
     * Gets the left gap (in pixels) for this item.
     *
     * @return the current left gap (default 0)
     */
    public double getLeftGap() {
        return leftGapProperty().get();
    }

    /**
     * Property accessor for the left gap.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link javafx.beans.property.DoubleProperty} representing the left gap
     */
    public DoubleProperty leftGapProperty() {
        if (leftGap == null) {
            leftGap = new SimpleDoubleProperty(this, "leftGap", 0);
        }
        return leftGap;
    }

    /**
     * Sets the left gap (in pixels) for this item.
     *
     * @param v the new left gap
     */
    public void setLeftGap(double v) {
        leftGapProperty().set(v);
    }

    // --- Top gap ---------------------------------------------------------------

    /**
     * The top gap (in pixels) for this item.
     * <p>
     * Default value = 0.
     */
    private DoubleProperty topGap;

    /**
     * Gets the top gap (in pixels) for this item.
     *
     * @return the current top gap (default 0)
     */
    public double getTopGap() {
        return topGapProperty().get();
    }

    /**
     * Property accessor for the top gap.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link javafx.beans.property.DoubleProperty} representing the top gap
     */
    public DoubleProperty topGapProperty() {
        if (topGap == null) {
            topGap = new SimpleDoubleProperty(this, "topGap", 0);
        }
        return topGap;
    }

    /**
     * Sets the top gap (in pixels) for this item.
     *
     * @param v the new top gap
     */
    public void setTopGap(double v) {
        topGapProperty().set(v);
    }

// --- Right gap --------------------------------------------------------------

    /**
     * The right gap (in pixels) for this item.
     * <p>
     * Default value = 0.
     */
    private DoubleProperty rightGap;

    /**
     * Gets the right gap (in pixels) for this item.
     *
     * @return the current right gap (default 0)
     */
    public double getRightGap() {
        return rightGapProperty().get();
    }

    /**
     * Property accessor for the right gap.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link javafx.beans.property.DoubleProperty} representing the right gap
     */
    public DoubleProperty rightGapProperty() {
        if (rightGap == null) {
            rightGap = new SimpleDoubleProperty(this, "rightGap", 0);
        }
        return rightGap;
    }

    /**
     * Sets the right gap (in pixels) for this item.
     *
     * @param v the new right gap
     */
    public void setRightGap(double v) {
        rightGapProperty().set(v);
    }

// --- Bottom gap -------------------------------------------------------------

    /**
     * The bottom gap (in pixels) for this item.
     * <p>
     * Default value = 0.
     */
    private DoubleProperty bottomGap;

    /**
     * Gets the bottom gap (in pixels) for this item.
     *
     * @return the current bottom gap (default 0)
     */
    public double getBottomGap() {
        return bottomGapProperty().get();
    }

    /**
     * Property accessor for the bottom gap.
     * <p>
     * This property can be observed or bound in FXML/SceneBuilder.
     * Default value = 0.
     *
     * @return the {@link javafx.beans.property.DoubleProperty} representing the bottom gap
     */
    public DoubleProperty bottomGapProperty() {
        if (bottomGap == null) {
            bottomGap = new SimpleDoubleProperty(this, "bottomGap", 0);
        }
        return bottomGap;
    }

    /**
     * Sets the bottom gap (in pixels) for this item.
     *
     * @param v the new bottom gap
     */
    public void setBottomGap(double v) {
        bottomGapProperty().set(v);
    }

    /*
     * ==========================================HELPERS================================================================
     */

    /**
     * Ensures that a column span or offset value stays within valid bounds.
     * <p>
     * The returned value is clamped between {@code 1} and {@code MAX_COLS}, inclusive.
     * This guarantees that no span or offset can be less than one column or greater
     * than the maximum number of columns supported by the grid.
     *
     * @param val the value to validate
     * @return the clamped value, guaranteed to be within {@code [1, MAX_COLS]}
     */
    private int ensure(Integer val) {
        if (val == null) return MAX_COLS;
        return Math.max(Math.min(val, MAX_COLS), 1);
    }
}
