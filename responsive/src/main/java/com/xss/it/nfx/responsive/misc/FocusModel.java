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

package com.xss.it.nfx.responsive.misc;

import com.xss.it.nfx.responsive.base.GridListDelegate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import xss.it.nfx.responsive.control.NfxGridCell;

import java.util.Objects;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the com.xss.it.nfx.responsive.misc package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 06, 2025
 * <p>
 * Created on 09/06/2025 at 13:27
 * <p>
 * Item-only focus model that guarantees at most one focused item (or none).
 * <p>
 * This model is intentionally independent of indices and the backing items list.
 * It delegates visual focus updates to the UI layer via {@link GridListDelegate#getCell(Object)}
 * and {@link NfxGridCell#setFocusedCell(boolean)}.
 *
 * @param <T> the item type represented by each cell
 */
public final class FocusModel<T> {

    /**
     * The currently focused item (nullable). Exposed as a read-only property via
     * {@link #focusedItemProperty()} for bindings/observers.
     */
    private final ObjectProperty<T> focused = new SimpleObjectProperty<>(null);

    /**
     * Bridge to retrieve cells for items so their focused visuals can be toggled
     * when the focused item changes.
     */
    private final GridListDelegate<T> delegate;

    /**
     * Creates a new focus model bound to the given delegate.
     * Sets up a listener to flip focus visuals on the old/new cells whenever
     * {@link #focused} changes.
     *
     * @param delegate the cell lookup delegate; must not be {@code null}
     */
    public FocusModel(GridListDelegate<T> delegate) {
        this.delegate = delegate;

        // Initialize (no previous focused item)
        handleFocused(null, getFocusedItem());

        // On focus change, update old/new cells' visual focus flags
        focused.addListener((obs, o, i) -> handleFocused(o, i));
    }

    /**
     * Returns the currently focused item, or {@code null} if none.
     *
     * @return the focused item, or {@code null}
     */
    public T getFocusedItem() {
        return focused.get();
    }

    /**
     * Sets the focused item (may be {@code null} to clear focus).
     * Triggers visual focus updates on the corresponding cells via the delegate.
     *
     * @param item the item to focus, or {@code null} to clear focus
     */
    public void setFocusedItem(T item) {
        focused.set(item);
    }

    /**
     * Returns a read-only view of the focused item property for observers/bindings.
     *
     * @return the read-only focused item property
     */
    public ReadOnlyObjectProperty<T> focusedItemProperty() {
        return focused;
    }

    /**
     * Notifies the model that an item was removed upstream. If the removed item is
     * currently focused, focus is cleared to avoid dangling references.
     *
     * @param removed the item that was removed
     */
    public void onItemRemoved(T removed) {
        if (Objects.equals(getFocusedItem(), removed)) {
            clearFocus();
        }
    }

    /**
     * Clears focus (no item focused).
     */
    public void clearFocus() {
        setFocusedItem(null);
    }

    /**
     * Internal handler invoked whenever the focused item changes.
     * Turns off visual focus on the old cell (if any) and turns it on for the new cell (if any).
     *
     * @param o the previously focused item (may be {@code null})
     * @param i the newly focused item (may be {@code null})
     */
    private void handleFocused(T o, T i) {
        if (o != null) {
            NfxGridCell<T> oc = delegate.getCell(o);
            if (oc != null) {
                oc.setFocusedCell(false);
            }
        }

        if (i != null) {
            NfxGridCell<T> ic = delegate.getCell(i);
            if (ic != null) {
                ic.setFocusedCell(true);
            }
        }
    }
}
