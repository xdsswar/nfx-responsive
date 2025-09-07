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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import xss.it.nfx.responsive.misc.SelectionModel;

import java.util.Objects;

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
 * Created on 09/05/2025 at 18:16
 * <p>
 * A grid cell used by {@link NfxGridListView} to render items.
 *
 * @param <T> the type of the item rendered by this cell
 */
public class NfxGridCell<T> extends Region {
    /**
     * The item contained in this cell.
     */
    private ObjectProperty<T> item;

    /**
     * The list view that contains this cell.
     */
    private final NfxGridListView<T> listView;

    /**
     * The pseudo-class representing the selected state.
     */
    private static final PseudoClass PSEUDO_CLASS_SELECTED =
            PseudoClass.getPseudoClass("selected");

    /**
     * The pseudo-class representing the focused state.
     */
    private static final PseudoClass PSEUDO_CLASS_FOCUSED =
            PseudoClass.getPseudoClass("focused");

    /**
     * Per-cell mouse handler.
     * Primary click typically requests focus on this cell and applies the
     * appropriate selection semantics (ctrl/shift aware if implemented).
     * Installed/removed in {@code update(...)}.
     */
    private final EventHandler<MouseEvent> clickEvent;

    /**
     * Per-cell key handler.
     * Runs when this cell has keyboard focus; handles activation via keys
     * (currently ENTER) to apply selection. Installed/removed in {@code update(...)}.
     */
    private final EventHandler<KeyEvent> keyEvent;


    /**
     * Creates a new {@code NfxGridCell} associated with the given list view.
     *
     * @param listView the {@link NfxGridListView} that contains this cell
     */
    public NfxGridCell(NfxGridListView<T> listView) {
        super();
        this.listView = listView;

        clickEvent = e -> {
            final var sm        = getListView().getSelectionModel();
            final var item      = getItem();
            final boolean isSel = sm.getSelectedItems().contains(item);
            final boolean multi = sm.getSelectionMode() == SelectionModel.Mode.MULTIPLE;
            final boolean mod   = e.isShortcutDown(); // Ctrl on Win/Linux, Cmd on macOS

            // --- Right click (context) ---
            if (Objects.equals(e.getButton(), MouseButton.SECONDARY)) {
                if (isSel) {
                    // Keep existing multi-selection
                    return;
                } else {
                    // Replace with only this item
                    sm.clearSelection();
                    sm.select(item);
                    return;
                }
            }

            // --- Primary click only ---
            if (!Objects.equals(e.getButton(), MouseButton.PRIMARY)) return;

            // --- Primary + shortcut (toggle in MULTIPLE mode) ---
            if (multi && mod) {
                if (isSel) {
                    if (getListView().isUnselectOnClick()) {
                        sm.unselect(item); // toggle off this one only
                    }
                } else {
                    sm.select(item);       // add without clearing others
                }
                return;
            }

            // --- Primary with NO modifier ---
            if (isSel && sm.getSelectedItems().size() == 1) {
                // Already the sole selection, do nothing
                return;
            }

            // Otherwise, replace with only this item
            sm.clearSelection();
            sm.select(item);
        };

        keyEvent = e -> {
            if (e.getEventType() != KeyEvent.KEY_PRESSED) return;

            var sm = getListView().getSelectionModel();

            switch (e.getCode()) {
                case ENTER: {
                    var item = getItem();
                    sm.clearSelection();
                    if (item != null) {
                        // Exclusive select
                        sm.select(item);
                    }
                    e.consume(); // handled here
                    break;
                }
                case SPACE: {
                    // If you really want SPACE to clear selection from the cell:
                    sm.clearSelection();
                    e.consume(); // handled here
                    break;
                }
                default:
                    // DO NOT consume: allow grid-level keyNavHandler to handle navigation, etc.
                    break;
            }
        };


        initialize();
    }

    /**
     * Initializes this cell, wiring styles, event handlers, and listeners.
     */
    private void initialize() {
        getStyleClass().add("grid-cell");
        setFocusTraversable(true);

        selectedCellProperty().addListener((obs, o, selected) -> {
            if (selected) {
                SelectionModel<T> sm = getListView().getSelectionModel();
                sm.select(getItem());
            }
        });

        /*
         * Creates and wires up the selected property so that pseudo-classes
         * are updated whenever the selection state changes.
         */
        selectedCellProperty().addListener((obs, wasSelected, val) ->
                pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, val));



        focusedCellProperty().addListener((obs, wasSelected, val) ->
                pseudoClassStateChanged(PSEUDO_CLASS_FOCUSED, val));

