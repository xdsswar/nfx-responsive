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

package xss.it.nfx.responsive.control;

import com.xss.it.nfx.responsive.base.GridListDelegate;
import com.xss.it.nfx.responsive.skins.GridListViewSkin;
import javafx.beans.DefaultProperty;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import xss.it.nfx.responsive.misc.SelectionModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.nfx.responsive.control package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 05, 2025
 * <p>
 * Created on 09/05/2025 at 17:57
 * <p>
 * A custom JavaFX control that displays a list of items in a grid layout.
 * <p>
 * This control uses a {@link GridListDelegate} to manage layout and a
 * {@link SelectionModel} to handle item selection.
 *
 * @param <T> the type of items contained in the list
 */
@DefaultProperty("items")
public final class NfxGridListView<T> extends Control {
    /**
     * The default user-agent stylesheet applied to all {@link NfxGridListView} instances.
     * <p>
     * This constant is initialized by loading the resource
     * {@code /xss/it/nfx/nfx-grid-list-view.css} from the classpath
     * (relative to {@link NfxGridListView}) and converting it to an
     * external form URL string for the JavaFX CSS engine.
     * <p>
     * The stylesheet defines the base look-and-feel for the control
     * and is returned by {@link #getUserAgentStylesheet()}.
     */
    private static final String STYLE_SHEET = load("/xss/it/nfx/nfx-grid-list-view.css").toExternalForm();

    /**
     * Delegate responsible for handling layout and rendering logic
     * of the {@code NfxGridListView}.
     */
    private final GridListDelegate<T> delegate;

    /**
     * The selection model that manages item selection for the list view.
     */
    private final SelectionModel<T> selectionModel;

    /**
     * Constructs a new {@code NfxGridListView}.
     * <p>
     * Initializes the {@link GridListDelegate} and {@link SelectionModel}.
     */
    public NfxGridListView() {
        selectionModel = new SelectionModel<>();
        delegate = new GridListDelegate<>(this);
        getStyleClass().add("grid-list-view");
    }


    /**
     * The list of items displayed by this {@code NfxGridListView}.
     */
    private ObjectProperty<ObservableList<T>> items;

    /**
     * Returns the observable list of items displayed by this grid list view.
     *
     * @return the list of items
     */
    public ObservableList<T> getItems() {
        return itemsProperty().get();
    }

    /**
     * Returns the {@link ObjectProperty} wrapper for the items list.
     * <p>
     * If the property has not yet been initialized, this method will
     * create a new {@link SimpleObjectProperty} with an empty
     * {@link FXCollections#observableArrayList()} as the default value.
     *
     * @return the items property
     */
    public ObjectProperty<ObservableList<T>> itemsProperty() {
        if (items == null) {
            items = new SimpleObjectProperty<>(this, "items", FXCollections.observableArrayList());
        }
        return items;
    }

    /**
     * Sets the observable list of items displayed by this grid list view.
     *
     * @param items the new list of items
     */
    public void setItems(ObservableList<T> items) {
        itemsProperty().set(items);
    }



    /**
     * Returns the selection model used by this grid list view.
     *
     * @return the selection model
     */
    public SelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    /**
     * The selection mode property, which determines how items in the
     * {@code NfxGridListView} can be selected.
     * <p>
     * The mode can be, for example:
     * <ul>
     *   <li>{@link SelectionModel.Mode#SINGLE} – only one item may be selected at a time</li>
     *   <li>{@link SelectionModel.Mode#MULTIPLE} – multiple items may be selected</li>
     * </ul>
     */
    private ObjectProperty<SelectionModel.Mode> selectionMode;

    /**
     * Returns the current selection mode.
     *
     * @return the selection mode
     */
    public SelectionModel.Mode getSelectionMode() {
        return selectionModeProperty().get();
    }

