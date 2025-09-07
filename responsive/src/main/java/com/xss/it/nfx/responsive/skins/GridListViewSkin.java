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

import com.xss.it.nfx.responsive.base.GridListDelegate;
import javafx.scene.control.SkinBase;
import xss.it.nfx.responsive.control.NfxGridListView;

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
 * @since September 05, 2025
 * <p>
 * Created on 09/05/2025 at 17:58
 * <p>
 * The skin implementation for {@link NfxGridListView}.
 * <p>
 * This class provides the visual representation of the {@code NfxGridListView}
 * by delegating layout and rendering to a {@link GridListDelegate}.
 *
 * @param <T> the type of items contained within the {@code NfxGridListView}
 */
public final class GridListViewSkin<T> extends SkinBase<NfxGridListView<T>> {

    /**
     * The delegate responsible for layout and rendering logic
     * of the {@code NfxGridListView}.
     */
    private final GridListDelegate<T> delegate;

    /**
     * Creates a new {@code GridListViewSkin} instance.
     *
     * @param control  the {@link NfxGridListView} control that this skin is attached to
     * @param delegate the {@link GridListDelegate} used for layout and rendering
     */
    public GridListViewSkin(NfxGridListView<T> control, GridListDelegate<T> delegate) {
        super(control);
        this.delegate = delegate;

        initialize();
    }

    /**
     * Initializes the skin by adding the delegate to the scene graph.
     */
    private void initialize() {
        getChildren().add(delegate);


    }
}
