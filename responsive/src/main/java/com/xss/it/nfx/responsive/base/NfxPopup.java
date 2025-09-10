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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;

import static xss.it.nfx.responsive.layout.NfxFluidPane.extractStyleSheets;

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
 * @since September 10, 2025
 * <p>
 * Created on 09/10/2025 at 00:59
 * <p>
 * Lightweight popup that can display a single JavaFX {@link Parent} as its content.
 *
 * <p>Supply the UI via {@link #setRoot(Parent)} and configure window behavior
 * (e.g., auto-hide, event filters) inside {@link #initialize()}.</p>
 */
public final class NfxPopup extends Popup {
    /**
     * Flag indicating whether this helper has already been attached.
     *
     * <p>Declared {@code volatile} so that updates made by one thread are
     * immediately visible to others, ensuring safe use across the JavaFX
     * application thread and any background threads.</p>
     */
    private volatile boolean attached = false;

    /**
     * The base container node for this control.
     *
     * <p>Acts as the root {@link StackPane} that holds the chart canvas,
     * overlays (such as tooltips or hover popups), and any additional
     * decoration nodes. Declared {@code final} since it is created once
     * during construction and never reassigned.</p>
     */
    private final StackPane base;

    /**
     * Constructs a new popup and performs one-time initialization.
     */
    public NfxPopup() {
        super();
        base = new StackPane();
        initialize();
    }

    /**
     * Hook for one-time setup (e.g., styles, event filters, window flags).
     * <p>Called from the constructor.</p>
     */
    private void initialize() {
        base.setCache(false);
        setAutoFix(true);
        setAutoHide(false);
        setHideOnEscape(true);

        handleRoot(getRoot());
        rootProperty().addListener((obs, o, p) -> handleRoot(p));

        setOnShowing(e->{
            Node node = getOwnerNode();
            Scene scene = node.getScene();
            if (scene != null && getScene() != null){
                getScene().getStylesheets().addAll(new ArrayList<>(scene.getStylesheets()));
            }
        });

        getContent().add(base);
    }

    /**
     * The root node to be shown inside this popup.
     *
     * <p><b>Note:</b> This class only exposes the property; callers are expected to
     * mirror it into the popup's content, for example:
     * <pre>{@code
     * popup.rootProperty().addListener((obs, oldRoot, newRoot) -> {
     *     popup.getContent().setAll(newRoot);
     * });
     * }</pre>
     * </p>
     */
    private ObjectProperty<Parent> root;

    /**
     * Returns the current root node to be displayed in the popup.
     *
     * @return the root {@link Parent}, or {@code null} if none
     */
    public Parent getRoot() {
        return rootProperty().get();
    }

    /**
     * The JavaFX property for the popup's root node.
     *
     * <p>Lazily initialized.</p>
     *
     * @return the {@link ObjectProperty} holding the root node
     */
    public ObjectProperty<Parent> rootProperty() {
        if (root == null){
            root = new SimpleObjectProperty<>();
        }
        return root;
    }

    /**
     * Sets the root node to display in the popup.
     *
     * @param root the UI content; may be {@code null}
     */
    public void setRoot(Parent root) {
        rootProperty().set(root);
    }

    /**
     * Shows the popup at the given screen coordinates, or repositions it if already visible.
     *
     * <p>If the popup is not currently showing, this delegates to {@link Popup#show(Node, double, double)}.
     * If it is already showing, the popup window is moved to the new coordinates via {@link #setX(double)}
     * and {@link #setY(double)} without re-opening it (avoids flicker and focus changes).</p>
     *
     * <p><b>Coordinates:</b> {@code screenX} and {@code screenY} are in <em>screen</em> space, not relative
     * to the owner node.</p>
     *
     * <p><b>Threading:</b> Must be called on the JavaFX Application Thread.</p>
     *
     * @param owner   the owner node whose window will own this popup; must be attached to a scene/window
     * @param screenX the target X position on the screen
     * @param screenY the target Y position on the screen
     */
    public void show(Node owner, double screenX, double screenY) {
        if (getRoot() == null) return;
        if (!isShowing()) {
            super.show(owner, screenX, screenY);
        } else {
            move(screenX, screenY);
        }
        getRoot().applyCss();
    }

    /**
     * Moves the popup to new screen coordinates if it is currently visible.
     *
     * @param screenX the new screen-space X coordinate
     * @param screenY the new screen-space Y coordinate
     */
    public void move(double screenX, double screenY) {
        if (getRoot() == null) return;
        if (isShowing()) {
            setX(screenX);
            setY(screenY);
        }
    }

    /**
     * Attaches stylesheets from the provided {@code parent} and {@code scene} to the tooltip root.
     * <p>
     * Collects stylesheets via {@code extractStyleSheets(parent, scene)}, clears any existing
     * stylesheets on the tooltip root, and applies the collected list.
     * </p>
     *
     * @param parent the parent whose stylesheets should be considered (may be {@code null})
     * @param scene  the scene whose stylesheets should be considered (may be {@code null})
     */
    public void attach(Parent parent, Scene scene){
        if (getRoot() == null || attached){
            return;
        }
        List<String> extracted = extractStyleSheets(parent, scene);
        base.getStylesheets().setAll(extracted);
        attached = true;
    }



    /**
     * Adds the given root node to this popup's content list.
     *
     * <p>If {@code parent} is {@code null}, the method is a no-op. Otherwise, the node
     * is appended to {@link #getContent()}.</p>
     *
     * <p><b>Notes & caveats:</b>
     * <ul>
     *   <li>This method does <em>not</em> clear existing content; repeated calls will
     *       accumulate multiple nodes. If you intend a single-root popup, prefer
     *       {@code getContent().setAll(parent)}.</li>
     *   <li>The supplied node must not already be attached to another parent; JavaFX
     *       will throw an {@link IllegalArgumentException} if it is.</li>
     *   <li>Must be called on the JavaFX Application Thread.</li>
     * </ul>
     * </p>
     *
     * @param parent the root UI node to add; may be {@code null}
     */
    private void handleRoot(Parent parent){
        if (parent == null){
            return;
        }
        base.getChildren().clear();
        base.getChildren().add(parent);
    }
}