        focusedProperty().addListener((obs, o, f) -> {
            if (!f){
                setFocusedCell(false);
            }
        });
    }


    /**
     * The focused state property of this cell.
     * <p>
     * This property is read-only for external callers. The internal state
     * is managed by the control and updates whenever the cell gains or
     * loses keyboard focus.
     */
    private final ReadOnlyBooleanWrapper focused =
            new ReadOnlyBooleanWrapper(this, "focused", false);

    /**
     * Sets the focused state of this cell.
     * <p>
     * Intended for internal use by the control.
     *
     * @param value {@code true} to mark as focused; {@code false} otherwise
     */
    public final void setFocusedCell(boolean value) {
        focused.set(value);
    }

    /**
     * Returns whether this cell is focused.
     *
     * @return {@code true} if focused; otherwise {@code false}
     */
    public final boolean isFocusedCell() {
        return focused.get();
    }

    /**
     * Returns the focused state property as a read-only property.
     *
     * @return the read-only focused {@link ReadOnlyBooleanProperty}
     */
    public final ReadOnlyBooleanProperty focusedCellProperty() {
        return focused.getReadOnlyProperty();
    }


    /**
     * The selected state property of this cell.
     * <p>
     * This property is read-only for external callers. The internal state
     * is managed by the {@link NfxGridListView}'s {@link SelectionModel}.
     */
    private final ReadOnlyBooleanWrapper selected =
            new ReadOnlyBooleanWrapper(this, "selected", false);

    /**
     * Sets the selected state of this cell.
     * <p>
     * Intended for internal use by the control or selection model.
     *
     * @param value {@code true} to mark as selected; {@code false} otherwise
     */
    public final void setSelectedCell(boolean value) {
        selected.set(value);
    }

    /**
     * Returns whether this cell is selected.
     *
     * @return {@code true} if selected; otherwise {@code false}
     */
    public final boolean isSelectedCell() {
        return selected.get();
    }

    /**
     * Returns the selected state property as a read-only property.
     *
     * @return the read-only selected {@link ReadOnlyBooleanProperty}
     */
    public final ReadOnlyBooleanProperty selectedCellProperty() {
        return selected.getReadOnlyProperty();
    }

    /**
     * Returns the item property of this cell.
     *
     * @return the {@link ObjectProperty} for the item
     */
    private ObjectProperty<T> itemProperty() {
        if (item == null) {
            item = new SimpleObjectProperty<>(this, "item");
        }
        return item;
    }

    /**
     * Returns the item contained in this cell.
     *
     * @return the item, or {@code null} if none is set
     */
    public final T getItem() {
        return itemProperty().get();
    }

    /**
     * Sets the item contained in this cell.
     *
     * @param item the item to set
     */
    final void setItem(T item) {
        itemProperty().set(item);
    }

    /**
     * Updates this cell with the given item and adjusts selection state.
     *
     * @param item the item to display in this cell
     */
    public void update(T item) {
        setItem(item);

        final boolean empty = (item == null);
        setSelectedCell(!empty && isSelectable(item));

        // always remove old handlers first (idempotent)
        removeEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
        removeEventHandler(KeyEvent.KEY_PRESSED, keyEvent);

        if (!empty) {
            // only handle keys/mouse when there’s an item
            addEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
            addEventHandler(KeyEvent.KEY_PRESSED, keyEvent);
            setFocusTraversable(true);
        } else {
            setFocusTraversable(false);
        }
    }

    /**
     * Returns the {@link NfxGridListView} that contains this cell.
     *
     * @return the containing list view
     */
    public final NfxGridListView<T> getListView() {
        return listView;
    }

    /**
     * Returns the index of the specified item within the containing list view.
     *
     * @param item the item whose index is to be located
     * @return the index of the item, or {@code -1} if not found or the list is empty
     */
    public final int getIndex(T item) {
        return getListView().getItems().isEmpty() ? -1 : getListView().getItems().indexOf(item);
    }

    /**
     * Sets textual content for this cell by installing a {@link Label} as its graphic.
     *
     * @param text the text to display; ignored if {@code null}
     */
    public final void setText(String text) {
        if (text != null) {
            Label label = new Label(text);
            label.getStyleClass().add("default-grid-cell-factory-label");
            setGraphics(label);
        }
    }

    /**
     * Sets the graphic node for this cell, replacing any existing children.
     *
     * @param graphics the node to install; ignored if {@code null}
     */
    public final void setGraphics(Node graphics) {
        if (graphics != null) {
            getChildren().clear();
            getChildren().add(graphics);
        }
    }

    /**
     * Determines whether the specified item should be considered selected.
     *
     * @param item the item to test
     * @return {@code true} if the item is selected (by value or index); otherwise {@code false}
     */
    protected final boolean isSelectable(T item) {
        return getListView().getSelectionModel().getSelectedItems().stream()
                .anyMatch(selected -> Objects.equals(selected, item)
                        || Objects.equals(getIndex(selected), getIndex(item)))
                || getListView().getSelectionModel().isSelected(item);
    }

    /**
     * Lays out child nodes to fill the entire bounds of this cell.
     */
    @Override
    protected final void layoutChildren() {
        for (Node child : getChildren()) {
            child.resizeRelocate(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Final override
     * @return List
     */
    @Override
    protected final ObservableList<Node> getChildren() {
        return super.getChildren();
    }
}