    /**
     * Returns the property that stores the selection mode.
     * <p>
     * If the property has not been initialized, it will be created
     * with a default value of {@link SelectionModel.Mode#SINGLE}.
     *
     * @return the selection mode property
     */
    public ObjectProperty<SelectionModel.Mode> selectionModeProperty() {
        if (selectionMode == null) {
            selectionMode = new SimpleObjectProperty<>(this, "selectionMode", SelectionModel.Mode.SINGLE);
        }
        return selectionMode;
    }

    /**
     * Sets the selection mode for this grid list view.
     *
     * @param selectionMode the new selection mode
     */
    public void setSelectionMode(SelectionModel.Mode selectionMode) {
        selectionModeProperty().set(selectionMode);
    }

    /**
     * The minimum cell width used to lay out items in the {@code NfxGridListView}.
     * <p>
     * This property is styleable via CSS using the {@code -fx-min-cell-width} property.
     * <p>
     * The default value is {@code 50.0}.
     */
    private DoubleProperty minCellWidth;

    /**
     * Returns the minimum cell width.
     *
     * @return the minimum cell width
     */
    public double getMinCellWidth() {
        return minCellWidthProperty().get();
    }

    /**
     * Returns the {@link DoubleProperty} that represents the minimum cell width.
     * <p>
     * If the property has not yet been initialized, this method will create
     * a {@link SimpleStyleableDoubleProperty} with a default value of {@code 50.0}.
     *
     * @return the minimum cell width property
     */
    public DoubleProperty minCellWidthProperty() {
        if (minCellWidth == null) {
            minCellWidth = new SimpleStyleableDoubleProperty(
                    StyleableProperties.MIN_CELL_WIDTH,
                    NfxGridListView.this,
                    "minCellWidth",
                    50.0
            );
        }
        return minCellWidth;
    }

    /**
     * Sets the minimum cell width.
     *
     * @param minCellWidth the new minimum cell width
     */
    public void setMinCellWidth(double minCellWidth) {
        minCellWidthProperty().set(minCellWidth);
    }

    /**
     * The height of each cell in the {@code NfxGridListView}.
     * <p>
     * This property is styleable via CSS using the {@code -fx-cell-height} property.
     * <p>
     * The default value is {@code 100.0}.
     */
    private DoubleProperty cellHeight;

    /**
     * Returns the height of each cell.
     *
     * @return the cell height
     */
    public double getCellHeight() {
        return cellHeightProperty().get();
    }

    /**
     * Returns the {@link DoubleProperty} that represents the cell height.
     * <p>
     * If the property has not yet been initialized, this method will create
     * a {@link SimpleStyleableDoubleProperty} with a default value of {@code 100.0}.
     *
     * @return the cell height property
     */
    public DoubleProperty cellHeightProperty() {
        if (cellHeight == null) {
            cellHeight = new SimpleStyleableDoubleProperty(
                    StyleableProperties.CELL_HEIGHT,
                    NfxGridListView.this,
                    "cellHeight",
                    50.0
            );
        }
        return cellHeight;
    }

    /**
     * Sets the height of each cell.
     *
     * @param cellHeight the new cell height
     */
    public void setCellHeight(double cellHeight) {
        cellHeightProperty().set(cellHeight);
    }


    /**
     * The maximum number of cells that can be displayed in a single row
     * of the {@code NfxGridListView}.
     * <p>
     * This property is styleable via CSS using the {@code -fx-max-cells-per-row} property.
     * <p>
     * The default value is {@code Integer.MAX_VALUE}, meaning there is
     * effectively no limit unless specified.
     */
    private IntegerProperty maxCellsPerRow;

    /**
     * Returns the maximum number of cells per row.
     *
     * @return the maximum number of cells per row
     */
    public int getMaxCellsPerRow() {
        return maxCellsPerRowProperty().get();
    }

