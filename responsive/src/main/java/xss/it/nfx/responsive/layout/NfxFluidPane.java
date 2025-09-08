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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @since September 07, 2025
 * <p>
 * Created on 09/07/2025 at 12:48
 * <p>
 *
 * A responsive, CSS-driven layout container similar to a fluid grid system
 * (inspired by frameworks like Bootstrap).
 * <p>
 * {@code NfxFluidPane} extends {@link javafx.scene.layout.Pane} and arranges
 * its direct children in a multi-column grid that adapts to breakpoints
 * (XS, SM, MD, LG, XL, XXL).
 *
 * <h2>Features</h2>
 * <ul>
 *   <li><b>Responsive breakpoints</b> — Six breakpoints (XS..XXL). Layout properties
 *       (column span, offset, margins/insets) can be defined per breakpoint.</li>
 *
 *   <li><b>Per-child constraints</b> — Each child can declare:
 *       <ul>
 *         <li>{@code -col-span}: how many grid columns it spans</li>
 *         <li>{@code -col-offset}: how many columns to offset before placement</li>
 *         <li>{@code -col-margin}: shorthand margins (Insets) around the child</li>
 *       </ul>
 *       Constraints can be set programmatically via static setters such as
 *       {@code setXsColSpan()}, {@code setSmColOffset()}, {@code setLgInsets()}, etc.</li>
 *
 *   <li><b>CSS-like stylesheet parsing</b> — Reads stylesheets (URL, classpath, filesystem)
 *       and extracts class-based rules. Selectors may include a breakpoint suffix,
 *       e.g. {@code .card:md} or {@code .header:xl}. Rules are applied to matching
 *       children based on their style classes.</li>
 *
 *   <li><b>Pseudo-class breakpoint styling</b> — Use breakpoint pseudo-classes in CSS
 *       to define visual skins or layout utilities per breakpoint without Java code.</li>
 *
 *   <li><b>Last-wins precedence</b> — When multiple rules apply, later ones override
 *       earlier ones (tracked internally by sequence numbers).</li>
 *
 *   <li><b>Insets shorthand</b> — {@code -col-margin} supports CSS-like shorthand:
 *       {@code a}, {@code v h}, {@code t h b}, {@code t r b l}.</li>
 *
 *   <li><b>Right-to-left support</b> — Row placement can honor RTL layout.</li>
 *
 *   <li><b>Automatic layout</b> — {@link #layoutChildren()} measures children,
 *       respects spans/offsets/insets, and places them in wrapping rows up to
 *       {@code MAX_COLS}.</li>
 * </ul>
 *
 * <h2>Usage Example (Programmatic)</h2>
 * <pre>{@code
 * NfxFluidPane pane = new NfxFluidPane();
 * Button a = new Button("A");
 * Button b = new Button("B");
 *
 * NfxFluidPane.setSmColSpan(a, 6);
 * NfxFluidPane.setSmColSpan(b, 6);
 *
 * pane.getChildren().addAll(a, b);
 * pane.getStylesheets().add("grid.css");
 * }</pre>
 *
 * <h2>Usage Example (CSS-only: visual skins with breakpoint pseudo-classes)</h2>
 * <pre>{@code
 * .box {
 *     -fx-background-color: #131722;
 *     -fx-background-radius: 18;
 *     -fx-padding: 18 16;
 * }
 *
 * .box:xs {
 *     -fx-background-color: linear-gradient(to bottom right, rgba(255,80,100,0.65), rgba(255,150,120,0.45));
 *     -fx-border-color: rgba(255,180,180,0.65);
 * }
 *
 * .box:lg {
 *     -fx-background-color: linear-gradient(to bottom right, rgba(0,180,255,0.55), rgba(120,180,255,0.40));
 *     -fx-border-color: rgba(110,170,255,0.65);
 * }
 * }</pre>
 *
 * <h2>Usage Example (CSS-only: layout utilities per breakpoint)</h2>
 * <pre>{@code
 * .card:xs  { -col-span: 12; }
 * .card:md  { -col-span: 6;  }
 * .card:xl  { -col-span: 4;  }
 *
 * .sidebar:lg { -col-span: 2; -col-offset: 1; }
 * .test:lg,
 * .test:xl,
 * .test:xxl { -col-span: 10; -col-offset: 2; -col-margin: 10; }
 * }</pre>
 *
 * <h2>Implementation Notes</h2>
 * <ul>
 *   <li>Breakpoint dispatch is centralized via helpers like {@code setSpanByBp()}.</li>
 *   <li>Parsing uses regex for blocks, selectors, and declarations.</li>
 *   <li>Stylesheet resolution order: URL → classpath → filesystem.</li>
 *   <li>Row helpers: {@code measureContentHeight()}, {@code placeRow()}, {@code clearRow()}.</li>
 * </ul>
 *
 * This class is {@code final}. Customize behavior via CSS and the provided setters.
 */
public final class NfxFluidPane extends Pane {
    /**
     * Represents the min of columns in the grid.
     */
    private static final int MIN_COLS = 1;

    /**
     * Represents the max number of columns in the grid.
     */
    private static final int MAX_COLS = 12;

    /**
     * Column count for extra-small screens.
     */
    private static final String EXTRA_SMALL_COLS = "xs-cols";

    /**
     * Column offset for extra-small screens.
     */
    private static final String EXTRA_SMALL_COL_OFFSET = "xs-col-offset";

    /**
     * Column count for small screens.
     */
    private static final String SMALL_COLS = "sm-cols";

    /**
     * Column offset for small screens.
     */
    private static final String SMALL_COL_OFFSET = "sm-col-offset";

    /**
     * Column count for medium screens.
     */
    private static final String MEDIUM_COLS = "md-cols";

    /**
     * Column offset for medium screens.
     */
    private static final String MEDIUM_COL_OFFSET = "md-col-offset";

    /**
     * Column count for large screens.
     */
    private static final String LARGE_COLS = "lg-cols";

    /**
     * Column offset for large screens.
     */
    private static final String LARGE_COL_OFFSET = "lg-col-offset";

    /**
     * Column count for extra large screens.
     */
    private static final String EXTRA_LARGE_COLS = "xl-cols";

    /**
     * Column offset for extra large screens.
     */
    private static final String EXTRA_LARGE_COL_OFFSET = "xl-col-offset";

    /**
     * Column count for extra-extra large screens.
     */
    private static final String EXTRA_EXTRA_LARGE = "xxl-cols";

    /**
     * Column offset for extra-extra large screens.
     */
    private static final String EXTRA_EXTRA_COL_OFFSET = "xxl-col-offset";

    /**
     * Lowercase token for the extra-small (XS) breakpoint.
     * Used as the CSS pseudo-class name <code>:xs</code>.
     */
    private static final String XS = "xs";

    /**
     * Lowercase token for the small (SM) breakpoint.
     * Used as the CSS pseudo-class name <code>:sm</code>.
     */
    private static final String SM = "sm";

    /**
     * Lowercase token for the medium (MD) breakpoint.
     * Used as the CSS pseudo-class name <code>:md</code>.
     */
    private static final String MD = "md";

    /**
     * Lowercase token for the large (LG) breakpoint.
     * Used as the CSS pseudo-class name <code>:lg</code>.
     */
    private static final String LG = "lg";

    /**
     * Lowercase token for the extra-large (XL) breakpoint.
     * Used as the CSS pseudo-class name <code>:xl</code>.
     */
    private static final String XL = "xl";

    /**
     * Lowercase token for the double-extra-large (XXL) breakpoint.
     * Used as the CSS pseudo-class name <code>:xxl</code>.
     */
    private static final String XXL = "xxl";

    /**
     * Insets key for extra-small screens.
     */
    private static final String EXTRA_SMALL_INSETS = "xs-insets";

    /**
     * Insets key for small screens.
     */
    private static final String SMALL_INSETS = "sm-insets";

    /**
     * Insets key for medium screens.
     */
    private static final String MEDIUM_INSETS = "md-insets";

    /**
     * Insets key for large screens.
     */
    private static final String LARGE_INSETS = "lg-insets";

    /**
     * Insets key for extra-large screens.
     */
    private static final String EXTRA_LARGE_INSETS = "xl-insets";

    /**
     * Insets key for extra-extra-large screens.
     */
    private static final String EXTRA_EXTRA_LARGE_INSETS = "xxl-insets";


    /**
     * Pseudo-class instance for <code>:xs</code>.
     * Toggle with <code>node.pseudoClassStateChanged(PC_XS, true/false)</code>.
     */
    private static final PseudoClass PC_XS = PseudoClass.getPseudoClass(XS);

    /**
     * Pseudo-class instance for <code>:sm</code>.
     * Toggle with <code>node.pseudoClassStateChanged(PC_SM, true/false)</code>.
     */
    private static final PseudoClass PC_SM = PseudoClass.getPseudoClass(SM);

    /**
     * Pseudo-class instance for <code>:md</code>.
     * Toggle with <code>node.pseudoClassStateChanged(PC_MD, true/false)</code>.
     */
    private static final PseudoClass PC_MD = PseudoClass.getPseudoClass(MD);

    /**
     * Pseudo-class instance for <code>:lg</code>.
     * Toggle with <code>node.pseudoClassStateChanged(PC_LG, true/false)</code>.
     */
    private static final PseudoClass PC_LG = PseudoClass.getPseudoClass(LG);

    /**
     * Pseudo-class instance for <code>:xl</code>.
     * Toggle with <code>node.pseudoClassStateChanged(PC_XL, true/false)</code>.
     */
    private static final PseudoClass PC_XL = PseudoClass.getPseudoClass(XL);

    /**
     * Pseudo-class instance for <code>:xxl</code>.
     * Toggle with <code>node.pseudoClassStateChanged(PC_XXL, true/false)</code>.
     */
    private static final PseudoClass PC_XXL = PseudoClass.getPseudoClass(XXL);

    /**
     * Caches the last applied breakpoint label (lower-case: "xs","sm","md","lg","xl","xxl").
     * <p>
     * Used to avoid redundant work: when the container’s active breakpoint hasn’t changed,
     * we skip re-toggling pseudo-classes and reapplying grid mappings.
     * <ul>
     *   <li><b>Initial state:</b> {@code null} until the first evaluation.</li>
     *   <li><b>Updates:</b> set inside {@code ensureActiveBreakpointApplied()} after
     *       normalizing the current breakpoint to lower-case.</li>
     *   <li><b>Threading:</b> access on the JavaFX Application Thread only.</li>
     * </ul>
     */
    private volatile String lastBp = null;

    /**
     * The fixed breakpoint threshold for the extra-small (XS) range.
     * <p>
     * This value is always {@code 0} and is immutable. It represents
     * the baseline for mobile-first layouts: any width below the
     * {@link #getSmBreakpoint() SM threshold} is considered "XS".
     */
    private static final double XS_BREAKPOINT = 0;

    /**
     * Additional stylesheet references owned by this pane.
     * <p>
     * Entries are href-like strings (URL, classpath resource name, or file path)
     * that will be merged into the effective stylesheet set when parsing/applying
     * grid rules. Modify this list on the JavaFX Application Thread and call
     * {@code refreshAndApplyCss()} if changes should take effect immediately.
     * <p>
     * Note: This list is mutable; order matters (later entries have higher precedence
     * under the class’s “last wins” resolution).
     */
    private final List<String> STYLES_SHEETS = new ArrayList<>();

    /**
     * Cached index of parsed grid style values.
     * <p>
     * Structure: className → breakpointKey → {@link Val}, where breakpointKey is
     * either the empty string (base) or one of: "xs", "sm", "md", "lg", "xl", "xxl".
     * <p>
     * This cache is rebuilt whenever relevant stylesheets change and is used to
     * avoid reparsing on every layout pass.
     */
    private Map<String, Map<String, Val>> cachedCssIndex = Collections.emptyMap();

    /**
     * Listener attached to {@code Scene.getStylesheets()} that triggers a re-parse
     * and re-application of CSS-derived grid constraints when the scene’s stylesheet
     * list changes (add/remove/permute).
     * <p>
     * Note: register/unregister this listener when the node’s scene changes to
     * avoid leaks. The callback executes on the JavaFX Application Thread.
     */
    private final ListChangeListener<String> sceneSheetsListener = change -> refreshAndApplyCss();

    /**
     * If {@code true}, disables automatic layout in {@link #layoutChildren()}.
     * Call {@link #layoutNow()} yourself to perform layout manually.
     * <p>
     * Threading: set on the JavaFX Application Thread. The {@code volatile}
     * modifier ensures visibility across threads, but any layout calls must
     * still occur on the FX thread.
     * <p>
     * Tip: when switching from {@code true} back to {@code false}, call
     * {@link #requestLayout()} to schedule a new layout pass.
     */
    private volatile boolean isManualLayout = false;


    /**
     * Creates a new {@code NfxFluidPane}.
     * <p>
     * The constructor performs control setup by invoking {@link #initialize()} to
     * prepare internal state (e.g., cached CSS index, listeners, and any default
     * configuration) before the pane participates in layout.
     * <p>
     * This constructor must be called on the JavaFX Application Thread.
     */
    public NfxFluidPane() {
        super();

        /*
         * Initialize the magic
         */
        initialize();
    }


    /**
     * Initializes listeners and performs the initial CSS parse/apply cycle.
     * <p>
     * Responsibilities:
     * <ul>
     *   <li>Re-applies the active breakpoint when width/height change.</li>
     *   <li>On children added: applies parsed CSS-derived constraints to just the new nodes
     *       and toggles the current breakpoint pseudo-class.</li>
     *   <li>Rewires the scene stylesheet change listener when the parent or scene changes
     *       (detach from old, attach to new) to avoid leaks.</li>
     *   <li>Performs an initial stylesheet refresh/parse so constraints are available immediately.</li>
     * </ul>
     * <b>Threading:</b> must be called on the JavaFX Application Thread.
     * <b>Performance:</b> refreshes are no-ops when the effective stylesheet set is unchanged.
     */
    private void initialize() {
        /*
         * Re-apply breakpoint on size changes
         */
        widthProperty().addListener((o, ov, nv) -> ensureActiveBreakpointApplied());
        heightProperty().addListener((o, ov, nv) -> ensureActiveBreakpointApplied());

        /*
         * Apply current breakpoint (and CSS props) to newly added children only
         */
        getChildren().addListener((ListChangeListener<Node>) ch -> {
            while (ch.next()) {
                if (ch.wasAdded()) {
                    if (!cachedCssIndex.isEmpty()) {
                        applyIndexToNodes(cachedCssIndex, ch.getAddedSubList());
                    }
                    if (lastBp != null) {
                        for (Node n : ch.getAddedSubList()) {
                            firePseudoClassChange(n, lastBp);
                        }
                    }
                }

                /*
                 * This is crazy , yes I know, but if the best way we got to force
                 * a relayout cuz requestLayout won't do much, and we need layout asap, so I did this guard-hack
                 */
                applyCss();
                try{
                    isManualLayout = true;
                    layoutNow();
                }
                finally {
                    isManualLayout = false;
                }

            }
        });

        /*
         * When parent changes (often implies scene wiring), refresh stylesheets once
         */
        parentProperty().addListener((obs, oldParent, newParent) -> {
            if (oldParent != null && oldParent.getScene() != null) {
                oldParent.getScene().getStylesheets().removeListener(sceneSheetsListener);
            }
            if (newParent != null && newParent.getScene() != null) {
                newParent.getScene().getStylesheets().addListener(sceneSheetsListener);
            }
            refreshAndApplyCss();
        });

        /*
         * When scene changes, rewire the scene stylesheet listener and refresh once
         */
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) oldScene.getStylesheets().removeListener(sceneSheetsListener);
            if (newScene != null) newScene.getStylesheets().addListener(sceneSheetsListener);
            refreshAndApplyCss();
        });

        /*
         * If we already have a scene at init time, hook its stylesheet changes
         */
        if (getScene() != null) {
            getScene().getStylesheets().addListener(sceneSheetsListener);
        }

        /*
         * Initial parse (if any stylesheets are present). A fucking pain in the ass.
         */
        refreshAndApplyCss();
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
            smBreakpoint = new SimpleDoubleProperty(this, "smBreakpoint", 800) {//576
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
            mdBreakpoint = new SimpleDoubleProperty(this, "mdBreakpoint", 1024) {//768
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
            lgBreakpoint = new SimpleDoubleProperty(this, "lgBreakpoint", 1280) {//992
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
            xlBreakpoint = new SimpleDoubleProperty(this, "xlBreakpoint", 1444) {//1200
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
            xxlBreakpoint = new SimpleDoubleProperty(this, "xxlBreakpoint", 1600) {//1444
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
            new ReadOnlyStringWrapper(this, "currentBreakpoint", XS);

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
        ensureActiveBreakpointApplied();
    }


    /**
     * Lays out this pane’s managed children.
     * <p>
     * Invoked by the JavaFX layout pass. If {@code isManualLayout} is {@code true},
     * this method returns immediately (no automatic layout); callers may invoke
     * {@link #layoutNow()} themselves at a time of their choosing. Otherwise this
     * method delegates to {@link #layoutNow()}, which performs the responsive,
     * breakpoint-aware grid layout and updates the preferred height.
     * <p>
     * Threading: JavaFX Application Thread.
     */

    @Override
    protected void layoutChildren() {
        if (isManualLayout) return;
        layoutNow();
    }

    /**
     * Lays out all managed children in a responsive, breakpoint-aware grid
     * and updates this pane’s preferred height.
     * <p>
     * Responsibilities:
     * <ul>
     *   <li>Determine the active breakpoint and column width.</li>
     *   <li>Resolve per-child span, offset, and insets for that breakpoint.</li>
     *   <li>Measure content height for each child at its computed content width.</li>
     *   <li>Pack children into rows, wrapping when columns are exhausted.</li>
     *   <li>Honor left-to-right and right-to-left node orientation.</li>
     *   <li>Snap positions and sizes to pixel boundaries.</li>
     *   <li>Set the pane’s preferred height to the total laid-out extent.</li>
     * </ul>
     * Threading: must run on the JavaFX Application Thread.
     */

    private void layoutNow() {
        ensureActiveBreakpointApplied();

        final double availW = Math.max(0, getWidth());
        if (availW <= 0 || MAX_COLS <= 0) { setPrefHeight(0); return; }

        final double colW = availW / MAX_COLS;
        final String bp   = getCurrentBreakpoint();
        final boolean rtl = getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;

        final List<Node> children = getManagedChildren();

        double y = 0;
        int currentCol = 0;       // columns consumed from the START side (LTR: left, RTL: right)
        double rowMaxH = 0;

        final List<Node>    rowNodes   = new ArrayList<>();
        final List<Integer> rowCols    = new ArrayList<>();   // startCol in LEFT-based coordinates
        final List<Integer> rowSpans   = new ArrayList<>();
        final List<Double>  rowHeights = new ArrayList<>();
        final List<Insets>  rowInsets  = new ArrayList<>();

        /*
         * Its funny, but I found out that this loop is faster cuz not uses Iterators, so this must be this way
         * no matter your opinion, so STFU ok.
         */
        for (int i = 0, n = children.size(); i < n; i++) {
            final Node node = children.get(i);

            int span = clampInt(colSpan(node, bp), 1, MAX_COLS);
            int off  = clampInt(colOffset(node, bp), 0, MAX_COLS - 1);
            int needed = off + span;

            // Not enough space from the START side? break the line.
            if (currentCol + needed > MAX_COLS) {
                if (!rowNodes.isEmpty()) {
                    placeRow(rowNodes, rowCols, rowSpans, rowHeights, rowInsets, rtl, colW, snapPositionY(y));
                    y += snapSpaceY(rowMaxH);
                    clearRow(rowNodes, rowCols, rowSpans, rowHeights, rowInsets);
                    rowMaxH = 0;
                    currentCol = 0;
                }
                // New (empty) row: if off+span still too big, shrink span to fit.
                if (off + span > MAX_COLS) {
                    span = Math.max(1, MAX_COLS - off);
                    needed = off + span;
                }
            }

            // Compute LEFT-based startCol for placement:
            // LTR: start = currentCol + off
            // RTL: block occupies the last 'needed' cols of the remaining space => left index = MAX_COLS - (currentCol + needed)
            final int startCol = rtl ? (MAX_COLS - (currentCol + needed))
                    : (currentCol + off);

            final Insets raw = insetsFor(node, bp);
            final Insets m = adjustEdgeGutters(raw, startCol, span);
            final double baseW    = span * colW;
            final double contentW = Math.max(0, baseW - (m.getLeft() + m.getRight()));
            final double hContent = measureContentHeight(node, contentW);

            rowNodes.add(node);
            rowCols.add(startCol);     // left-based index
            rowSpans.add(span);
            rowHeights.add(hContent);
            rowInsets.add(m);

            final double nodeTotalH = hContent + m.getTop() + m.getBottom();
            if (nodeTotalH > rowMaxH) rowMaxH = nodeTotalH;

            // advance from the START side by off+span
            currentCol += needed;

            if (currentCol >= MAX_COLS) {
                placeRow(rowNodes, rowCols, rowSpans, rowHeights, rowInsets, rtl, colW, snapPositionY(y));
                y += snapSpaceY(rowMaxH);
                clearRow(rowNodes, rowCols, rowSpans, rowHeights, rowInsets);
                currentCol = 0;
                rowMaxH = 0;
            }
        }

        if (!rowNodes.isEmpty()) {
            placeRow(rowNodes, rowCols, rowSpans, rowHeights, rowInsets, rtl, colW, snapPositionY(y));
            y += snapSpaceY(rowMaxH);
        }

        setPrefHeight(snapSpaceY(y));
    }


    /*
     * ==========================================HELPERS================================================================
     */

    /**
     * Trims outer horizontal gutters for a grid item at the container edges.
     * <p>
     * If the item begins in the first column, the left inset is forced to {@code 0};
     * if the item ends at or beyond the last column ({@code startCol + span >= MAX_COLS}),
     * the right inset is forced to {@code 0}. Top and bottom insets are preserved.
     * <p>
     * Notes:
     * <ul>
     *   <li>{@code startCol} is a left-based grid index (0..MAX_COLS-1). RTL mirroring is handled elsewhere.</li>
     *   <li>If no adjustment is needed, the original {@link Insets} instance is returned to avoid allocation.</li>
     * </ul>
     *
     * @param m        the original insets (non-null)
     * @param startCol the left-based starting column index
     * @param span     the number of columns occupied (≥ 1)
     * @return adjusted insets with outer gutters removed when touching container edges
     */

    private Insets adjustEdgeGutters(Insets m, int startCol, int span) {
        double left  = (startCol == 0) ? 0 : m.getLeft();
        double right = (startCol + span >= MAX_COLS) ? 0 : m.getRight();
        return (left == m.getLeft() && right == m.getRight()) ? m
                : new Insets(m.getTop(), right, m.getBottom(), left);
    }

    /**
     * Measures the content height of a node for a given content width.
     * For {@link Region} instances, it respects min/pref/max sizing.
     * For other nodes, it queries {@link Node#prefHeight(double)}.
     *
     * @param n         The node to measure.
     * @param contentW  The available content width.
     * @return The measured height.
     */
    private double measureContentHeight(Node n, double contentW) {
        if (n instanceof Region r) {
            r.setMinWidth(0);
            r.setMaxWidth(Double.MAX_VALUE);
            return Math.max(r.minHeight(contentW), r.prefHeight(contentW));
        }
        return n.prefHeight(-1);
    }


    /**
     * Places a single row of nodes, applying column, span, and per-node insets.
     *
     * @param rowNodes   Nodes in the row (size N).
     * @param rowCols    Column indices for each node (size N).
     * @param rowSpans   Column spans for each node (size N).
     * @param rowHeights Computed content heights for each node (size N).
     * @param rowInsets  Insets (margins) for each node (size N). Null -> {@link Insets#EMPTY}.
     * @param rtl        Whether layout is right-to-left.
     * @param colW       Width of one column in pixels.
     * @param y          Baseline Y of this row (top edge before insets).
     */
    private void placeRow(
            List<Node> rowNodes,
            List<Integer> rowCols,
            List<Integer> rowSpans,
            List<Double> rowHeights,
            List<Insets> rowInsets,
            boolean rtl,
            double colW,
            double y
    ) {
        for (int k = 0, sz = rowNodes.size(); k < sz; k++) {
            final Node rn = rowNodes.get(k);
            final int  c  = rowCols.get(k);
            final int  s  = rowSpans.get(k);
            final Insets m = rowInsets.get(k);

            final double baseX = rtl ? (MAX_COLS - (c + s)) * colW : c * colW;
            final double baseW = s * colW;

            final double x = baseX + m.getLeft();
            final double w = Math.max(0, baseW - (m.getLeft() + m.getRight()));
            final double hContent = rowHeights.get(k);
            final double yNode = y + m.getTop();

            rn.resizeRelocate(
                    snapPositionX(x),
                    snapPositionY(yNode),
                    snapSizeX(w),
                    snapSizeY(hContent)
            );
        }
    }

    /**
     * Clears all row-related collections, resetting the state
     * for building or recalculating the next row.
     *
     * @param rowNodes   The list of nodes in the row.
     * @param rowCols    The list of column indices for the row.
     * @param rowSpans   The list of column spans for the row.
     * @param rowHeights The list of row heights.
     * @param rowInsets  The list of insets associated with the row.
     */
    private static void clearRow(
            List<Node> rowNodes,
            List<Integer> rowCols,
            List<Integer> rowSpans,
            List<Double> rowHeights,
            List<Insets> rowInsets
    ) {
        rowNodes.clear();
        rowCols.clear();
        rowSpans.clear();
        rowHeights.clear();
        rowInsets.clear();
    }

    /**
     * Resolves the {@link Insets} for a given node and breakpoint identifier.
     * <p>
     * The breakpoint string is matched against known values (XS, SM, MD, LG, XL, XXL),
     * and the corresponding per-breakpoint insets accessor is used. If the breakpoint
     * does not match any known case, {@link Insets#EMPTY} is returned.
     *
     * @param n  the node to retrieve insets for
     * @param bp the breakpoint identifier (e.g. "XS", "SM", "MD", "LG", "XL", "XXL")
     * @return the insets for the given node and breakpoint, or {@link Insets#EMPTY} if none
     */
    private Insets insetsFor(Node n, String bp) {
        return switch (bp) {
            case XS -> getXsInsets(n);
            case SM -> getSmInsets(n);
            case MD -> getMdInsets(n);
            case LG -> getLgInsets(n);
            case XL -> getXlInsets(n);
            case XXL -> getXxlInsets(n);
            default -> Insets.EMPTY;
        };
    }

    /**
     * Resolves the effective column span for the given {@link Node} at a specific breakpoint.
     * <p>
     * This method routes to the breakpoint-specific span accessors (e.g. {@code getSmColSpan(item)})
     * which are expected to derive the value from the node's metadata (e.g., style-class tokens like
     * {@code col-sm-6} / {@code col-6} or node properties such as {@code "nfx.span.sm"}, {@code "nfx.span"}).
     * <p>
     * Breakpoint selection is based on the {@code bp} label:
     * <ul>
     *   <li>{@code "SM"} → {@link #getSmColSpan(Node)}</li>
     *   <li>{@code "MD"} → {@link #getMdColSpan(Node)}</li>
     *   <li>{@code "LG"} → {@link #getLgColSpan(Node)}</li>
     *   <li>{@code "XL"} → {@link #getXlColSpan(Node)}</li>
     *   <li>{@code "XXL"} → {@link #getXxlColSpan(Node)}</li>
     *   <li>Any other value (including {@code "XS"}) → defaults to {@code MAX_COLS}</li>
     * </ul>
     * <p>
     * Returning {@code MAX_COLS} indicates the default/auto behavior for the active row
     * (typically interpreted by the layout as "span the full row" or "take the remaining space",
     * depending on the caller's placement logic).
     *
     * @param item the child {@link Node} whose span should be resolved
     * @param bp   the current breakpoint label ({@code "XS"}, {@code "SM"}, {@code "MD"},
     *             {@code "LG"}, {@code "XL"}, or {@code "XXL"}); labels are case-sensitive
     * @return the resolved span in grid columns, or {@code MAX_COLS} when falling back to the default
     */

    private int colSpan(Node item, String bp) {
        return switch (bp) {
            case SM  -> getSmColSpan(item);
            case MD  -> getMdColSpan(item);
            case LG  -> getLgColSpan(item);
            case XL  -> getXlColSpan(item);
            case XXL -> getXxlColSpan(item);
            default    -> getXsColSpan(item);
        };
    }

    /**
     * Resolves the effective column offset for the given {@link Node} at a specific breakpoint.
     * <p>
     * This method dispatches to the breakpoint-specific offset accessors (e.g.
     * {@code getMdColOffset(item)}). Those accessors are expected to derive the value
     * from the node's metadata, such as:
     * <ul>
     *   <li>style-class tokens: {@code offset-2}, {@code offset-sm-1}, {@code offset-lg-3}</li>
     *   <li>node properties: {@code "nfx.offset"}, {@code "nfx.offset.sm"}, {@code "nfx.offset.lg"}</li>
     * </ul>
     * <p>
     * Breakpoint selection is based on the {@code bp} label:
     * <ul>
     *   <li>{@code "SM"}  → {@link #getSmColOffset(Node)}</li>
     *   <li>{@code "MD"}  → {@link #getMdColOffset(Node)}</li>
     *   <li>{@code "LG"}  → {@link #getLgColOffset(Node)}</li>
     *   <li>{@code "XL"}  → {@link #getXlColOffset(Node)}</li>
     *   <li>{@code "XXL"} → {@link #getXxlColOffset(Node)}</li>
     *   <li>Any other value (including {@code "XS"}) → {@link #getXsColOffset(Node)}</li>
     * </ul>
     * <p>
     * Offsets are expressed in grid columns (non-negative integers). The caller is
     * responsible for any clamping/normalization relative to the active grid width.
     *
     * @param item the child {@link Node} whose offset should be resolved
     * @param bp   the current breakpoint label ({@code "XS"}, {@code "SM"}, {@code "MD"},
     *             {@code "LG"}, {@code "XL"}, or {@code "XXL"}); labels are case-sensitive
     * @return the resolved offset in grid columns for the given node and breakpoint
     */

    private int colOffset(Node item, String bp) {
        return switch (bp) {
            case SM  -> getSmColOffset(item);
            case MD  -> getMdColOffset(item);
            case LG  -> getLgColOffset(item);
            case XL  -> getXlColOffset(item);
            case XXL -> getXxlColOffset(item);
            default    -> getXsColOffset(item);
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
        if (w >= getXxlBreakpoint()) return XXL;
        if (w >= getXlBreakpoint())  return XL;
        if (w >= getLgBreakpoint())  return LG;
        if (w >= getMdBreakpoint())  return MD;
        if (w >= getSmBreakpoint())  return SM;
        return XS;
    }

    /**
     * Refreshes the effective stylesheet set, rebuilds the parsed CSS index,
     * and applies any resulting grid constraints to current children.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Collects effective stylesheets via {@link #extractStyleSheets(Parent, Scene)}.</li>
     *   <li>If the set (including order) hasn’t changed since the last run, exits early.</li>
     *   <li>Updates the cached list, rebuilds {@code cachedCssIndex} once via {@code buildIndex(...)}.</li>
     *   <li>Applies resolved constraints to direct children with {@code applyIndexToChildren(...)}.</li>
     *   <li>Re-asserts the current breakpoint pseudo-class (if any) on all children.</li>
     * </ol>
     * <b>Threading:</b> must be called on the JavaFX Application Thread.
     * <b>Performance:</b> avoids work when stylesheet order/content is unchanged.
     */
    private void refreshAndApplyCss() {
        final List<String> newSheets = extractStyleSheets(this, getScene());
        // If equal (same order/contents), skip the work
        if (newSheets.equals(STYLES_SHEETS)) return;

        STYLES_SHEETS.clear();
        STYLES_SHEETS.addAll(newSheets);

        // Rebuild index once
        cachedCssIndex = buildIndex(STYLES_SHEETS);

        // Apply to all current children
        if (!cachedCssIndex.isEmpty()) {
            applyIndexToChildren(cachedCssIndex);
        }

        // Re-assert current breakpoint pseudo-class across children
        if (lastBp != null) {
            for (Node n : getChildren()) {
                firePseudoClassChange(n, lastBp);
            }
        }
    }

    /**
     * Clamps an integer value to the given range.
     * <p>
     * If the value is less than {@code lo}, {@code lo} is returned.
     * If the value is greater than {@code hi}, {@code hi} is returned.
     * Otherwise, the original value is returned.
     *
     * @param v  the value to clamp
     * @param lo the lower bound (inclusive)
     * @param hi the upper bound (inclusive)
     * @return the clamped value within the range [lo, hi]
     */
    private static int clampInt(int v, int lo, int hi) {
        return v < lo ? lo : Math.min(v, hi);
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

    /**
     * Sets the column span for extra-small screens.
     *
     * @param node The node to set the column span on.
     * @param value The column span value.
     */
    public static void setXsColSpan(Node node, Integer value) {
        setConstraint(node, EXTRA_SMALL_COLS, value == null ? null : Math.max(MIN_COLS, Math.min(value, MAX_COLS)));
    }

    /**
     * Retrieves the column span for extra-small screens.
     *
     * @param node The node to get the column span from.
     * @return The column span value.
     */
    public static Integer getXsColSpan(Node node) {
        Object value = getConstraint(node, EXTRA_SMALL_COLS);
        return value == null ? MAX_COLS :  (Integer) value;
    }

    /**
     * Sets the column offset for extra-small screens.
     *
     * @param node The node to set the column offset on.
     * @param value The column offset value.
     */
    public static void setXsColOffset(Node node, Integer value) {
        setConstraint(node, EXTRA_SMALL_COL_OFFSET,
                value == null ? null : Math.max(0, Math.min(value, MAX_COLS - 1)));
    }

    /**
     * Retrieves the column offset for extra-small screens.
     *
     * @param node The node to get the column offset from.
     * @return The column offset value.
     */
    public static Integer getXsColOffset(Node node) {
        Object value = getConstraint(node, EXTRA_SMALL_COL_OFFSET);
        return value == null ? 0 : (Integer) value;
    }

    /**
     * Sets the column span for small screens.
     *
     * @param node The node to set the column span on.
     * @param value The column span value.
     */
    public static void setSmColSpan(Node node, Integer value) {
        setConstraint(node, SMALL_COLS,  value == null ? null : Math.max(MIN_COLS, Math.min(value, MAX_COLS)));
    }

    /**
     * Retrieves the column span for small screens.
     *
     * @param node The node to get the column span from.
     * @return The column span value.
     */
    public static Integer getSmColSpan(Node node) {
        Object value = getConstraint(node, SMALL_COLS);
        return value == null ? 6 : (Integer) value;
    }

    /**
     * Sets the column offset for small screens.
     *
     * @param node The node to set the column offset on.
     * @param value The column offset value.
     */
    public static void setSmColOffset(Node node, Integer value) {
        setConstraint(node, SMALL_COL_OFFSET,
                value == null ? null : Math.max(0, Math.min(value, MAX_COLS - 1)));
    }

    /**
     * Retrieves the column offset for small screens.
     *
     * @param node The node to get the column offset from.
     * @return The column offset value.
     */
    public static Integer getSmColOffset(Node node) {
        Object value = getConstraint(node, SMALL_COL_OFFSET);
        return value == null ? 0 : (Integer) value;
    }

    /**
     * Sets the column span for medium screens.
     *
     * @param node The node to set the column span on.
     * @param value The column span value.
     */
    public static void setMdColSpan(Node node, Integer value) {
        setConstraint(node, MEDIUM_COLS,  value == null ? null : Math.max(MIN_COLS, Math.min(value, MAX_COLS)));
    }

    /**
     * Retrieves the column span for medium screens.
     *
     * @param node The node to get the column span from.
     * @return The column span value.
     */
    public static Integer getMdColSpan(Node node) {
        Object value = getConstraint(node, MEDIUM_COLS);
        return value == null ? 4 : (Integer) value;
    }

    /**
     * Sets the column offset for medium screens.
     *
     * @param node The node to set the column offset on.
     * @param value The column offset value.
     */
    public static void setMdColOffset(Node node, Integer value) {
        setConstraint(node, MEDIUM_COL_OFFSET,
                value == null ? null : Math.max(0, Math.min(value, MAX_COLS - 1)));
    }

    /**
     * Retrieves the column offset for medium screens.
     *
     * @param node The node to get the column offset from.
     * @return The column offset value.
     */
    public static Integer getMdColOffset(Node node) {
        Object value = getConstraint(node, MEDIUM_COL_OFFSET);
        return value == null ? 0 : (Integer) value;
    }

    /**
     * Sets the column span for large screens.
     *
     * @param node The node to set the column span on.
     * @param value The column span value.
     */
    public static void setLgColSpan(Node node, Integer value) {
        setConstraint(node, LARGE_COLS,  value == null ? null : Math.max(MIN_COLS, Math.min(value, MAX_COLS)));
    }

    /**
     * Retrieves the column span for large screens.
     *
     * @param node The node to get the column span from.
     * @return The column span value.
     */
    public static Integer getLgColSpan(Node node) {
        Object value = getConstraint(node, LARGE_COLS);
        return value == null ? 3 : (Integer) value;
    }

    /**
     * Sets the column offset for large screens.
     *
     * @param node The node to set the column offset on.
     * @param value The column offset value.
     */
    public static void setLgColOffset(Node node, Integer value) {
        setConstraint(node, LARGE_COL_OFFSET,
                value == null ? null : Math.max(0, Math.min(value, MAX_COLS - 1)));
    }

    /**
     * Retrieves the column offset for large screens.
     *
     * @param node The node to get the column offset from.
     * @return The column offset value.
     */
    public static Integer getLgColOffset(Node node) {
        Object value = getConstraint(node, LARGE_COL_OFFSET);
        return value == null ? 0 : (Integer) value;
    }

    /**
     * Sets the column span for extra large screens.
     *
     * @param node The node to set the column span on.
     * @param value The column span value.
     */
    public static void setXlColSpan(Node node, Integer value) {
        setConstraint(node, EXTRA_LARGE_COLS,  value == null ? null : Math.max(MIN_COLS, Math.min(value, MAX_COLS)));
    }

    /**
     * Retrieves the column span for extra large screens.
     *
     * @param node The node to get the column span from.
     * @return The column span value.
     */
    public static Integer getXlColSpan(Node node) {
        Object value = getConstraint(node, EXTRA_LARGE_COLS);
        return value == null ? 2 : (Integer) value;
    }

    /**
     * Sets the column offset for extra large screens.
     *
     * @param node The node to set the column offset on.
     * @param value The column offset value.
     */
    public static void setXlColOffset(Node node, Integer value) {
        setConstraint(node, EXTRA_LARGE_COL_OFFSET,
                value == null ? null : Math.max(0, Math.min(value, MAX_COLS - 1)));
    }

    /**
     * Retrieves the column offset for extra large screens.
     *
     * @param node The node to get the column offset from.
     * @return The column offset value.
     */
    public static Integer getXlColOffset(Node node) {
        Object value = getConstraint(node, EXTRA_LARGE_COL_OFFSET);
        return value == null ? 0 : (Integer) value;
    }

    /**
     * Sets the column span for extra-extra large screens.
     *
     * @param node The node to set the column span on.
     * @param value The column span value.
     */
    public static void setXxlColSpan(Node node, Integer value) {
        setConstraint(node, EXTRA_EXTRA_LARGE,  value == null ? null : Math.max(MIN_COLS, Math.min(value, MAX_COLS)));
    }

    /**
     * Retrieves the column span for extra-extra large screens.
     *
     * @param node The node to get the column span from.
     * @return The column span value.
     */
    public static Integer getXxlColSpan(Node node) {
        Object value = getConstraint(node, EXTRA_EXTRA_LARGE);
        return value == null ? 1 : (Integer) value;
    }

    /**
     * Sets the column offset for extra-extra large screens.
     *
     * @param node The node to set the column offset on.
     * @param value The column offset value.
     */
    public static void setXxlColOffset(Node node, Integer value) {
        setConstraint(node, EXTRA_EXTRA_COL_OFFSET,  value == null ? null : Math.max(0, Math.min(value, MAX_COLS - 1)));
    }

    /**
     * Retrieves the column offset for extra-extra large screens.
     *
     * @param node The node to get the column offset from.
     * @return The column offset value.
     */
    public static Integer getXxlColOffset(Node node) {
        Object value = getConstraint(node, EXTRA_EXTRA_COL_OFFSET);
        return value == null ? 0 : (Integer) value;
    }

    /**
     * Sets the insets for extra-small screens.
     *
     * @param node  The node to set the insets on.
     * @param value The Insets value.
     */
    public static void setXsInsets(Node node, Insets value) {
        setConstraint(node, EXTRA_SMALL_INSETS, value);
    }

    /**
     * Retrieves the insets for extra-small screens.
     *
     * @param node The node to get the insets from.
     * @return The Insets value, or {@link Insets#EMPTY} if none.
     */
    public static Insets getXsInsets(Node node) {
        Object v = getConstraint(node, EXTRA_SMALL_INSETS);
        return v instanceof Insets ? (Insets) v : Insets.EMPTY;
    }

    /**
     * Sets the insets for small screens.
     */
    public static void setSmInsets(Node node, Insets value) {
        setConstraint(node, SMALL_INSETS, value);
    }

    /**
     * Retrieves the insets for small screens.
     */
    public static Insets getSmInsets(Node node) {
        Object v = getConstraint(node, SMALL_INSETS);
        return v instanceof Insets ? (Insets) v : Insets.EMPTY;
    }

    /**
     * Sets the insets for medium screens.
     */
    public static void setMdInsets(Node node, Insets value) {
        setConstraint(node, MEDIUM_INSETS, value);
    }

    /**
     * Retrieves the insets for medium screens.
     */
    public static Insets getMdInsets(Node node) {
        Object v = getConstraint(node, MEDIUM_INSETS);
        return v instanceof Insets ? (Insets) v : Insets.EMPTY;
    }

    /**
     * Sets the insets for large screens.
     */
    public static void setLgInsets(Node node, Insets value) {
        setConstraint(node, LARGE_INSETS, value);
    }

    /**
     * Retrieves the insets for large screens.
     */
    public static Insets getLgInsets(Node node) {
        Object v = getConstraint(node, LARGE_INSETS);
        return v instanceof Insets ? (Insets) v : Insets.EMPTY;
    }

    /**
     * Sets the insets for extra-large screens.
     */
    public static void setXlInsets(Node node, Insets value) {
        setConstraint(node, EXTRA_LARGE_INSETS, value);
    }

    /**
     * Retrieves the insets for extra-large screens.
     */
    public static Insets getXlInsets(Node node) {
        Object v = getConstraint(node, EXTRA_LARGE_INSETS);
        return v instanceof Insets ? (Insets) v : Insets.EMPTY;
    }

    /**
     * Sets the insets for extra-extra-large screens.
     */
    public static void setXxlInsets(Node node, Insets value) {
        setConstraint(node, EXTRA_EXTRA_LARGE_INSETS, value);
    }

    /**
     * Retrieves the insets for extra-extra-large screens.
     */
    public static Insets getXxlInsets(Node node) {
        Object v = getConstraint(node, EXTRA_EXTRA_LARGE_INSETS);
        return v instanceof Insets ? (Insets) v : Insets.EMPTY;
    }


    /**
     * Sets a constraint on a node.
     *
     * @param node The node to set the constraint on.
     * @param key The key for the constraint.
     * @param value The value of the constraint. If null, the constraint is removed.
     */
    private static void setConstraint(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }

        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    /**
     * Retrieves a constraint from a node.
     *
     * @param node The node to get the constraint from.
     * @param key The key for the constraint.
     * @return The value of the constraint, or null if the node does not have the constraint.
     */
    private static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            return node.getProperties().getOrDefault(key, null);
        }
        return null;
    }

    /**
     * Toggles the responsive breakpoint pseudo-classes on a single node.
     *
     * <p>This method ensures that exactly one of the breakpoint pseudo-classes
     * (<code>:xs</code>, <code>:sm</code>, <code>:md</code>, <code>:lg</code>,
     * <code>:xl</code>, <code>:xxl</code>) is active on the given node based on
     * the supplied breakpoint label. All non-matching pseudo-classes are disabled.
     *
     * <p><b>Contract:</b>
     * <ul>
     *   <li><b>Node:</b> any {@link Node} (not recursive; caller decides which nodes to target).</li>
     *   <li><b>bpl:</b> expected lower-case breakpoint token: "xs", "sm", "md", "lg", "xl", or "xxl".</li>
     *   <li><b>Threading:</b> must be invoked on the JavaFX Application Thread.</li>
     *   <li><b>Idempotent:</b> calling repeatedly with the same <code>bpl</code> is safe.</li>
     *   <li><b>Unknown token:</b> if <code>bpl</code> does not match any known token,
     *       all breakpoint pseudo-classes are turned off for <code>n</code>.</li>
     * </ul>
     *
     * <p><b>Usage:</b> typically called by a container (e.g., NfxFluidPane) whenever the active
     * breakpoint changes, e.g., inside a size listener or at the start of layout.
     *
     * @param n   the target node
     * @param bpl the current breakpoint label in lower-case ("xs" | "sm" | "md" | "lg" | "xl" | "xxl")
     */
    private static void firePseudoClassChange(Node n, String bpl) {
        n.pseudoClassStateChanged(PC_XS,  XS.equals(bpl));
        n.pseudoClassStateChanged(PC_SM,  SM.equals(bpl));
        n.pseudoClassStateChanged(PC_MD,  MD.equals(bpl));
        n.pseudoClassStateChanged(PC_LG,  LG.equals(bpl));
        n.pseudoClassStateChanged(PC_XL,  XL.equals(bpl));
        n.pseudoClassStateChanged(PC_XXL, XXL.equals(bpl));
    }

    /**
     * Applies the active responsive breakpoint to this container's direct children
     * by toggling their breakpoint pseudo-classes, but only when the breakpoint
     * actually changes (debounced via {@link #lastBp}).
     *
     * <p><strong>What it does</strong>
     * <ul>
     *   <li>Obtains the current breakpoint label via {@code getCurrentBreakpoint()}.</li>
     *   <li>If the label differs from the last applied one, updates {@link #lastBp} and
     *       calls {@link #firePseudoClassChange(Node, String)} for each managed child.</li>
     *   <li>Requests a layout pass via {@link #requestLayout()}.</li>
     * </ul>
     *
     * <p><strong>Expected breakpoint format</strong>
     * <br>
     * The pseudo-classes are registered with lower-case names (<code>xs, sm, md, lg, xl, xxl</code>).
     * If {@code getCurrentBreakpoint()} returns upper-case tokens (e.g. <code>"XS"</code>),
     * normalize to lower-case before comparing/applying, or ensure
     * {@link #firePseudoClassChange(Node, String)} accepts/normalizes the input.
     *
     * <p><strong>When to call</strong>
     * <ul>
     *   <li>On width/height changes of the container.</li>
     *   <li>At the start of {@code layoutChildren()}.</li>
     * </ul>
     *
     * <p><strong>Scope</strong>
     * <br>
     * This toggles pseudo-classes on <em>direct children</em> only. If you want the
     * pseudo-classes to apply to all descendants, traverse the node tree instead
     * of iterating just {@code getChildren()}.
     *
     * <p><strong>Threading</strong>
     * <br>
     * Must be called on the JavaFX Application Thread.
     */
    private void ensureActiveBreakpointApplied() {
        String bp = getCurrentBreakpoint();
        if (bp != null && !bp.equals(lastBp)) {
            lastBp = bp;
            for (Node n : getChildren()){
                firePseudoClassChange(n, bp);
            }
            requestLayout();
        }
    }


    /*
     * =================================================================================================================
     *  CSS HELPERS
     *
     *  This section contains the parsing and application utilities for the grid CSS:
     *   - Patterns to recognize supported custom CSS properties (e.g. -col-span, -col-offset, -col-margin).
     *   - Extractor functions to read integer or string values from CSS blocks.
     *   - Parser logic to convert CSS shorthand values into Insets objects.
     *   - Index builder to resolve class selectors with optional breakpoint suffixes (e.g. .my-class:md).
     *   - Dispatcher methods to apply resolved values (span, offset, insets) to nodes at the correct breakpoint.
     *
     *  Notes:
     *   • "last-wins": later stylesheet rules override earlier ones.
     *   • Supports both base selectors (.class) and breakpoint selectors (.class:sm, .class:lg, etc).
     *   • Insets parsing follows CSS shorthand conventions: 1, 2, 3, or 4 values.
     *
     * =================================================================================================================
     */


    /**
     * Collects all stylesheets applicable to a node in the correct cascading order.
     * <p>
     * The resulting list preserves insertion order while removing duplicates.
     * Stylesheets are collected in three stages:
     * <ol>
     *   <li>From ancestor nodes, starting at the root and walking down to the direct parent
     *       (but <strong>not</strong> including the node itself).</li>
     *   <li>From the {@link Scene}, if one is present.</li>
     *   <li>From the node itself (the highest priority stylesheets).</li>
     * </ol>
     * <p>
     * This ensures that styles cascade in the same way JavaFX applies them internally:
     * parent styles first, then scene-level styles, and finally node-level styles.
     *
     * @param self  the node whose stylesheets should be resolved
     * @param scene the scene the node is in (may be {@code null})
     * @return an immutable list of stylesheet URLs, in cascading order, without duplicates
     */
    private static List<String> extractStyleSheets(Parent self, Scene scene) {
        // Keep insertion order + dedupe
        LinkedHashSet<String> urls = new LinkedHashSet<>();

        // 1) Ancestors from root → direct parent (NOT including self)
        ArrayList<Parent> chain = new ArrayList<>();
        for (Parent p = self.getParent(); p != null; p = p.getParent()) {
            chain.add(p);
        }
        for (Parent p : chain.reversed()) { // Java 21+: reversed view
            urls.addAll(p.getStylesheets());
        }

        // 2) Scene sheets
        if (scene != null) {
            urls.addAll(scene.getStylesheets());
        }

        // 3) This pane's sheets LAST (highest priority)
        urls.addAll(self.getStylesheets());

        return List.copyOf(urls);
    }


    /**
     * Ordered list of supported breakpoint identifiers.
     * <p>
     * These constants correspond to the responsive design breakpoints
     * handled by this layout:
     * <ul>
     *   <li>{@code XS}  – extra small</li>
     *   <li>{@code SM}  – small</li>
     *   <li>{@code MD}  – medium</li>
     *   <li>{@code LG}  – large</li>
     *   <li>{@code XL}  – extra large</li>
     *   <li>{@code XXL} – extra extra large</li>
     * </ul>
     * The array preserves logical order from smallest to largest breakpoint.
     */
    private static final String[] BPS = { XS, SM, MD, LG, XL, XXL };


    /**
     * Regex pattern for extracting CSS blocks.
     * <p>
     * Matches any selector part before a <code>{</code> followed by the block body up to
     * the matching <code>}</code>. Uses the <code>(?s)</code> flag so that
     * <code>.</code> also matches newlines.
     * <p>
     * Group 1 → selector(s)<br>
     * Group 2 → block content
     */
    private static final Pattern BLOCK_P = Pattern.compile("(?s)([^{]+)\\{([^}]*)}");


    /**
     * Regex pattern for validating and capturing allowed CSS-like selectors.
     * <p>
     * Supported forms:
     * <ul>
     *   <li><code>.class</code></li>
     *   <li><code>.class:xs</code></li>
     *   <li><code>.class:sm</code></li>
     *   <li><code>.class:md</code></li>
     *   <li><code>.class:lg</code></li>
     *   <li><code>.class:xl</code></li>
     *   <li><code>.class:xxl</code></li>
     * </ul>
     * <p>
     * Named groups:
     * <ul>
     *   <li><b>klass</b> → the CSS class name</li>
     *   <li><b>bp</b> → the optional breakpoint suffix</li>
     * </ul>
     */
    private static final Pattern SEL_P =
            Pattern.compile("^\\.(?<klass>[a-zA-Z0-9_-]+)(?::(?<bp>xs|sm|md|lg|xl|xxl))?$",
                    Pattern.CASE_INSENSITIVE);

    /**
     * Creates a regex pattern that matches an integer property declaration inside
     * a CSS-like style string.
     * <p>
     * Example match:
     * <pre>{@code
     *     -col-span: 3;
     * }</pre>
     *
     * Capturing groups:
     * <ul>
     *   <li>Group 2 → the integer value (may be negative)</li>
     * </ul>
     *
     * @param prop the property name (e.g. {@code "-col-span"})
     * @return a compiled {@link Pattern} that matches integer values
     */
    private static Pattern declInt(String prop) {
        return Pattern.compile("(?im)(^|;)\\s*" + Pattern.quote(prop) + "\\s*:\\s*(-?\\d+)\\s*(?:;|$)");
    }

    /**
     * Creates a regex pattern that matches a string property declaration inside
     * a CSS-like style string.
     * <p>
     * Example match:
     * <pre>{@code
     *     -col-align: center;
     * }</pre>
     *
     * Capturing groups:
     * <ul>
     *   <li>Group 2 → the string value (trimmed, stops at ';')</li>
     * </ul>
     *
     * @param prop the property name (e.g. {@code "-col-align"})
     * @return a compiled {@link Pattern} that matches string values
     */
    private static Pattern declStr(String prop) {
        return Pattern.compile("(?ims)(^|;)\\s*" + Pattern.quote(prop) + "\\s*:\\s*([^;]+?)\\s*(?:;|$)");
    }

    /**
     * Pattern for parsing column span declarations.
     * <p>
     * Example:
     * <pre>{@code
     *     -col-span: 3;
     * }</pre>
     * Captures the integer value <code>3</code>.
     */
    private static final Pattern SPAN_P = declInt("-col-span");

    /**
     * Pattern for parsing column offset declarations.
     * <p>
     * Example:
     * <pre>{@code
     *     -col-offset: 2;
     * }</pre>
     * Captures the integer value <code>2</code>.
     */
    private static final Pattern OFFS_P = declInt("-col-offset");

    /**
     * Pattern for parsing column margin (insets) declarations.
     * <p>
     * Example:
     * <pre>{@code
     *     -col-margin: 8 4 8 4;
     * }</pre>
     * Captures the string <code>"8 4 8 4"</code>.
     */
    private static final Pattern INSETS_P = declStr("-col-margin");


    /**
     * Value carrier for parsed style properties, grouped by (class, breakpoint).
     * <p>
     * This holds the "last-wins" values encountered during stylesheet parsing:
     * <ul>
     *   <li>{@code span}   – column span value (from {@code -col-span})</li>
     *   <li>{@code offset} – column offset value (from {@code -col-offset})</li>
     *   <li>{@code insets} – margin/insets value (from {@code -col-margin})</li>
     * </ul>
     *
     * Sequence numbers ({@code seqSpan}, {@code seqOff}, {@code seqInsets})
     * track the order of appearance in the stylesheet so that, when conflicts
     * occur, the most recent definition overrides earlier ones.
     */
    private static final class Val {
        int seqSpan = -1, seqOff = -1, seqInsets = -1;
        Integer span = null, offset = null;
        Insets insets = null;
    }

    /**
     * Builds an index of parsed style values from the given list of stylesheets.
     * <p>
     * The index is structured as:
     * <pre>
     *   className → ( breakpointKey → Val )
     * </pre>
     * where:
     * <ul>
     *   <li><b>className</b> is the CSS class selector without the leading dot</li>
     *   <li><b>breakpointKey</b> is one of {@code xs|sm|md|lg|xl|xxl}</li>
     *   <li><b>Val</b> holds the most recent ("last wins") values for
     *       {@code -col-span}, {@code -col-offset}, and {@code -col-margin}</li>
     * </ul>
     * <p>
     * Stylesheets are processed in the order provided. If multiple rules
     * match the same (class, breakpoint), later definitions override earlier ones.
     *
     * @param sheets ordered list of stylesheet hrefs to parse
     * @return nested map: class → breakpoint → {@link Val}
     */
    private static Map<String, Map<String, Val>> buildIndex(List<String> sheets) {
        Map<String, Map<String, Val>> map = new HashMap<>();
        int seq = 0;

        if (sheets == null) return map;
        for (String href : sheets) {
            String css = readCss(href);
            if (css == null || css.isBlank()) continue;

            css = css.replaceAll("(?s)/\\*.*?\\*/", "");

            Matcher m = BLOCK_P.matcher(css);
            while (m.find()) {
                String selectors = m.group(1).trim();
                String body      = m.group(2);

                Integer span     = extractInt(body, SPAN_P);
                Integer offset   = extractInt(body, OFFS_P);
                String  insetRaw = extractString(body, INSETS_P);
                Insets  insets   = (insetRaw != null) ? parseInsets(insetRaw) : null;

                if (span == null && offset == null && insets == null) continue;

                for (String selRaw : selectors.split(",")) {
                    String sel = selRaw.trim();
                    Matcher sm = SEL_P.matcher(sel);
                    if (!sm.matches()) continue; // only ".class" or ".class:bp"

                    String klass = sm.group("klass");
                    String bp    = sm.group("bp");
                    String key   = (bp == null) ? "" : bp.toLowerCase(Locale.ROOT); // "" = base

                    Map<String, Val> perBp = map.computeIfAbsent(klass, k -> new HashMap<>());
                    Val v = perBp.computeIfAbsent(key, b -> new Val());

                    if (span   != null && seq >= v.seqSpan)    { v.seqSpan   = seq; v.span   = span;   }
                    if (offset != null && seq >= v.seqOff)     { v.seqOff    = seq; v.offset = offset; }
                    if (insets != null && seq >= v.seqInsets)  { v.seqInsets = seq; v.insets = insets; }
                    seq++;
                }
            }
        }
        return map;
    }

    /**
     * Extracts the first integer value matched by the given property pattern.
     * <p>
     * The pattern is expected to have its numeric value in <b>group 2</b>
     * (as produced by {@link #declInt(String)}).
     *
     * @param body the CSS-like property string to search
     * @param p    the compiled pattern to apply
     * @return the parsed integer if found, otherwise {@code null}
     */
    private static Integer extractInt(String body, Pattern p) {
        Matcher dm = p.matcher(body);
        return dm.find() ? Integer.parseInt(dm.group(2)) : null;
    }

    /**
     * Extracts the first string value matched by the given property pattern.
     * <p>
     * The pattern is expected to have its string value in <b>group 2</b>
     * (as produced by {@link #declStr(String)}).
     *
     * @param body the CSS-like property string to search
     * @param p    the compiled pattern to apply
     * @return the trimmed string value if found, otherwise {@code null}
     */
    private static String extractString(String body, Pattern p) {
        Matcher dm = p.matcher(body);
        return dm.find() ? dm.group(2).trim() : null;
    }

    /**
     * Applies the parsed grid style index to this pane's direct children.
     * <p>
     * For each child node:
     * <ul>
     *   <li>Looks up its style classes in the {@code index} map</li>
     *   <li>Resolves the effective {@link Val} for the current breakpoint</li>
     *   <li>Sets layout constraints such as column span, column offset,
     *       and insets accordingly</li>
     * </ul>
     * <p>
     * If a child has no matching entry in the index, it is left unchanged.
     *
     * @param index nested map of parsed values, keyed by
     *              <code>className → (breakpointKey → {@link Val})</code>
     */
    private void applyIndexToChildren(Map<String, Map<String, Val>> index) {
        applyIndexToNodes(index, getChildren());
    }

    /**
     * Applies a parsed grid-style index to a set of nodes.
     * <p>
     * For each node, this method inspects its style classes and, for each supported
     * breakpoint in {@code BPS}, resolves effective values for:
     * {@code -col-span}, {@code -col-offset}, and {@code -col-margin}.
     * <p>
     * Resolution rules:
     * <ul>
     *   <li>Looks up values in the index under two keys per class:
     *       the empty key {@code ""} (breakpoint-agnostic base) and the exact
     *       breakpoint key (e.g., {@code "md"}).</li>
     *   <li>When multiple candidates exist, the "last-wins" policy is enforced via
     *       sequence numbers stored in {@link Val} ({@code seqSpan}, {@code seqOff},
     *       {@code seqInsets}). A value with a greater or equal sequence replaces
     *       the current best.</li>
     *   <li>If a property is not specified for a node/class/breakpoint, the
     *       existing constraint remains unchanged.</li>
     * </ul>
     * After resolution, the appropriate per-breakpoint setters are invoked:
     * {@code setSpanByBp}, {@code setOffByBp}, and {@code setInsetsByBp}.
     *
     * @param index nested map of parsed values (className → breakpointKey → {@link Val})
     * @param nodes nodes to which the resolved constraints should be applied
     */
    private void applyIndexToNodes(Map<String, Map<String, Val>> index, List<? extends Node> nodes) {
        if (index == null || index.isEmpty() || nodes == null || nodes.isEmpty()) return;

        for (Node n : nodes) {
            final List<String> classes = n.getStyleClass();
            if (classes == null || classes.isEmpty()) continue;

            for (String bp : BPS) {
                int bestS = -1, bestO = -1, bestM = -1;
                Integer span = null, off = null;
                Insets ins = null;
                for (String cls : classes) {
                    Map<String, Val> perBp = index.get(cls);
                    if (perBp == null) continue;

                    // base candidate
                    Val base = perBp.get("");
                    if (base != null) {
                        int sSeq = base.seqSpan, oSeq = base.seqOff, mSeq = base.seqInsets;
                        if (base.span   != null && sSeq >= 0 && sSeq > bestS) { bestS = sSeq; span = base.span;   }
                        if (base.offset != null && oSeq >= 0 && oSeq > bestO) { bestO = oSeq; off  = base.offset; }
                        if (base.insets != null && mSeq >= 0 && mSeq > bestM) { bestM = mSeq; ins  = base.insets; }
                    }

                    // exact-bp candidate (gets a big priority boost)
                    Val exact = perBp.get(bp);
                    if (exact != null) {
                        final int BOOST = 1_000_000;
                        int sSeq = exact.seqSpan   >= 0 ? exact.seqSpan   + BOOST : -1;
                        int oSeq = exact.seqOff    >= 0 ? exact.seqOff    + BOOST : -1;
                        int mSeq = exact.seqInsets >= 0 ? exact.seqInsets + BOOST : -1;

                        if (exact.span   != null && sSeq > bestS) { bestS = sSeq; span = exact.span;   }
                        if (exact.offset != null && oSeq > bestO) { bestO = oSeq; off  = exact.offset; }
                        if (exact.insets != null && mSeq > bestM) { bestM = mSeq; ins  = exact.insets; }
                    }
                }


                if (span != null) setSpanByBp(n, bp, span);
                if (off  != null) setOffByBp(n, bp, off);
                if (ins  != null) setInsetsByBp(n, bp, ins);
            }
        }
    }

    /**
     * Attempts to load a CSS stylesheet reference as text using a
     * "best-effort" resolution strategy. The lookup proceeds in order:
     * <ol>
     *   <li><b>As a URL</b> — supports schemes like {@code http:}, {@code file:}, {@code jar:}, etc.</li>
     *   <li><b>As a classpath resource</b> — resolved relative to {@code NfxFluidPane}'s class loader.</li>
     *   <li><b>As a filesystem path</b> — relative to the current working directory.</li>
     * </ol>
     * <p>
     * The stylesheet is read using UTF-8 encoding. Any errors (e.g. malformed URI,
     * missing file, I/O errors) are ignored silently, and {@code null} is returned
     * if the reference cannot be resolved by any of the strategies.
     *
     * @param ref the stylesheet reference (URL, classpath resource, or file path)
     * @return the CSS file contents as a string, or {@code null} if not found
     */
    private static String readCss(String ref) {
        // 1) try as URL (http:, file:, jar:, etc.)
        try (InputStream in = URI.create(ref).toURL().openStream();
             Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {
            sc.useDelimiter("\\A");
            return sc.hasNext() ? sc.next() : "";
        } catch (Throwable ignore) {}

        // 2) try as classpath resource
        try (InputStream in = NfxFluidPane.class.getResourceAsStream(
                ref.startsWith("/") ? ref : "/" + ref)) {
            if (in != null) {
                try (Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {
                    sc.useDelimiter("\\A");
                    return sc.hasNext() ? sc.next() : "";
                }
            }
        } catch (Throwable ignore) {}

        // 3) try as filesystem path (relative to working dir)
        try {
            Path p = Paths.get(ref);
            if (Files.exists(p)) {
                return Files.readString(p, StandardCharsets.UTF_8);
            }
        } catch (Throwable ignore) {}

        return null;
    }


    /**
     * Parses a CSS-like shorthand string into an {@link Insets} object.
     * <p>
     * Supported forms (values are in pixels by default, optional <code>px</code> suffix allowed;
     * values may be separated by whitespace or commas):
     * <ul>
     *   <li><b>1 value</b>: {@code a} → all sides = {@code a}</li>
     *   <li><b>2 values</b>: {@code v h} → top/bottom = {@code v}, left/right = {@code h}</li>
     *   <li><b>3 values</b>: {@code t h b} → top = {@code t}, left/right = {@code h}, bottom = {@code b}</li>
     *   <li><b>4 values</b>: {@code t r b l} → top, right, bottom, left</li>
     * </ul>
     * <p>
     * Example inputs:
     * <pre>
     *   "10"          → Insets(10, 10, 10, 10)
     *   "10 20"       → Insets(10, 20, 10, 20)
     *   "5, 15, 25"   → Insets(5, 15, 25, 15)
     *   "5 10 15 20"  → Insets(5, 10, 15, 20)
     *   "8px"         → Insets(8, 8, 8, 8)
     * </pre>
     * If the string is null, blank, or cannot be parsed, {@link Insets#EMPTY} is returned.
     *
     * @param s the CSS-like shorthand string
     * @return an {@link Insets} instance corresponding to the parsed values
     */
    private static Insets parseInsets(String s) {
        if (s == null || s.isBlank()) return Insets.EMPTY;
        // allow comma or whitespace separation, optional 'px'
        String[] parts = s.trim().replace(",", " ").split("\\s+");
        double[] v = new double[Math.min(4, parts.length)];
        for (int i = 0; i < v.length; i++) {
            String p = parts[i];
            if (p.endsWith("px")) p = p.substring(0, p.length() - 2);
            v[i] = Double.parseDouble(p);
        }
        return switch (v.length) {
            case 1 -> new Insets(v[0]);                               // a
            case 2 -> new Insets(v[0], v[1], v[0], v[1]);             // v h
            case 3 -> new Insets(v[0], v[1], v[2], v[1]);             // t h b
            case 4 -> new Insets(v[0], v[1], v[2], v[3]);             // t r b l
            default -> Insets.EMPTY;
        };
    }

    /**
     * Sets the column span constraint on a node for the given breakpoint.
     *
     * @param n the node to update
     * @param bp the breakpoint identifier ({@code XS}, {@code SM}, {@code MD}, {@code LG}, {@code XL}, {@code XXL})
     * @param v the span value to apply
     */
    private static void setSpanByBp(Node n, String bp, int v) {
        switch (bp) {
            case XS -> setXsColSpan(n, v);
            case SM -> setSmColSpan(n, v);
            case MD -> setMdColSpan(n, v);
            case LG -> setLgColSpan(n, v);
            case XL -> setXlColSpan(n, v);
            case XXL -> setXxlColSpan(n, v);
        }
    }

    /**
     * Sets the column offset constraint on a node for the given breakpoint.
     *
     * @param n the node to update
     * @param bp the breakpoint identifier ({@code XS}, {@code SM}, {@code MD}, {@code LG}, {@code XL}, {@code XXL})
     * @param v the offset value to apply
     */
    private static void setOffByBp(Node n, String bp, int v) {
        switch (bp) {
            case XS -> setXsColOffset(n, v);
            case SM -> setSmColOffset(n, v);
            case MD -> setMdColOffset(n, v);
            case LG -> setLgColOffset(n, v);
            case XL -> setXlColOffset(n, v);
            case XXL -> setXxlColOffset(n, v);
        }
    }

    /**
     * Sets the insets (margins) constraint on a node for the given breakpoint.
     *
     * @param n the node to update
     * @param bp the breakpoint identifier ({@code XS}, {@code SM}, {@code MD}, {@code LG}, {@code XL}, {@code XXL})
     * @param v the insets value to apply
     */
    private static void setInsetsByBp(Node n, String bp, Insets v) {
        switch (bp) {
            case XS -> setXsInsets(n, v);
            case SM -> setSmInsets(n, v);
            case MD -> setMdInsets(n, v);
            case LG -> setLgInsets(n, v);
            case XL -> setXlInsets(n, v);
            case XXL -> setXxlInsets(n, v);
        }
    }
}