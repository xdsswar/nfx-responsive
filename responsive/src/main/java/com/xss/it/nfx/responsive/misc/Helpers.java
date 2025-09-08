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

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

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
 * @since September 08, 2025
 * <p>
 * Created on 09/08/2025 at 14:37
 */
public class Helpers {
    /**
     * Builds (but does not start) a fade-in animation for the given node.
     * <p>
     * The timeline animates the node's {@code opacity} to {@code 1.0} over the specified
     * duration. The starting opacity is whatever the node currently has; callers who want
     * a full fade-in should typically set {@code node.setOpacity(0)} before playing.
     * <p>
     * Note: The returned {@link Timeline} is not automatically played — call {@link Timeline#play()}.
     * Must be used on the JavaFX Application Thread.
     *
     * @param node   the node to fade in (non-null)
     * @param delay  total animation duration in milliseconds
     * @return a configured timeline that fades the node to fully opaque
     */
    public static Timeline fadeIn(Node node, int delay) {
        Duration duration = Duration.millis(delay);
        return new Timeline(
                new KeyFrame(duration,
                        new KeyValue(node.opacityProperty(), 1.0, Interpolator.EASE_BOTH))
        );
    }

    /**
     * Builds (but does not start) a fade-out animation for the given node.
     * <p>
     * The timeline animates the node's {@code opacity} to {@code 0.0} over the specified
     * duration. The starting opacity is whatever the node currently has; callers who want
     * a full fade-out should typically ensure the node is visible/opaque before playing.
     * <p>
     * Note: The returned {@link Timeline} is not automatically played — call {@link Timeline#play()}.
     * Must be used on the JavaFX Application Thread.
     *
     * @param node   the node to fade out (non-null)
     * @param delay  total animation duration in milliseconds
     * @return a configured timeline that fades the node to fully transparent
     */
    public static Timeline fadeOt(Node node, int delay) {
        Duration duration = Duration.millis(delay);
        return new Timeline(
                new KeyFrame(duration,
                        new KeyValue(node.opacityProperty(), 0.0, Interpolator.EASE_BOTH))
        );
    }

}