    /**
     * Returns the {@link IntegerProperty} that represents the maximum
     * number of cells per row.
     * <p>
     * If the property has not yet been initialized, this method will create
     * a {@link SimpleStyleableIntegerProperty} with a default value of
     * {@code Integer.MAX_VALUE}.
     *
     * @return the maximum cells per row property
     */
    public IntegerProperty maxCellsPerRowProperty() {
        if (maxCellsPerRow == null) {
            maxCellsPerRow = new SimpleStyleableIntegerProperty(
                    StyleableProperties.MAX_CELLS_PER_ROW,
                    NfxGridListView.this,
                    "maxCellsPerRow",
                    Integer.MAX_VALUE
            );
        }
        return maxCellsPerRow;
    }

    /**
     * Sets the maximum number of cells per row.
     *
     * @param maxCellsPerRow the new maximum number of cells per row
     */
    public void setMaxCellsPerRow(int maxCellsPerRow) {
        maxCellsPerRowProperty().set(maxCellsPerRow);
    }

    /**
     * The insets (padding) applied to each cell box in the {@code NfxGridListView}.
     * <p>
     * This property is styleable via CSS using the {@code -fx-box-insets} property.
     * <p>
     * The default value is {@code Insets.EMPTY}.
     */
    private ObjectProperty<Insets> boxInsets;

    /**
     * Returns the box insets applied to each cell.
     *
     * @return the box insets
     */
    public Insets getBoxInsets() {
        return boxInsetsProperty().get();
    }

    /**
     * Returns the {@link ObjectProperty} that represents the box insets.
     * <p>
     * If the property has not yet been initialized, this method will create
     * a {@link SimpleStyleableObjectProperty} with a default value of {@code Insets.EMPTY}.
     *
     * @return the box insets property
     */
    public ObjectProperty<Insets> boxInsetsProperty() {
        if (boxInsets == null) {
            boxInsets = new SimpleStyleableObjectProperty<>(
                    StyleableProperties.BOX_INSETS,
                    NfxGridListView.this,
                    "boxInsets",
                    Insets.EMPTY
            );
        }
        return boxInsets;
    }

    /**
     * Sets the box insets applied to each cell.
     *
     * @param boxInsets the new box insets
     */
    public void setBoxInsets(Insets boxInsets) {
        boxInsetsProperty().set(boxInsets);
    }

    /**
     * The cell factory used to create {@link NfxGridCell} instances
     * for rendering items in the {@code NfxGridListView}.
     * <p>
     * This property allows developers to provide custom cell
     * implementations by supplying a callback that returns
     * a new {@code NfxGridCell} for a given {@code NfxGridListView}.
     */
    private ObjectProperty<Callback<NfxGridListView<T>, NfxGridCell<T>>> cellFactory;

    /**
     * Returns the current cell factory.
     *
     * @return the cell factory
     */
    public Callback<NfxGridListView<T>, NfxGridCell<T>> getCellFactory() {
        return cellFactoryProperty().get();
    }

    /**
     * Returns the property that stores the cell factory.
     *
     * @return the cell factory property
     */
    public ObjectProperty<Callback<NfxGridListView<T>, NfxGridCell<T>>> cellFactoryProperty() {
        if (cellFactory == null) {
            cellFactory = new SimpleObjectProperty<>(this, "cellFactory", defaultCellFactory());
        }
        return cellFactory;
    }

    /**
     * Sets the cell factory used to create cells.
     *
     * @param cellFactory the new cell factory
     */
    public void setCellFactory(Callback<NfxGridListView<T>, NfxGridCell<T>> cellFactory) {
        cellFactoryProperty().set(cellFactory);
    }

    /**
     * Determines whether clicking on an already-selected cell will
     * unselect it in the {@code NfxGridListView}.
     * <p>
     * If {@code true}, clicking on a selected cell will toggle its
     * selection state to unselected. If {@code false}, clicking on a
     * selected cell has no effect.
     * <p>
     * The default value is {@code false}.
     */
    private BooleanProperty unselectOnClick;

