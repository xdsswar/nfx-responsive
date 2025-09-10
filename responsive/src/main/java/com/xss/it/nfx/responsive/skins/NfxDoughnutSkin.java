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

package com.xss.it.nfx.responsive.skins;

import com.xss.it.nfx.responsive.base.NfxDoughnutDelegate;
import javafx.scene.control.SkinBase;
import xss.it.nfx.responsive.control.NfxDoughnutChart;
import xss.it.nfx.responsive.misc.DonutData;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the com.xss.it.nfx.responsive.skins package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 08, 2025
 * <p>
 * Created on 09/08/2025 at 15:21
 * <p>
 * Default skin implementation for the {@link NfxDoughnutChart} control.
 * <p>
 * This skin is responsible for attaching and managing the visual
 * representation of the {@code NfxDoughnutChart}. It delegates drawing
 * and layout logic to an associated {@link NfxDoughnutDelegate}.
 * </p>
 *
 * @param <T> the type of {@link DonutData} used by the control
 */
public final class NfxDoughnutSkin<T extends DonutData<? extends Number>> extends SkinBase<NfxDoughnutChart<T>> {
    /**
     *  Delegate responsible for handling layout and rendering logic
     */
    private final NfxDoughnutDelegate<T> delegate;

    /**
     * Creates a new skin instance for the given {@link NfxDoughnutChart}.
     *
     * @param control  the {@link NfxDoughnutChart} control that this skin attaches to
     * @param delegate the delegate that performs rendering and layout
     */
    public NfxDoughnutSkin(NfxDoughnutChart<T> control, NfxDoughnutDelegate<T> delegate) {
        super(control);
        this.delegate = delegate;

        initialize();
    }

    /**
     * Initializes the skin by adding the delegate to the skin's children.
     */
    private void initialize(){
        getChildren().add(delegate);
    }
}
