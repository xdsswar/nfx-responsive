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

import javafx.scene.Node;

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
 * Created on 09/05/2025 at 18:02
 * <p>
 * A lightweight wrapper that associates an item type {@code T} with a JavaFX {@link Node}.
 * <p>
 * This interface allows treating UI nodes as "cells" in a virtualized container
 * without requiring a heavyweight cell class hierarchy. It is most useful in
 * virtualized or custom list/grid views where nodes need to be handled uniformly.
 *
 * @param <T> the type of the item that this cell may represent
 * @param <N> the type of the JavaFX {@link Node} contained in this cell
 */
public interface Cell<T, N extends Node> {

    /**
     * Wraps a given {@link Node} into a {@code Cell} instance.
     * <p>
     * This provides a simple adapter so that any node can be treated
     * as a {@code Cell}, without additional boilerplate.
     *
     * @param <T>  the type of the item (unused in this wrapper, but retained
     *             for consistency with cell contracts)
     * @param <N>  the type of the node
     * @param node the node to wrap; must not be {@code null}
     * @return a {@code Cell} instance exposing the given node
     */
    static <T, N extends Node> Cell<T, N> wrap(N node) {
        return new Cell<>() {
            /**
             * Returns the underlying {@link Node} wrapped by this {@code Cell}.
             *
             * @return the non-null node instance
             */
            @Override
            public N node() {
                return node;
            }

            /**
             * Returns a string representation of this {@code Cell}.
             * <p>
             * This implementation delegates to {@link Node#toString()},
             * which is useful for logging and debugging. If the wrapped
             * node is {@code null}, the string {@code "null"} is returned.
             *
             * @return a string representation of the wrapped node
             */
            @Override
            public String toString() {
                return node != null ? node.toString() : "null";
            }

        };
    }

    /**
     * Returns the underlying {@link Node} associated with this cell.
     *
     * @return the node instance, never {@code null}
     */
    N node();
}