    /**
     * Returns whether clicking on a selected cell will unselect it.
     *
     * @return {@code true} if clicking unselects, {@code false} otherwise
     */
    public boolean isUnselectOnClick() {
        return unselectOnClickProperty().get();
    }

    /**
     * Returns the property that controls whether clicking on a
     * selected cell will unselect it.
     * <p>
     * If the property has not yet been initialized, this method will
     * create a {@link SimpleBooleanProperty} with a default value of {@code true}.
     *
     * @return the unselect-on-click property
     */
    public BooleanProperty unselectOnClickProperty() {
        if (unselectOnClick == null) {
            unselectOnClick = new SimpleBooleanProperty(this, "unselectOnClick", true);
        }
        return unselectOnClick;
    }

    /**
     * Sets whether clicking on a selected cell will unselect it.
     *
     * @param unselectOnClick {@code true} to enable unselect-on-click,
     *                        {@code false} to disable it
     */
    public void setUnselectOnClick(boolean unselectOnClick) {
        unselectOnClickProperty().set(unselectOnClick);
    }

    /**
     * The spacing (margins) applied around each cell in the {@link NfxGridListView}.
     * <p>
     * This property allows per-side control of the space between cells by using
     * an {@link Insets} value (top, right, bottom, left). It behaves like a
     * "margin" for cells within the grid.
     * <ul>
     *   <li>Default value: {@link Insets#EMPTY} (no spacing).</li>
     *   <li>Can be set programmatically or via CSS using {@code -cell-spacing}.</li>
     *   <li>Example CSS: {@code -cell-spacing: 4 8 4 8;} adds vertical and horizontal gaps.</li>
     * </ul>
     */
    private ObjectProperty<Insets> cellSpacing;

    /**
     * Returns the current cell spacing.
     *
     * @return the {@link Insets} representing spacing around each cell;
     *         returns {@link Insets#EMPTY} if not set
     */
    public Insets getCellSpacing() {
        return cellSpacing == null ? Insets.EMPTY : cellSpacing.get();
    }

    /**
     * Sets the spacing (margins) around each cell.
     *
     * @param value the {@link Insets} value to apply; cannot be {@code null}
     */
    public void setCellSpacing(Insets value) {
        cellSpacingProperty().set(value);
    }

    /**
     * The property object for cell spacing.
     * <p>
     * This property is styleable via CSS using {@code -cell-spacing}.
     *
     * @return the {@link ObjectProperty} that wraps the cell spacing value
     */
    public ObjectProperty<Insets> cellSpacingProperty() {
        if (cellSpacing == null) {
            cellSpacing = new SimpleStyleableObjectProperty<>(
                    StyleableProperties.CELL_SPACING,
                    this,
                    "cellSpacing",
                    Insets.EMPTY
            );
        }
        return cellSpacing;
    }


    /**
     * Optional placeholder node for this pane (e.g., shown when there is no content).
     * <p>
     * Backed by a lazily-initialized {@link ObjectProperty} named {@code placeHolder}.
     * The lifecycle (adding/removing it from the scene graph) is managed by this pane’s
     * logic; setting this property alone does not automatically add it as a child.
     * <p>
     * Default: {@code null}.
     */
    private ObjectProperty<Node> placeHolder;

    /**
     * Returns the current placeholder node.
     *
     * @return the placeholder node, or {@code null} if none is set
     */
    public Node getPlaceHolder() {
        return placeHolderProperty().get();
    }

    /**
     * The placeholder property.
     * <p>
     * Created on first access. External code can observe or bind this property.
     *
     * @return the {@link ObjectProperty} wrapping the placeholder node
     */
    public ObjectProperty<Node> placeHolderProperty() {
        if (placeHolder == null){
            placeHolder = new SimpleObjectProperty<>(this, "placeHolder", defaultPlaceholder());
        }
        return placeHolder;
    }

    /**
     * Sets the placeholder node.
     * <p>
     * Note: Setting this property does not by itself insert the node into the
     * scene graph; display behavior is handled elsewhere by the pane.
     *
     * @param placeHolder the node to use as a placeholder (may be {@code null})
     */
    public void setPlaceHolder(Node placeHolder) {
        placeHolderProperty().set(placeHolder);
    }


