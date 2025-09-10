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

package com.xss.it.nfx.responsive.misc.property;

import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the com.xss.it.nfx.responsive.misc.property package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 10, 2025
 * <p>
 * Created on 09/10/2025 at 14:54
 * <p>
 * A styleable double property that can be programmatically "user-overridden,"
 * preventing AUTHOR/USER_AGENT CSS from changing it while the override is active.
 *
 * <p>When set via {@link #set(double)}, the property is marked as a user override
 * (see {@link #isUserSet()}). While this flag is true, AUTHOR and USER_AGENT CSS
 * will be ignored; however, INLINE styles always apply. Call
 * {@link #clearUserOverride()} to relinquish control back to CSS.</p>
 */
public  class UserStyleableDoubleProperty extends SimpleStyleableDoubleProperty {

    /**
     * Tracks whether the value was set by user code.
     *
     * <p>When {@code true}, AUTHOR/USER_AGENT CSS will not update this property.
     * INLINE CSS still takes effect regardless of this flag.</p>
     */
    private boolean userSet = false;

    /**
     * Creates a new user-overridable styleable double property.
     *
     * @param cssMetaData  the CSS metadata describing how this property maps to CSS
     * @param bean         the owning bean (node/control) for CSS association
     * @param name         the logical property name
     * @param initialValue the initial double value
     */
    public UserStyleableDoubleProperty(CssMetaData<? extends Styleable, Number> cssMetaData,
                                       Object bean,
                                       String name,
                                       Double initialValue) {
        super(cssMetaData, bean, name, initialValue);
    }

    /**
     * Sets the property value and marks it as a user override.
     *
     * <p>After calling this, AUTHOR/USER_AGENT CSS will no longer change this
     * property until {@link #clearUserOverride()} is invoked. INLINE CSS may still
     * apply on top of this value.</p>
     *
     * @param newValue the new double value
     */
    @Override
    public void set(double newValue) {
        userSet = true;
        super.set(newValue);
    }

    /**
     * Applies a CSS value depending on the style origin and override state.
     *
     * <p>If the origin is {@link StyleOrigin#INLINE}, the value is always applied.
     * For AUTHOR or USER_AGENT, the value is applied only when the property is not
     * currently marked as a user override ({@link #isUserSet()} is {@code false}).</p>
     *
     * @param origin the CSS style origin (INLINE, AUTHOR, or USER_AGENT)
     * @param v      the value supplied by the CSS engine
     */
    @Override
    public void applyStyle(StyleOrigin origin, Number v) {
        if (origin == StyleOrigin.INLINE || !userSet) {
            super.applyStyle(origin, v);
        }
    }

    /**
     * Clears the user override flag, allowing CSS to take control again.
     *
     * <p>After calling this, subsequent CSS passes from AUTHOR/USER_AGENT (and INLINE)
     * may update the property value.</p>
     */
    public void clearUserOverride() {
        userSet = false;
    }

    /**
     * Indicates whether the property has been programmatically overridden by the user.
     *
     * @return {@code true} if last set via {@link #set(double)} and not yet cleared; otherwise {@code false}
     */
    public boolean isUserSet() {
        return userSet;
    }
}
