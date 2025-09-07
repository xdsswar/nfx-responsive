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

package xss.it.nfx.responsive.misc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.nfx.responsive.misc package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 05, 2025
 * <p>
 * Created on 09/05/2025 at 18:09
 */
public class SelectionModel<T> {
    /**
     * Selection mode
     */
    public enum Mode {
        /**
         * Single mode.
         */
        SINGLE,

        /**
         * Multiple mode.
         */
        MULTIPLE
    }

    /**
     * The list of selected items.
     */
    private final ObservableList<T> selectedItems = FXCollections.observableArrayList();

    /**
     * The selection mode.
     */
    private Mode mode = Mode.SINGLE;

    /**
     * Returns the list of selected items.
     * @return the observable list of selected items
     */
    public ObservableList<T> getSelectedItems() {
        return selectedItems;
    }

    /**
     * Sets the selection mode.
     * @param mode the new selection mode
     */
    public void setSelectionMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * Returns the current selection mode.
     * @return the current selection mode
     */
    public Mode getSelectionMode() {
        return mode;
    }

    /**
     * Selects the given item according to the current selection mode.
     * <ul>
     *   <li>In {@link Mode#SINGLE} mode, replaces the selection with this item
     *       if it is not already the sole selected item.</li>
     *   <li>In {@link Mode#MULTIPLE} mode, adds the item if it is not already
     *       present in the selection.</li>
     * </ul>
     *
     * @param item the item to select; ignored if {@code null}
     */
    @SuppressWarnings("unchecked")
    public void select(T item) {
        if (item == null) {
            return;
        }
        if (mode == Mode.SINGLE) {
            // Avoid churn if the same item is already the only selection
            if (!selectedItems.isEmpty() && Objects.equals(selectedItems.getFirst(), item)) {
                return;
            }
            selectedItems.setAll(item); // atomic clear + add
        } else {
            // Only add if not already in selection
            if (!selectedItems.contains(item)) {
                selectedItems.add(item);
            }
        }
    }


    /**
     * Selects multiple items.
     * @param items the list of items to select
     */
    public void select(List<T> items) {
        selectedItems.clear();
        for (T item : items) {
            if (!isSelected(item)) {
                selectedItems.add(item);
            }
        }
    }


    /**
     * Unselects an item.
     * @param item the item to be unselected
     */
    public void unselect(T item) {
        selectedItems.remove(item);
    }

    /**
     * Clears the selection.
     */
    public void clearSelection() {
        selectedItems.clear();
    }

    /**
     * Checks if an item is selected.
     * @param item the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(T item) {
        return selectedItems.contains(item);
    }


    @SuppressWarnings("unchecked")
    public void selectExclusive(T item) {
        if (item == null) return;
        // atomic clear + add
        selectedItems.setAll(item);
    }


}