    /**
     * Scrolls the view so that the given item becomes visible.
     *
     * @param item the item to scroll into view; if {@code null} or not found, no action is taken
     */
    public void scroll(T item) {
        delegate.scroll(item);
    }


    /**
     * Returns the first item in the list.
     *
     * @return the first item, or {@code null} if the list is empty
     */
    public T getFirst() {
        return delegate.getFirstItem();
    }

    /**
     * Returns the last item in the list.
     *
     * @return the last item, or {@code null} if the list is empty
     */
    public T getLast() {
        return delegate.getLastItem();
    }


    /**
     * Refreshes this {@code NfxGridListView} by delegating to its {@link GridListDelegate}.
     * <p>
     * This will clear the current selection and trigger a full reset of the
     * visual cells, ensuring the content is redrawn based on the latest data
     * and layout constraints.
     */
    public void refresh() {
        delegate.refresh();
    }

    /**
     * Exposes the live, read-only focused-item property for observation/binding.
     * <p>
     * The property's <em>value</em> may be {@code null} to indicate that no item is
     * currently focused. The property itself is read-only to callers; focus changes
     * should be performed through the owning API.
     *
     * @return a read-only property reflecting the currently focused item (nullable)
     * @implNote Read and observe this on the JavaFX Application Thread.
     */
    public ReadOnlyObjectProperty<T> focusedItemProperty() {
        return delegate.focusedItemProperty();
    }

    /**
     * Convenience accessor for the currently focused item.
     * <p>
     * Equivalent to {@code focusedItemProperty().get()} and may return {@code null}
     * when no item is focused.
     *
     * @return the focused item, or {@code null} if none
     */
    public T getFocusedItem() {
        return delegate.focusedItemProperty().get();
    }


    /**
     * Creates the default skin for this control.
     *
     * @return the default {@link Skin} implementation
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new GridListViewSkin<>(this, delegate);
    }

    /**
     * Returns the list of {@link CssMetaData} objects that define
     * the styleable properties for the {@link NfxGridListView} class.
     * <p>
     * This includes both the CSS metadata defined in {@link Control}
     * and the additional custom properties defined in
     * {@link StyleableProperties}.
     *
     * @return an unmodifiable list of {@link CssMetaData} for this control class
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * Returns the list of {@link CssMetaData} objects that define
     * the styleable properties for this specific {@link NfxGridListView} instance.
     * <p>
     * By default, this delegates to {@link #getClassCssMetaData()},
     * ensuring that the CSS engine sees the same metadata for all
     * instances of this control.
     *
     * @return the list of {@link CssMetaData} for this control instance
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    /**
     * Returns the default cell factory used by the {@link NfxGridListView}.
     * <p>
     * The default factory produces {@link NfxGridCell} instances that render
     * each item by calling {@link Object#toString()} and displaying it as text.
     * <p>
     * Subclasses or client code may override the cell factory to provide
     * custom rendering logic.
     *
     * @return a {@link Callback} that produces {@link NfxGridCell} instances
     */
    private Callback<NfxGridListView<T>, NfxGridCell<T>> defaultCellFactory() {
        return new Callback<>() {
            @Override
            public NfxGridCell<T> call(NfxGridListView<T> listView) {
                return new NfxGridCell<>(listView) {
                    @Override
                    public void update(T item) {
                        super.update(item);
                        setText(String.format("%s", item));
                    }
                };
            }
        };
    }


    /**
     * Returns the URL of the default user-agent stylesheet for this control.
     * <p>
     * This method is called by the JavaFX CSS engine to apply the control’s
     * default styles. It should return a URL string (typically obtained via
     * {@link Class#getResource(String)}) pointing to the CSS file that defines
     * the base look and feel of the control.
     * <p>
     * In this implementation, the value is provided by the constant
     * {@code STYLE_SHEET}, which should contain the fully resolved
     * resource path.
     *
     * @return the URL of the default stylesheet, or {@code null} if no
     *         default stylesheet is defined
     */
    @Override
    public String getUserAgentStylesheet() {
        return STYLE_SHEET;
    }

