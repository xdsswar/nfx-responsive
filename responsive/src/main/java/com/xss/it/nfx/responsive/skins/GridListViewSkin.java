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
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SkinBase;
import xss.it.nfx.responsive.control.NfxGridListView;

import static com.xss.it.nfx.responsive.misc.Helpers.fadeIn;
import static com.xss.it.nfx.responsive.misc.Helpers.fadeOt;

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

    private final ListChangeListener<T> listener;

    /**
     * Creates a new {@code GridListViewSkin} instance.
     *
     * @param control  the {@link NfxGridListView} control that this skin is attached to
     * @param delegate the {@link GridListDelegate} used for layout and rendering
     */
    public GridListViewSkin(NfxGridListView<T> control, GridListDelegate<T> delegate) {
        super(control);
        this.delegate = delegate;

        listener = c ->{
            while (c.next()){
                handleListPlaceHolderBasedOnItems(getSkinnable().getItems());
            }
        };

        initialize();
    }

    /**
     * Initializes the skin by adding the delegate to the scene graph.
     */
    private void initialize() {
        getChildren().add(delegate);

        handleListPlaceHolderBasedOnItems(getSkinnable().getItems());

        getSkinnable().itemsProperty().addListener((obs, o, n) -> {
            if (o != null){
                o.removeListener(listener);
            }

            if (n != null){
                handleListPlaceHolderBasedOnItems(n);
                n.addListener(listener);
            }
        });
    }


    /**
     * Handles the visibility of the placeholder node based on the presence of items in the list.
     * If the list is empty or null, the placeholder is displayed; otherwise, the list content is shown.
     *
     * @param items the observable list of items to monitor
     */
    private void handleListPlaceHolderBasedOnItems(ObservableList<T> items){
        final int delay = 100;
        if (items.isEmpty() && getSkinnable().getPlaceHolder() != null){
            Timeline fo = fadeOt(delegate, delay);
            fo.setOnFinished(e->{
                getChildren().remove(delegate);
                if (!getChildren().contains(getSkinnable().getPlaceHolder())) {
                    getSkinnable().getPlaceHolder().setOpacity(0);
                    getChildren().add(getSkinnable().getPlaceHolder());
                    fadeIn(getSkinnable().getPlaceHolder(), delay).play();
                }
            });
            fo.play();
        }
        else {
            Timeline fo = fadeOt(getSkinnable().getPlaceHolder(), delay);
            fo.setOnFinished(e->{
                getChildren().remove(getSkinnable().getPlaceHolder());
                if (!getChildren().contains(delegate)) {
                    delegate.setOpacity(0);
                    getChildren().add(delegate);
                    fadeIn(delegate, delay * 2).play();
                }
            });
            fo.play();
        }
    }
}