    /**
     * Creates a default placeholder node displaying a message when no items are available.
     *
     * @return a Label styled as a placeholder message
     */
    private Node defaultPlaceholder() {
        Label label = new Label("No items available");
        label.setStyle("-fx-font-size: 16; -fx-text-fill: gray;");
        return label;
    }

    /**
     * Defines the CSS styleable properties for {@link NfxGridListView}.
     * <p>
     * Each {@link CssMetaData} entry maps a CSS property to a JavaFX property
     * of the control, enabling styling through external stylesheets.
     */
    private static class StyleableProperties {

        /**
         * CSS metadata for the {@code -cell-height} property.
         * <p>
         * Controls the height of each cell in the {@code NfxGridListView}.
         * Default value: {@code 50}.
         */
        public static final CssMetaData<NfxGridListView<?>, Number> CELL_HEIGHT =
                new CssMetaData<>("-cell-height", StyleConverter.getSizeConverter(), 50) {
                    /**
                     * Determines whether the {@code cellHeight} property is settable
                     * (i.e., not bound).
                     */
                    @Override
                    public boolean isSettable(NfxGridListView<?> styleable) {
                        return styleable.cellHeight == null || !styleable.cellHeight.isBound();
                    }

                    /**
                     * Returns the {@link StyleableProperty} corresponding to
                     * {@code cellHeight}.
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxGridListView<?> styleable) {
                        return (StyleableProperty<Number>) styleable.cellHeightProperty();
                    }
                };

        /**
         * CSS metadata for the {@code -min-cell-width} property.
         * <p>
         * Controls the minimum width of cells in the {@code NfxGridListView}.
         * Default value: {@code 50}.
         */
        public static final CssMetaData<NfxGridListView<?>, Number> MIN_CELL_WIDTH =
                new CssMetaData<>("-min-cell-width", StyleConverter.getSizeConverter(), 50) {
                    /**
                     * Determines whether the {@code minCellWidth} property is settable
                     * (i.e., not bound).
                     */
                    @Override
                    public boolean isSettable(NfxGridListView<?> styleable) {
                        return styleable.minCellWidth == null || !styleable.minCellWidth.isBound();
                    }

                    /**
                     * Returns the {@link StyleableProperty} corresponding to
                     * {@code minCellWidth}.
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxGridListView<?> styleable) {
                        return (StyleableProperty<Number>) styleable.minCellWidthProperty();
                    }
                };


        /**
         * CSS metadata for the {@code -max-cells-per-row} property.
         * <p>
         * Controls the maximum number of cells that may appear in a single row
         * of the {@link NfxGridListView}. This provides an upper bound on layout,
         * even if more cells could theoretically fit based on available width
         * and {@code minCellWidth}.
         * <p>
         * Default value: {@code 12}.
         */
        public static final CssMetaData<NfxGridListView<?>, Number> MAX_CELLS_PER_ROW =
                new CssMetaData<>("-max-cells-per-row", StyleConverter.getSizeConverter(), 12) {
                    /**
                     * Determines whether the {@code maxCellsPerRow} property is settable
                     * (i.e., not bound).
                     */
                    @Override
                    public boolean isSettable(NfxGridListView<?> styleable) {
                        return styleable.maxCellsPerRow == null || !styleable.maxCellsPerRow.isBound();
                    }

                    /**
                     * Returns the {@link StyleableProperty} corresponding to
                     * {@code maxCellsPerRow}.
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxGridListView<?> styleable) {
                        return (StyleableProperty<Number>) styleable.maxCellsPerRowProperty();
                    }
                };


        /**
         * CSS metadata for the {@code -box-insets} property.
         * <p>
         * Controls the padding (insets) applied to each cell in the {@code NfxGridListView}.
         * Default value: {@link Insets#EMPTY}.
         */
        public static final CssMetaData<NfxGridListView<?>, Insets> BOX_INSETS =
                new CssMetaData<>("-box-insets", StyleConverter.getInsetsConverter(), Insets.EMPTY) {
                    /**
                     * Determines whether the {@code boxInsets} property is settable
                     * (i.e., not bound).
                     */
                    @Override
                    public boolean isSettable(NfxGridListView<?> styleable) {
                        return styleable.boxInsets == null || !styleable.boxInsets.isBound();
                    }

                    /**
                     * Returns the {@link StyleableProperty} corresponding to
                     * {@code boxInsets}.
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Insets> getStyleableProperty(NfxGridListView<?> styleable) {
                        return (StyleableProperty<Insets>) styleable.boxInsetsProperty();
                    }
                };

        /**
         * CSS metadata for the {@code -cell-spacing} property.
         * <p>
         * This property defines the spacing (as {@link Insets}) applied around each cell
         * in the {@link NfxGridListView}. It allows fine-grained control of the space
         * between cells on all four sides (top, right, bottom, left).
         * <ul>
         *   <li>Default value: {@link Insets#EMPTY} (no spacing).</li>
         *   <li>Can be set in CSS as {@code -cell-spacing: top right bottom left;}.</li>
         *   <li>Example: {@code -cell-spacing: 4 8 4 8;} for asymmetric margins.</li>
         * </ul>
         */
        public static final CssMetaData<NfxGridListView<?>, Insets> CELL_SPACING =
                new CssMetaData<>("-cell-spacing", StyleConverter.getInsetsConverter(), Insets.EMPTY) {
                    @Override
                    public boolean isSettable(NfxGridListView<?> styleable) {
                        return styleable.cellSpacing == null || !styleable.cellSpacing.isBound();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public StyleableProperty<Insets> getStyleableProperty(NfxGridListView<?> styleable) {
                        return (StyleableProperty<Insets>) styleable.cellSpacingProperty();
                    }
                };


        /**
         * The complete list of styleable properties supported by {@link NfxGridListView}.
         * <p>
         * This list combines the styleable properties defined in the superclass
         * {@link Control} with the custom properties defined in
         * {@link StyleableProperties} ({@code CELL_HEIGHT}, {@code MIN_CELL_WIDTH},
         * {@code BOX_INSETS}).
         * <p>
         * The list is unmodifiable and is used by
         * {@link NfxGridListView#getClassCssMetaData()} and
         * {@link NfxGridListView#getControlCssMetaData()} to expose CSS metadata
         * to the JavaFX CSS engine.
         */
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> cssMetaData =
                    new ArrayList<>(Control.getClassCssMetaData());
            cssMetaData.add(CELL_HEIGHT);
            cssMetaData.add(MIN_CELL_WIDTH);
            cssMetaData.add(BOX_INSETS);
            cssMetaData.add(MAX_CELLS_PER_ROW);
            cssMetaData.add(CELL_SPACING);
            STYLEABLES = Collections.unmodifiableList(cssMetaData);
        }

    }


    /**
     * Loads a resource relative to the {@link NfxGridListView} classpath location.
     * <p>
     * This is a convenience method that delegates to
     * {@link Class#getResource(String)} on {@code NfxGridListView.class}.
     * <p>
     * Example usage:
     * <pre>{@code
     * URL stylesheet = NfxGridListView.load("gridlistview.css");
     * }</pre>
     *
     * @param location the resource path, relative to the {@code NfxGridListView} class
     *                 (e.g. {@code "styles.css"} or {@code "/com/example/styles.css"})
     * @return the resolved {@link URL}, or {@code null} if the resource was not found
     */
    public static URL load(final String location) {
        return NfxGridListView.class.getResource(location);
    }

}
