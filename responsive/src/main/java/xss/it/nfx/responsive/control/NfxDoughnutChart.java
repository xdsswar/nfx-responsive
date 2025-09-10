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

import com.xss.it.nfx.responsive.base.NfxDoughnutDelegate;
import com.xss.it.nfx.responsive.misc.property.UserStyleableDoubleProperty;
import com.xss.it.nfx.responsive.misc.property.UserStyleableObjectProperty;
import com.xss.it.nfx.responsive.skins.NfxDoughnutSkin;
import javafx.beans.DefaultProperty;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import xss.it.nfx.responsive.misc.ChartCallBack;
import xss.it.nfx.responsive.misc.DonutData;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * @since September 08, 2025
 * <p>
 * Created on 09/08/2025 at 15:13
 */
@DefaultProperty("data")
public final class NfxDoughnutChart<T extends DonutData<? extends Number>> extends Control {
    /**
     * The default stylesheet used by {@code NfxDoughnutChart}.
     * <p>
     * This CSS file defines the base styles for the doughnut chart control,
     * including colors, fonts, and legend styling. It is loaded from the
     * classpath at {@code /xss/it/nfx/nfx-doughnut-chart.css}.
     * </p>
     */
    private static final String STYLE_SHEET = load("/xss/it/nfx/nfx-doughnut-chart.css").toExternalForm();

    /**
     * Delegate instance responsible for handling the rendering and logic of
     * the NfxDoughnutChart chart.
     */
    private final NfxDoughnutDelegate<T> delegate;

    /**
     * Constructs a new NfxDoughnutChart chart.
     * <p>
     * Initializes the chart and assigns a delegate to manage drawing
     * and behavior.
     * </p>
     */
    public NfxDoughnutChart() {
        super();
        delegate = new NfxDoughnutDelegate<>(this);
        getStyleClass().add("nfx-doughnut-chart");
        getStylesheets().add(getUserAgentStylesheet());
    }

    /**
     * Holds the observable list of data items for the chart.
     * <p>
     * Wrapped in an {@link ObjectProperty} so it can be observed and bound.
     * </p>
     */
    private ObjectProperty<ObservableList<T>> data;

    /**
     * Returns the current data list used by the chart.
     *
     * @return the observable list of data items
     */
    public ObservableList<T> getData() {
        return dataProperty().get();
    }

    /**
     * Provides access to the data property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with an empty observable list.
     * </p>
     *
     * @return the object property wrapping the data list
     */
    public ObjectProperty<ObservableList<T>> dataProperty() {
        if (data == null){
            data = new SimpleObjectProperty<>(this, "data", FXCollections.observableArrayList());
        }
        return data;
    }


    /**
     * Sets the observable list of data items for the chart.
     *
     * @param data the new observable list of data items
     */
    public void setData(ObservableList<T> data) {
        dataProperty().set(data);
    }

    /**
     * Title property for the chart.
     * <p>
     * Wrapped in a {@link StringProperty} so it can be observed and bound.
     * Defaults to "NfxDoughnutChart".
     * </p>
     */
    private StringProperty title;

    /**
     * Returns the current chart title.
     *
     * @return the chart title as a string
     */
    public String getTitle() {
        return titleProperty().get();
    }

    /**
     * Provides access to the title property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value "NfxDoughnutChart".
     * </p>
     *
     * @return the string property representing the chart title
     */
    public StringProperty titleProperty() {
        if (title == null){
            title = new SimpleStringProperty(this, "title", "NfxDoughnutChart");
        }
        return title;
    }

    /**
     * Sets the chart title.
     *
     * @param title the new title string
     */
    public void setTitle(String title) {
        titleProperty().set(title);
    }


    /**
     * Subtitle property for the chart.
     * <p>
     * Wrapped in a {@link StringProperty} so it can be observed and bound.
     * Defaults to "Subtitle".
     * </p>
     */
    private StringProperty subtitle;

    /**
     * Returns the current chart subtitle.
     *
     * @return the subtitle string
     */
    public String getSubtitle() {
        return subtitleProperty().get();
    }

    /**
     * Provides access to the subtitle property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value "Subtitle".
     * </p>
     *
     * @return the string property representing the chart subtitle
     */
    public StringProperty subtitleProperty() {
        if (subtitle == null){
            subtitle = new SimpleStringProperty(this, "subtitle", "Subtitle");
        }
        return subtitle;
    }

    /**
     * Sets the chart subtitle.
     *
     * @param subtitle the new subtitle string
     */
    public void setSubtitle(String subtitle) {
        subtitleProperty().set(subtitle);
    }


    /**
     * Legend square size property.
     * <p>
     * Determines the size (in pixels) of the square used as a marker
     * in the chart legend. Defaults to {@code 20}.
     * </p>
     */
    private DoubleProperty legendSquareSize;

    /**
     * Returns the current legend square size.
     *
     * @return the legend square size in pixels
     */
    public double getLegendSquareSize() {
        return legendSquareSizeProperty().get();
    }

    /**
     * Sets the legend square size.
     *
     * @param value the new size in pixels
     */
    public void setLegendSquareSize(double value) {
        legendSquareSizeProperty().set(value);
    }

    /**
     * Provides access to the legend square size property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 20}.
     * </p>
     *
     * @return the double property representing the legend square size
     */
    public DoubleProperty legendSquareSizeProperty() {
        if (legendSquareSize == null) {
            legendSquareSize = new UserStyleableDoubleProperty(
                    Styleables.LEGEND_SQUARE_SIZE,
                    this,
                    "legendSquareSize",
                    20d);
        }
        return legendSquareSize;
    }


    /**
     * Legend gap property.
     * <p>
     * Specifies the horizontal gap (in pixels) between the legend marker
     * and its corresponding label. Defaults to {@code 8}.
     * </p>
     */
    private DoubleProperty legendGap;

    /**
     * Returns the current legend gap.
     *
     * @return the legend gap in pixels
     */
    public double getLegendGap() {
        return legendGapProperty().get();
    }

    /**
     * Sets the legend gap.
     *
     * @param value the new gap in pixels
     */
    public void setLegendGap(double value) {
        legendGapProperty().set(value);
    }

    /**
     * Provides access to the legend gap property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 8}.
     * </p>
     *
     * @return the double property representing the legend gap
     */
    public DoubleProperty legendGapProperty() {
        if (legendGap == null) {
            legendGap = new UserStyleableDoubleProperty(
                    Styleables.LEGEND_GAP,
                    this,
                    "legendGap",
                    8d);
        }
        return legendGap;
    }

    /**
     * Legend item gap property.
     * <p>
     * Defines the vertical gap (in pixels) between individual legend items.
     * Defaults to {@code 14}.
     * </p>
     */
    private DoubleProperty legendItemGap;

    /**
     * Returns the current legend item gap.
     *
     * @return the legend item gap in pixels
     */
    public double getLegendItemGap() {
        return legendItemGapProperty().get();
    }

    /**
     * Sets the legend item gap.
     *
     * @param value the new gap in pixels
     */
    public void setLegendItemGap(double value) {
        legendItemGapProperty().set(value);
    }

    /**
     * Provides access to the legend item gap property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 14}.
     * </p>
     *
     * @return the double property representing the legend item gap
     */
    public DoubleProperty legendItemGapProperty() {
        if (legendItemGap == null) {
            legendItemGap = new UserStyleableDoubleProperty(
                    Styleables.LEGEND_ITEM_GAP,
                    this,
                    "legendItemGap",
                    14d);
        }
        return legendItemGap;
    }


    /**
     * Legend line gap property.
     * <p>
     * Specifies the vertical gap (in pixels) between lines of legend items.
     * Defaults to {@code 20}.
     * </p>
     */
    private DoubleProperty legendLineGap;

    /**
     * Returns the current legend line gap.
     *
     * @return the legend line gap in pixels
     */
    public double getLegendLineGap() {
        return legendLineGapProperty().get();
    }

    /**
     * Sets the legend line gap.
     *
     * @param value the new line gap in pixels
     */
    public void setLegendLineGap(double value) {
        legendLineGapProperty().set(value);
    }

    /**
     * Provides access to the legend line gap property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 20}.
     * </p>
     *
     * @return the double property representing the legend line gap
     */
    public DoubleProperty legendLineGapProperty() {
        if (legendLineGap == null) {
            legendLineGap = new UserStyleableDoubleProperty(
                    Styleables.LEGEND_LINE_GAP,
                    this,
                    "legendLineGap",
                    20d);
        }
        return legendLineGap;
    }


    /**
     * Inner radius property.
     * <p>
     * Represents the absolute inner radius (in pixels) of the doughnut’s
     * empty center hole. Defaults to {@code 0.5}.
     * </p>
     */
    private DoubleProperty innerRadius;

    /**
     * Returns the current inner radius.
     *
     * @return the inner radius in pixels
     */
    public double getInnerRadius() {
        return innerRadiusProperty().get();
    }

    /**
     * Sets the inner radius.
     *
     * @param value the new inner radius in pixels
     */
    public void setInnerRadius(double value) {
        innerRadiusProperty().set(value);
    }

    /**
     * Provides access to the inner radius property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 0.5}.
     * </p>
     *
     * @return the double property representing the inner radius
     */
    public DoubleProperty innerRadiusProperty() {
        if (innerRadius == null) {
            innerRadius = new UserStyleableDoubleProperty(
                    Styleables.INNER_RADIUS,
                    this,
                    "innerRadius",
                    50d)
            {
                @Override
                public void set(double newValue) {
                    double clamped = Math.max(0, Math.min(newValue, 100));
                    super.set(clamped);
                }
            };
        }
        return innerRadius;
    }

    /**
     * Animated property.
     * <p>
     * Determines whether the chart is drawn with animation effects.
     * Defaults to {@code true}.
     * </p>
     */
    private BooleanProperty animated;

    /**
     * Returns whether the chart is animated.
     *
     * @return {@code true} if animated, {@code false} otherwise
     */
    public boolean isAnimated() {
        return animatedProperty().get();
    }

    /**
     * Enables or disables chart animation effects.
     *
     * @param v {@code true} to enable animations, {@code false} to disable
     */
    public void setAnimated(boolean v) {
        animatedProperty().set(v);
    }

    /**
     * Provides access to the animated property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code true}.
     * </p>
     *
     * @return the boolean property controlling animation effects
     */
    public BooleanProperty animatedProperty() {
        if (animated == null) {
            animated = new SimpleBooleanProperty(this, "animated", true);
        }
        return animated;
    }


    /**
     * Animation delay property.
     * <p>
     * Defines the duration of the slice animation (in milliseconds).
     * Defaults to {@code 500}.
     * </p>
     */
    private DoubleProperty animationDelay;

    /**
     * Returns the current animation delay in milliseconds.
     *
     * @return the animation delay in ms
     */
    public double getAnimationDelay() {
        return animationDelayProperty().get();
    }

    /**
     * Sets the animation delay in milliseconds.
     *
     * @param ms the new animation delay
     */
    public void setAnimationDelay(double ms) {
        animationDelayProperty().set(ms);
    }

    /**
     * Provides access to the animation delay property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 500}.
     * </p>
     *
     * @return the double property representing the animation delay
     */
    public DoubleProperty animationDelayProperty() {
        if (animationDelay == null) {
            animationDelay = new SimpleDoubleProperty(this, "animationDelay", 500);
        }
        return animationDelay;
    }

    /**
     * Title font property.
     * <p>
     * Specifies the {@link Font} used for the chart title text.
     * Defaults to "Roboto, 16px".
     * </p>
     */
    private ObjectProperty<Font> titleFont;

    /**
     * Returns the current title font.
     *
     * @return the title font (never {@code null})
     */
    public Font getTitleFont() {
        return titleFontProperty().get();
    }

    /**
     * Sets the title font.
     *
     * @param f the new title font (must not be {@code null})
     */
    public void setTitleFont(Font f) {
        titleFontProperty().set(f);
    }

    /**
     * Provides access to the title font property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default font.
     * </p>
     *
     * @return the styleable property representing the title font
     */
    public  ObjectProperty<Font> titleFontProperty() {
        if (titleFont == null) {
            titleFont = new UserStyleableObjectProperty<>(
                    Styleables.TITLE_FONT,
                    this,
                    "titleFont",
                    Font.getDefault()
            );
        }
        return titleFont;
    }


    /**
     * Title fill property.
     * <p>
     * Defines the paint (color or gradient) used to render the chart title text.
     * Defaults to {@code Color.web("#444")}.
     * </p>
     */
    private ObjectProperty<Paint> titleFill;

    /**
     * Returns the current paint used for the title.
     *
     * @return the {@link Paint} applied to the title text
     */
    public Paint getTitleFill() {
        return titleFillProperty().get();
    }

    /**
     * Sets the paint used for the title.
     *
     * @param p the new {@link Paint} to apply to the title text
     */
    public void setTitleFill(Paint p) {
        titleFillProperty().set(p);
    }

    /**
     * Provides access to the title fill property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code Color.web("#444")}.
     * </p>
     *
     * @return the object property representing the title fill
     */
    public ObjectProperty<Paint> titleFillProperty() {
        if (titleFill == null) {
            titleFill = new UserStyleableObjectProperty<>(
                    Styleables.TITLE_FILL,
                    this,
                    "titleFill",
                    Color.web("#444")
            );
        }
        return titleFill;
    }

    /**
     * Subtitle font property.
     * <p>
     * Specifies the {@link Font} used for the chart subtitle text.
     * Defaults to "Roboto, 12px" if not set.
     * </p>
     */
    private ObjectProperty<Font> subtitleFont;

    /**
     * Returns the current subtitle font.
     *
     * @return the subtitle font (never {@code null})
     */
    public Font getSubtitleFont() {
        return subtitleFontProperty().get();
    }

    /**
     * Sets the subtitle font.
     *
     * @param f the new subtitle font (must not be {@code null})
     */
    public void setSubtitleFont(Font f) {
        subtitleFontProperty().set(f);
    }

    /**
     * Provides access to the subtitle font property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default font.
     * </p>
     *
     * @return the styleable property representing the subtitle font
     */
    public ObjectProperty<Font> subtitleFontProperty() {
        if (subtitleFont == null) {
            subtitleFont = new UserStyleableObjectProperty<>(
                    Styleables.SUBTITLE_FONT,
                    this,
                    "subtitleFont",
                    Font.getDefault()
            );
        }
        return subtitleFont;
    }


    /**
     * Subtitle fill property.
     * <p>
     * Defines the paint (color or gradient) used to render the chart subtitle text.
     * Defaults to {@code Color.web("#777")}.
     * </p>
     */
    private ObjectProperty<Paint> subtitleFill;

    /**
     * Returns the current paint used for the subtitle.
     *
     * @return the {@link Paint} applied to the subtitle text
     */
    public Paint getSubtitleFill() {
        return subtitleFillProperty().get();
    }

    /**
     * Sets the paint used for the subtitle.
     *
     * @param p the new {@link Paint} to apply to the subtitle text
     */
    public void setSubtitleFill(Paint p) {
        subtitleFillProperty().set(p);
    }

    /**
     * Provides access to the subtitle fill property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code Color.web("#777")}.
     * </p>
     *
     * @return the object property representing the subtitle fill
     */
    public ObjectProperty<Paint> subtitleFillProperty() {
        if (subtitleFill == null) {
            subtitleFill = new UserStyleableObjectProperty<>(
                    Styleables.SUBTITLE_FILL,
                    this,
                    "subtitleFill",
                    Color.web("#777")
            );
        }
        return subtitleFill;
    }


    /**
     * Chart background property.
     * <p>
     * Defines the paint used to fill the canvas background.
     * If {@code null}, the canvas remains transparent; otherwise it is
     * filled with the specified paint on each frame.
     * Defaults to {@code null}.
     * </p>
     */
    private ObjectProperty<Paint> chartBackground;

    /**
     * Returns the current chart background paint.
     *
     * @return the {@link Paint} applied to the canvas background, or {@code null} if transparent
     */
    public Paint getChartBackground() {
        return chartBackgroundProperty().get();
    }

    /**
     * Sets the chart background paint.
     *
     * @param p the new {@link Paint} to apply, or {@code null} for transparency
     */
    public void setChartBackground(Paint p) {
        chartBackgroundProperty().set(p);
    }

    /**
     * Provides access to the chart background property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code null}.
     * </p>
     *
     * @return the object property representing the chart background paint
     */
    public ObjectProperty<Paint> chartBackgroundProperty() {
        if (chartBackground == null) {
            chartBackground = new UserStyleableObjectProperty<>(
                    Styleables.CHART_BACKGROUND,
                    this,
                    "chartBackground",
                    null
            );
        }
        return chartBackground;
    }


    /**
     * Legend marker shape property.
     * <p>
     * Specifies the shape of the markers used in the legend.
     * Defaults to {@link LegendMarkerShape#CIRCLE}.
     * </p>
     */
    private ObjectProperty<LegendMarkerShape> legendMarkerShape;

    /**
     * Returns the current legend marker shape.
     *
     * @return the {@link LegendMarkerShape} used for legend markers
     */
    public LegendMarkerShape getLegendMarkerShape() {
        return legendMarkerShapeProperty().get();
    }

    /**
     * Sets the legend marker shape.
     *
     * @param s the new {@link LegendMarkerShape} to use for legend markers
     */
    public void setLegendMarkerShape(LegendMarkerShape s) {
        legendMarkerShapeProperty().set(s);
    }

    /**
     * Provides access to the legend marker shape property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@link LegendMarkerShape#CIRCLE}.
     * </p>
     *
     * @return the object property representing the legend marker shape
     */
    public ObjectProperty<LegendMarkerShape> legendMarkerShapeProperty() {
        if (legendMarkerShape == null) {
            legendMarkerShape = new SimpleObjectProperty<>(this, "legendMarkerShape", LegendMarkerShape.CIRCLE);
        }
        return legendMarkerShape;
    }


    /**
     * Legend marker corner radius property.
     * <p>
     * Defines the corner radius (in pixels) used when the legend marker
     * shape is {@code ROUNDED}. Values less than {@code 0} are clamped
     * to {@code 0}. Defaults to {@code 4}.
     * </p>
     */
    private DoubleProperty legendMarkerCornerRadius;

    /**
     * Returns the current legend marker corner radius.
     *
     * @return the corner radius in pixels
     */
    public double getLegendMarkerCornerRadius() {
        return legendMarkerCornerRadiusProperty().get();
    }

    /**
     * Sets the legend marker corner radius.
     * <p>
     * Negative values are clamped to {@code 0}.
     * </p>
     *
     * @param v the new corner radius in pixels
     */
    public void setLegendMarkerCornerRadius(double v) {
        legendMarkerCornerRadiusProperty().set(Math.max(0, v));
    }

    /**
     * Provides access to the legend marker corner radius property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 4}.
     * </p>
     *
     * @return the double property representing the legend marker corner radius
     */
    public DoubleProperty legendMarkerCornerRadiusProperty() {
        if (legendMarkerCornerRadius == null) {
            legendMarkerCornerRadius = new UserStyleableDoubleProperty(
                    Styleables.LEGEND_MARKER_CORNER_RADIUS,
                    this,
                    "legendMarkerCornerRadius",
                    4d);
        }
        return legendMarkerCornerRadius;
    }

    /**
     * Legend label font property.
     * <p>
     * Specifies the {@link Font} used for legend label text.
     * Defaults to "Roboto, 11px".
     * </p>
     */
    private ObjectProperty<Font> legendLabelFont;

    /**
     * Returns the current legend label font.
     *
     * @return the legend label font (never {@code null})
     */
    public Font getLegendLabelFont() {
        return legendLabelFontProperty().get();
    }

    /**
     * Sets the legend label font.
     *
     * @param f the new legend label font (must not be {@code null})
     */
    public void setLegendLabelFont(Font f) {
        legendLabelFontProperty().set(f);
    }

    /**
     * Provides access to the legend label font property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default font.
     * </p>
     *
     * @return the styleable property representing the legend label font
     */
    public ObjectProperty<Font> legendLabelFontProperty() {
        if (legendLabelFont == null) {
            legendLabelFont = new UserStyleableObjectProperty<>(
                    Styleables.LEGEND_LABEL_FONT,
                    this,
                    "legendLabelFont",
                    Font.getDefault()
            );
        }
        return legendLabelFont;
    }


    /**
     * Legend label fill property.
     * <p>
     * Defines the paint (color or gradient) used to render the legend label text.
     * Defaults to {@code Color.web("#333")}.
     * </p>
     */
    private ObjectProperty<Paint> legendLabelFill;

    /**
     * Returns the current paint used for legend labels.
     *
     * @return the {@link Paint} applied to legend label text
     */
    public Paint getLegendLabelFill() {
        return legendLabelFillProperty().get();
    }

    /**
     * Sets the paint used for legend labels.
     *
     * @param p the new {@link Paint} to apply to legend label text
     */
    public void setLegendLabelFill(Paint p) {
        legendLabelFillProperty().set(p);
    }

    /**
     * Provides access to the legend label fill property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code Color.web("#333")}.
     * </p>
     *
     * @return the object property representing the legend label fill
     */
    public ObjectProperty<Paint> legendLabelFillProperty() {
        if (legendLabelFill == null) {
            legendLabelFill = new UserStyleableObjectProperty<>(
                    Styleables.LEGEND_LABEL_FILL,
                    this,
                    "legendLabelFill",
                    Color.web("#333")
            );
        }
        return legendLabelFill;
    }

    /**
     * Legend order property.
     * <p>
     * Specifies the ordering of items in the legend.
     * Defaults to {@link LegendOrder#AS_IS}, which preserves the order
     * of items as provided in the data.
     * </p>
     */
    private ObjectProperty<LegendOrder> legendOrder;

    /**
     * Returns the current legend order.
     *
     * @return the {@link LegendOrder} defining how legend items are ordered
     */
    public LegendOrder getLegendOrder() {
        return legendOrderProperty().get();
    }

    /**
     * Sets the legend order.
     *
     * @param o the new {@link LegendOrder} to apply
     */
    public void setLegendOrder(LegendOrder o) {
        legendOrderProperty().set(o);
    }

    /**
     * Provides access to the legend order property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@link LegendOrder#AS_IS}.
     * </p>
     *
     * @return the object property representing the legend order
     */
    public ObjectProperty<LegendOrder> legendOrderProperty() {
        if (legendOrder == null) {
            legendOrder = new SimpleObjectProperty<>(this, "legendOrder", LegendOrder.AS_IS);
        }
        return legendOrder;
    }

    /**
     * Legend auto-hide property.
     * <p>
     * Determines whether the legend is automatically hidden
     * when there are no items to display.
     * Defaults to {@code true}.
     * </p>
     */
    private BooleanProperty legendAutoHide;

    /**
     * Returns whether legend auto-hide is enabled.
     *
     * @return {@code true} if the legend will automatically hide when empty,
     *         {@code false} otherwise
     */
    public boolean isLegendAutoHide() {
        return legendAutoHideProperty().get();
    }

    /**
     * Sets whether the legend should automatically hide when empty.
     *
     * @param value {@code true} to auto-hide the legend, {@code false} to always show it
     */
    public void setLegendAutoHide(boolean value) {
        legendAutoHideProperty().set(value);
    }

    /**
     * Provides access to the legend auto-hide property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code true}.
     * </p>
     *
     * @return the boolean property controlling legend auto-hide behavior
     */
    public BooleanProperty legendAutoHideProperty() {
        if (legendAutoHide == null) {
            legendAutoHide = new SimpleBooleanProperty(this, "legendAutoHide", true);
        }
        return legendAutoHide;
    }

    /**
     * Legend auto-hide minimum width property.
     * <p>
     * Specifies the minimum chart width (in pixels) below which
     * the legend will automatically hide, if {@link #isLegendAutoHide()}
     * is enabled.
     * Defaults to {@code 260}.
     * </p>
     */
    private DoubleProperty legendAutoHideMinWidth;

    /**
     * Returns the current legend auto-hide minimum width.
     *
     * @return the minimum chart width in pixels required to display the legend
     */
    public double getLegendAutoHideMinWidth() {
        return legendAutoHideMinWidthProperty().get();
    }

    /**
     * Sets the legend auto-hide minimum width.
     *
     * @param value the new minimum chart width in pixels
     */
    public void setLegendAutoHideMinWidth(double value) {
        legendAutoHideMinWidthProperty().set(value);
    }

    /**
     * Provides access to the legend auto-hide minimum width property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 260}.
     * </p>
     *
     * @return the double property representing the legend auto-hide minimum width
     */
    public DoubleProperty legendAutoHideMinWidthProperty() {
        if (legendAutoHideMinWidth == null) {
            legendAutoHideMinWidth = new SimpleDoubleProperty(this, "legendAutoHideMinWidth", 560);
        }
        return legendAutoHideMinWidth;
    }


    /**
     * Legend auto-hide minimum height property.
     * <p>
     * Specifies the minimum chart height (in pixels) below which
     * the legend will automatically hide, if {@link #isLegendAutoHide()}
     * is enabled.
     * Defaults to {@code 200}.
     * </p>
     */
    private DoubleProperty legendAutoHideMinHeight;

    /**
     * Returns the current legend auto-hide minimum height.
     *
     * @return the minimum chart height in pixels required to display the legend
     */
    public double getLegendAutoHideMinHeight() {
        return legendAutoHideMinHeightProperty().get();
    }

    /**
     * Sets the legend auto-hide minimum height.
     *
     * @param value the new minimum chart height in pixels
     */
    public void setLegendAutoHideMinHeight(double value) {
        legendAutoHideMinHeightProperty().set(value);
    }

    /**
     * Provides access to the legend auto-hide minimum height property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code 200}.
     * </p>
     *
     * @return the double property representing the legend auto-hide minimum height
     */
    public DoubleProperty legendAutoHideMinHeightProperty() {
        if (legendAutoHideMinHeight == null) {
            legendAutoHideMinHeight = new SimpleDoubleProperty(this, "legendAutoHideMinHeight", 320);
        }
        return legendAutoHideMinHeight;
    }

    /**
     * Show legend property.
     * <p>
     * Determines whether the legend is visible.
     * Defaults to {@code true}.
     * </p>
     */
    private BooleanProperty showLegend;

    /**
     * Returns whether the legend is currently visible.
     *
     * @return {@code true} if the legend is visible, {@code false} otherwise
     */
    public boolean isShowLegend() {
        return showLegendProperty().get();
    }

    /**
     * Provides access to the show legend property.
     * <p>
     * If the property has not been initialized, it will be lazily created
     * with the default value {@code true}.
     * </p>
     *
     * @return the boolean property representing legend visibility
     */
    public BooleanProperty showLegendProperty() {
        if (showLegend == null){
            showLegend = new SimpleBooleanProperty(this, "showLegend", true);
        }
        return showLegend;
    }

    /**
     * Sets whether the legend should be visible.
     *
     * @param showLegend {@code true} to show the legend, {@code false} to hide it
     */
    public void setShowLegend(boolean showLegend) {
        showLegendProperty().set(showLegend);
    }

    /**
     * The property representing the position of the chart legend.
     *
     * <p>This is a JavaFX {@link ObjectProperty} that allows binding and observation.
     * It controls where the legend is placed relative to the chart, with the default
     * value being {@link LegendPos#RIGHT}.</p>
     */
    private ObjectProperty<LegendPos> legendPosition;

    /**
     * Returns the current legend position.
     *
     * @return the {@link LegendPos} value of this chart's legend position
     */
    public LegendPos getLegendPosition() {
        return legendPositionProperty().get();
    }

    /**
     * Returns the {@link ObjectProperty} for the legend position.
     *
     * <p>This method lazily initializes the property if it has not been
     * created yet. The default position is {@link LegendPos#RIGHT}.</p>
     *
     * @return the legend position property
     */
    public ObjectProperty<LegendPos> legendPositionProperty() {
        if (legendPosition == null) {
            legendPosition = new SimpleObjectProperty<>(this, "legendPosition", LegendPos.RIGHT);
        }
        return legendPosition;
    }

    /**
     * Sets the legend position to the specified value.
     *
     * @param legendPosition the new {@link LegendPos} to apply
     */
    public void setLegendPosition(LegendPos legendPosition) {
        legendPositionProperty().set(legendPosition);
    }


    /**
     * The property that defines the gap (in pixels) between the chart content
     * and its surrounding border.
     *
     * <p>This is a JavaFX {@link DoubleProperty}, allowing binding and styling
     * through CSS. The default value is {@code 20.0}.</p>
     */
    private DoubleProperty borderGap;

    /**
     * Returns the current border gap value.
     *
     * @return the gap between the chart border and its content, in pixels
     */
    public double getBorderGap() {
        return borderGapProperty().get();
    }

    /**
     * Returns the {@link DoubleProperty} for the border gap.
     *
     * <p>This property is lazily initialized. By default, it is created as a
     * {@link SimpleStyleableDoubleProperty}, enabling CSS styling with the
     * property name {@code borderGap}. The default value is {@code 20.0}.</p>
     *
     * @return the border gap property
     */
    public DoubleProperty borderGapProperty() {
        if (borderGap == null) {
            borderGap = new UserStyleableDoubleProperty(
                    Styleables.BORDER_GAP,
                    this,
                    "borderGap",
                    20d
            );
        }
        return borderGap;
    }

    /**
     * Sets the border gap to the specified value.
     *
     * @param borderGap the new gap (in pixels) between the chart border and its content
     */
    public void setBorderGap(double borderGap) {
        borderGapProperty().set(borderGap);
    }


    /**
     * Chart-aware factory callback that builds popup content for a datum of type {@code T}.
     *
     * <p>The callback receives:
     * <ul>
     *   <li><b>data</b> — the datum ({@code T})</li>
     *   <li><b>owner</b> — the invoking chart ({@link NfxDoughnutChart}<{@code T}>)</li>
     * </ul>
     * and returns a {@link Parent} node to be shown as a popup (e.g., on hover/click).
     * The default implementation is provided by {@code defaultPopupFactory()}.</p>
     *
     * <p><b>Threading:</b> Invoked on the JavaFX Application Thread.</p>
     */
    private ObjectProperty<ChartCallBack<T, Parent, NfxDoughnutChart<T>>> popupFactory;

    /**
     * Returns the current chart-aware popup factory.
     *
     * @return the {@link ChartCallBack} that creates a {@link Parent} for the given datum and chart
     */
    public ChartCallBack<T, Parent, NfxDoughnutChart<T>> getPopupFactory() {
        return popupFactoryProperty().get();
    }

    /**
     * The JavaFX property for the popup factory.
     *
     * <p>Lazily initialized with the property name {@code "popupFactory"} and
     * a default value from {@code defaultPopupFactory()}.</p>
     *
     * @return the {@link ObjectProperty} holding the popup factory callback
     */
    public ObjectProperty<ChartCallBack<T, Parent, NfxDoughnutChart<T>>> popupFactoryProperty() {
        if (popupFactory == null){
            popupFactory = new SimpleObjectProperty<>(this, "popupFactory", defaultPopupFactory());
        }
        return popupFactory;
    }

    /**
     * Sets the chart-aware popup factory callback.
     *
     * @param popupFactory the {@link ChartCallBack} that creates popup content
     *                     for a datum and the owning {@link NfxDoughnutChart}
     */
    public void setPopupFactory(ChartCallBack<T, Parent, NfxDoughnutChart<T>> popupFactory) {
        popupFactoryProperty().set(popupFactory);
    }

    /**
     * Whether the chart's title should be shown.
     *
     * <p>This is a JavaFX {@link BooleanProperty} that supports binding and observation.
     * The default value is {@code true}.</p>
     */
    private BooleanProperty showTitle;

    /**
     * Returns whether the chart's title is currently shown.
     *
     * @return {@code true} if the title is visible; {@code false} otherwise
     */
    public boolean isShowTitle() {
        return showTitleProperty().get();
    }

    /**
     * The JavaFX property controlling title visibility.
     *
     * <p>Lazily initialized as a {@link SimpleBooleanProperty} named {@code "showTitle"}
     * with a default value of {@code true}.</p>
     *
     * @return the {@link BooleanProperty} backing the title visibility
     */
    public BooleanProperty showTitleProperty() {
        if (showTitle == null){
            showTitle = new SimpleBooleanProperty(this, "showTitle", true);
        }
        return showTitle;
    }

    /**
     * Sets whether the chart's title should be shown.
     *
     * @param showTitle {@code true} to show the title; {@code false} to hide it
     */
    public void setShowTitle(boolean showTitle) {
        showTitleProperty().set(showTitle);
    }

    /**
     * Whether the chart's subtitle should be shown.
     *
     * <p>This is a JavaFX {@link BooleanProperty} that supports binding and observation.
     * The default value is {@code true}.</p>
     */
    private BooleanProperty showSubtitle;

    /**
     * Returns whether the chart's subtitle is currently shown.
     *
     * @return {@code true} if the subtitle is visible; {@code false} otherwise
     */
    public boolean isShowSubtitle() {
        return showSubtitleProperty().get();
    }

    /**
     * The JavaFX property controlling subtitle visibility.
     *
     * <p>Lazily initialized as a {@link SimpleBooleanProperty} named {@code "showSubtitle"}
     * with a default value of {@code true}.</p>
     *
     * @return the {@link BooleanProperty} backing the subtitle visibility
     */
    public BooleanProperty showSubtitleProperty() {
        if (showSubtitle == null){
            showSubtitle = new SimpleBooleanProperty(this, "showSubtitle", true);
        }
        return showSubtitle;
    }

    /**
     * Sets whether the chart's subtitle should be shown.
     *
     * @param showSubtitle {@code true} to show the subtitle; {@code false} to hide it
     */
    public void setShowSubtitle(boolean showSubtitle) {
        showSubtitleProperty().set(showSubtitle);
    }

    /**
     * Whether interactive popups (e.g., hover/click details) are enabled.
     *
     * <p>This is a JavaFX {@link BooleanProperty} that supports binding and observation.
     * The default value is {@code true}.</p>
     */
    private BooleanProperty popupEnabled;

    /**
     * Returns whether interactive popups are currently enabled.
     *
     * @return {@code true} if popups are enabled; {@code false} otherwise
     */
    public boolean isPopupEnabled() {
        return popupEnabledProperty().get();
    }

    /**
     * The JavaFX property controlling whether popups are enabled.
     *
     * <p>Lazily initialized as a {@link SimpleBooleanProperty} named {@code "popupEnabled"}
     * with a default value of {@code true}.</p>
     *
     * @return the {@link BooleanProperty} backing the popup-enabled flag
     */
    public BooleanProperty popupEnabledProperty() {
        if (popupEnabled == null){
            popupEnabled = new SimpleBooleanProperty(this, "popupEnabled", true);
        }
        return popupEnabled;
    }

    /**
     * Enables or disables interactive popups.
     *
     * @param popupEnabled {@code true} to enable popups; {@code false} to disable them
     */
    public void setPopupEnabled(boolean popupEnabled) {
        popupEnabledProperty().set(popupEnabled);
    }

    /**
     * Creates the default skin for this control.
     * <p>
     * This method is called by the JavaFX framework to provide
     * the visual representation of the {@code NfxDoughnutChart}.
     * It returns a new instance of {@link NfxDoughnutSkin},
     * which handles layout and rendering.
     * </p>
     *
     * @return the default {@link Skin} implementation for this control
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new NfxDoughnutSkin<>(this, delegate);
    }

    /**
     * Returns the user-agent stylesheet used by this control.
     * <p>
     * This method is called by the JavaFX framework to load the default CSS
     * associated with the {@code NfxDoughnutChart}. The stylesheet is defined by
     * the {@code STYLE_SHEET} constant.
     * </p>
     *
     * @return the URL string of the user-agent stylesheet
     */
    @Override
    public String getUserAgentStylesheet() {
        return STYLE_SHEET;
    }

    /**
     * Returns the list of CSS metadata associated with this control.
     *
     * <p>This method is called by the JavaFX CSS engine to determine
     * which CSS properties are supported by the control instance.
     * The metadata is defined statically in {@link #getClassCssMetaData()}.</p>
     *
     * @return the list of {@link CssMetaData} supported by this control
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    /**
     * Returns the CSS metadata associated with the {@code NfxDoughnutChart} class.
     *
     * <p>This method exposes all styleable properties of the control at the
     * class level, making them available to the JavaFX CSS engine. The list
     * is provided by {@code Styleables.STYLEABLES}, which aggregates all
     * custom and inherited CSS properties for the control.</p>
     *
     * @return the list of {@link CssMetaData} supported by the class
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return Styleables.STYLEABLES;
    }


    /**
     * Creates the default chart-aware popup factory used when none is provided.
     *
     * <p>Returns a {@link ChartCallBack} that constructs a lightweight {@link Parent}
     * (for example, a small pane with text/graphics) for a given datum and its owning
     * {@link NfxDoughnutChart}. The implementation is intentionally generic and
     * allocation-friendly; callers may replace it via {@code setPopupFactory(...)}
     * to provide richer content.</p>
     *
     * <p><b>Behavior notes:</b></p>
     * <ul>
     *   <li>If the datum is {@code null}, the callback returns {@code null} (no popup).</li>
     *   <li>Text/content may be derived from the datum's {@code toString()} or any chart-supplied
     *       formatting utilities, if available.</li>
     *   <li>The returned node is not cached; implement caching externally if needed.</li>
     *   <li>Invoked on the JavaFX Application Thread.</li>
     * </ul>
     *
     * @param <D> the doughnut data type (value-bearing) used by the chart
     * @return a {@link ChartCallBack} that builds default popup content for a datum and chart
     */
    private static <D extends DonutData<? extends Number>> ChartCallBack<D, Parent, NfxDoughnutChart<D>> defaultPopupFactory(){
        return (data, chart) -> {
            StackPane root = new StackPane();
            Label label = new Label();
            root.getStyleClass().add("nfx-doughnut-popup");
            label.getStyleClass().add("nfx-doughnut-popup-label");
            root.setMouseTransparent(true);
            label.setMouseTransparent(true);

            double total = chart.getData().stream()
                    .map(DonutData::getValue)
                    .filter(Objects::nonNull)
                    .mapToDouble(Number::doubleValue)
                    .map(v -> Math.max(0, v))
                    .sum();
            if (total <= 0) total = 1;

            String name = data.getName() == null ? "" : data.getName();
            double val  = data.getValue() == null ? 0 : Math.max(0, data.getValue().doubleValue());
            double pct  = 100.0 * val / total;

            String valueStr = isWhole(data.getValue().doubleValue()) ?
                    String.format("%.0f", data.getValue().doubleValue()) :
                    String.format("%.2f", data.getValue().doubleValue());

            String pctStr   = isWhole(pct) ?
                    String.format("%.0f%%", pct) :
                    String.format("%.2f%%", pct);

            label.setText(String.format("%s : %s (%s)", name, valueStr, pctStr));
            root.getChildren().add(label);
            return root;
        };
    }

    /**
     * Returns whether the given value is effectively a whole number.
     *
     * <p>Rounds {@code x} to the nearest integer using {@link Math#rint(double)}
     * (ties to even) and checks if the absolute difference is below a small
     * tolerance to account for floating-point error.</p>
     *
     * @param x the value to test
     * @return {@code true} if {@code |x - rint(x)| < 1e-9}, otherwise {@code false}
     */
    private static boolean isWhole(double x) {
        return Math.abs(x - Math.rint(x)) < 1e-9;
    }

    /**
     * Helper method for loading a resource from the classpath.
     *
     * @param location the resource path relative to {@link NfxDoughnutChart}'s package
     * @return the URL pointing to the requested resource, or {@code null} if not found
     */
    @SuppressWarnings("all")
    private static URL load(String location){
        return NfxDoughnutChart.class.getResource(location);
    }


    /**
     * Holds the CSS metadata definitions for {@code NfxDoughnutChart}.
     *
     * <p>This static inner class centralizes all {@link CssMetaData}
     * objects that describe the styleable properties of the control,
     * such as colors, fonts, and legend settings. These metadata entries
     * are exposed via {@link #STYLEABLES} and used by
     * {@link #getClassCssMetaData()} to integrate with the JavaFX CSS engine.</p>
     *
     * <p>By keeping CSS metadata in a dedicated static class, the control
     * avoids repeated allocations and ensures consistent style handling
     * across all instances.</p>
     */
    private static final class Styleables{
        /**
         * CSS metadata for the legend marker square size.
         *
         * <p>Maps the CSS property {@code -nfx-legend-square-size} to the
         * {@link NfxDoughnutChart#legendSquareSizeProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Number} (pixels)</li>
         *   <li><b>Default:</b> {@code 20}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> no</li>
         *   <li><b>Animatable:</b> yes (via {@code DoubleProperty})</li>
         * </ul>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-legend-square-size: 18;
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>,Number> LEGEND_SQUARE_SIZE =
                new CssMetaData<>("-nfx-legend-square-size", StyleConverter.getSizeConverter(), 20d) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.legendSquareSize == null || !s.legendSquareSize.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Number>) s.legendSquareSizeProperty();
                    }
                };

        /**
         * CSS metadata for the legend gap property.
         *
         * <p>Maps the CSS property {@code -nfx-legend-gap} to the
         * {@link NfxDoughnutChart#legendGapProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Number} (pixels)</li>
         *   <li><b>Default:</b> {@code 8}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> no</li>
         *   <li><b>Animatable:</b> yes (via {@code DoubleProperty})</li>
         * </ul>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-legend-gap: 12;
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>,Number> LEGEND_GAP =
                new CssMetaData<>("-nfx-legend-gap", StyleConverter.getSizeConverter(), 8d) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.legendGap == null || !s.legendGap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Number>) s.legendGapProperty();
                    }
                };

        /**
         * CSS metadata for the legend item gap property.
         *
         * <p>Maps the CSS property {@code -nfx-legend-item-gap} to the
         * {@link NfxDoughnutChart#legendItemGapProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Number} (pixels)</li>
         *   <li><b>Default:</b> {@code 14}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> no</li>
         *   <li><b>Animatable:</b> yes (via {@code DoubleProperty})</li>
         * </ul>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-legend-item-gap: 10;
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>,Number> LEGEND_ITEM_GAP =
                new CssMetaData<>("-nfx-legend-item-gap", StyleConverter.getSizeConverter(), 14d) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.legendItemGap == null || !s.legendItemGap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Number>) s.legendItemGapProperty();
                    }
                };

        /**
         * CSS metadata for the legend line gap property.
         *
         * <p>Maps the CSS property {@code -nfx-legend-line-gap} to the
         * {@link NfxDoughnutChart#legendLineGapProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Number} (pixels)</li>
         *   <li><b>Default:</b> {@code 20}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> no</li>
         *   <li><b>Animatable:</b> yes (via {@code DoubleProperty})</li>
         * </ul>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-legend-line-gap: 24;
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>,Number> LEGEND_LINE_GAP =
                new CssMetaData<>("-nfx-legend-line-gap", StyleConverter.getSizeConverter(), 20d) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.legendLineGap == null || !s.legendLineGap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Number>) s.legendLineGapProperty();
                    }
                };

        /**
         * CSS metadata for the inner radius property of the doughnut chart.
         *
         * <p>Maps the CSS property {@code -nfx-inner-radius} to the
         * {@link NfxDoughnutChart#innerRadiusProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Number} (fraction of chart radius)</li>
         *   <li><b>Default:</b> {@code 0.5}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> no</li>
         *   <li><b>Animatable:</b> yes (via {@code DoubleProperty})</li>
         * </ul>
         *
         * <p>The value is interpreted as a fraction of the outer radius:
         * <ul>
         *   <li>{@code 0.0} → pie chart (no hole)</li>
         *   <li>{@code 0.5} → standard doughnut</li>
         *   <li>{@code 1.0} → fully hollow (invisible)</li>
         * </ul>
         * </p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-inner-radius: 0.4;
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>,Number> INNER_RADIUS =
                new CssMetaData<>("-nfx-inner-radius", StyleConverter.getSizeConverter(), 50d) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.innerRadius == null || !s.innerRadius.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Number>) s.innerRadiusProperty();
                    }
                };


        /**
         * CSS metadata for the title font family property.
         *
         * <p>Maps the CSS property {@code -nfx-title-font} to the
         * {@link NfxDoughnutChart#titleFontProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link String} (font family name)</li>
         *   <li><b>Default:</b> {@code "Roboto"}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>If {@code null} or unspecified, the system default font family is used.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-title-font: "Arial";
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final FontCssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>> TITLE_FONT =
                new FontCssMetaData<>("-nfx-title-font", Font.getDefault()) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.titleFont == null || !s.titleFont.isBound();
                    }

                    @Override
                    public StyleableProperty<Font> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Font>) s.titleFontProperty();
                    }
                };

        /**
         * CSS metadata for the title fill property.
         *
         * <p>Maps the CSS property {@code -nfx-title-fill} to the
         * {@link NfxDoughnutChart#titleFillProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Paint} (color or gradient)</li>
         *   <li><b>Default:</b> {@code Color.web("#444")}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>This paint defines the color or gradient used to render the chart title.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-title-fill: #222;
         *     -nfx-title-fill: linear-gradient(to right, #ff7f50, #1e90ff);
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final  CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>, Paint> TITLE_FILL =
                new CssMetaData<>("-nfx-title-fill", StyleConverter.getPaintConverter(), Color.web("#444")) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.titleFill == null || !s.titleFill.isBound();
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Paint>) s.titleFillProperty();
                    }
                };


        /**
         * CSS metadata for the subtitle font family property.
         *
         * <p>Maps the CSS property {@code -nfx-subtitle-font} to the
         * {@link NfxDoughnutChart#subtitleFontProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link String} (font family name)</li>
         *   <li><b>Default:</b> {@code null} (system default is used)</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>If this value is {@code null}, the subtitle text will use the system’s default font family.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-subtitle-font: "Arial";
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final FontCssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>> SUBTITLE_FONT =
                new FontCssMetaData<>("-nfx-subtitle-font", Font.getDefault()) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.subtitleFont == null || !s.subtitleFont.isBound();
                    }

                    @Override
                    public StyleableProperty<Font> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Font>) s.subtitleFontProperty();
                    }
                };



        /**
         * CSS metadata for the subtitle fill property.
         *
         * <p>Maps the CSS property {@code -nfx-subtitle-fill} to the
         * {@link NfxDoughnutChart#subtitleFillProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Paint} (color or gradient)</li>
         *   <li><b>Default:</b> {@code Color.web("#777")}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>This paint defines the color or gradient used to render the subtitle text.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-subtitle-fill: #555;
         *     -nfx-subtitle-fill: linear-gradient(to right, #ff7f50, #1e90ff);
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>, Paint> SUBTITLE_FILL =
                new CssMetaData<>("-nfx-subtitle-fill", StyleConverter.getPaintConverter(), Color.web("#777")) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.subtitleFill == null || !s.subtitleFill.isBound();
                    }
                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Paint>) s.subtitleFillProperty();
                    }
                };

        /**
         * CSS metadata for the legend marker corner radius property.
         *
         * <p>Maps the CSS property {@code -nfx-legend-marker-corner-radius} to the
         * {@link NfxDoughnutChart#legendMarkerCornerRadiusProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Number} (pixels)</li>
         *   <li><b>Default:</b> {@code 4}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> no</li>
         *   <li><b>Animatable:</b> yes (via {@code DoubleProperty})</li>
         * </ul>
         *
         * <p>This value only applies when the legend marker shape is
         * {@code ROUNDED}. Values less than {@code 0} are clamped to {@code 0}.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-legend-marker-corner-radius: 6;
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>,Number> LEGEND_MARKER_CORNER_RADIUS =
                new CssMetaData<>("-nfx-legend-marker-corner-radius", StyleConverter.getSizeConverter(), 4d) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.legendMarkerCornerRadius == null || !s.legendMarkerCornerRadius.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Number>) s.legendMarkerCornerRadiusProperty();
                    }
                };

        /**
         * CSS metadata for the legend label font family property.
         *
         * <p>Maps the CSS property {@code -nfx-legend-label-font} to the
         * {@link NfxDoughnutChart#legendLabelFontProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link String} (font family name)</li>
         *   <li><b>Default:</b> {@code null} (system default font family)</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>If {@code null}, legend labels fall back to the system’s default font family.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-legend-label-font: 24 "Segoe UI";
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final FontCssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>> LEGEND_LABEL_FONT
                = new FontCssMetaData<>("-nfx-legend-label-font", Font.getDefault()) {
            @Override
            public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                return s.legendLabelFont == null || !s.legendLabelFont.isBound();
            }

            @Override
            public StyleableProperty<Font> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                return (StyleableProperty<Font>) s.legendLabelFontProperty();
            }
        };

        /**
         * CSS metadata for the legend label fill property.
         *
         * <p>Maps the CSS property {@code -nfx-legend-label-fill} to the
         * {@link NfxDoughnutChart#legendLabelFillProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Paint} (color or gradient)</li>
         *   <li><b>Default:</b> {@code Color.web("#333")}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>Defines the paint used for legend label text, which may be a
         * solid color or a gradient.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx--legend-label-fill: #000;
         *     -nfx--legend-label-fill: linear-gradient(to bottom, #444, #999);
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>, Paint> LEGEND_LABEL_FILL =
                new CssMetaData<>("-nfx-legend-label-fill", StyleConverter.getPaintConverter(), Color.web("#333")) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.legendLabelFill == null || !s.legendLabelFill.isBound();
                    }
                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Paint>) s.legendLabelFillProperty();
                    }
                };

        /**
         * CSS metadata for the chart background paint.
         *
         * <p>Maps the CSS property {@code -nfx-chart-background} to the
         * {@link NfxDoughnutChart#chartBackgroundProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Paint} (color or gradient)</li>
         *   <li><b>Default:</b> {@code null} (canvas remains transparent)</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>When set, the canvas background is filled each frame with the specified paint.
         * Omit this property to keep the canvas transparent.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-chart-background: #f8f9fa;           *
         *     -nfx-chart-background: linear-gradient(to bottom, #ffffff, #e6e6e6);
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>, Paint> CHART_BACKGROUND =
                new CssMetaData<>("-nfx-chart-background", StyleConverter.getPaintConverter(), null) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.chartBackground == null || !s.chartBackground.isBound();
                    }
                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Paint>) s.chartBackgroundProperty();
                    }
                };


        /**
         * CSS metadata for the border gap around the chart content.
         *
         * <p>Maps the CSS property {@code -nfx-border-gap} to the
         * {@link NfxDoughnutChart#borderGapProperty()} of the control.</p>
         *
         * <ul>
         *   <li><b>Type:</b> {@link Number} (typically a {@code double}, pixels)</li>
         *   <li><b>Default:</b> {@code 20.0}</li>
         *   <li><b>Applies to:</b> {@link NfxDoughnutChart}</li>
         *   <li><b>Inherits:</b> yes</li>
         *   <li><b>Animatable:</b> no</li>
         * </ul>
         *
         * <p>Controls the pixel gap between the chart’s rendered content and its outer border.
         * Increasing this value adds padding around the doughnut visualization.</p>
         *
         * <p>Example CSS:</p>
         * <pre>
         * .doughnut-chart {
         *     -nfx-border-gap: 24;
         * }
         * </pre>
         */
        @SuppressWarnings("unchecked")
        private static final CssMetaData<NfxDoughnutChart<? extends DonutData<? extends Number>>, Number> BORDER_GAP =
                new CssMetaData<>("-nfx-border-gap", StyleConverter.getSizeConverter(), 20d) {
                    @Override
                    public boolean isSettable(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return s.borderGap == null || !s.borderGap.isBound();
                    }
                    @Override
                    public StyleableProperty<Number> getStyleableProperty(NfxDoughnutChart<? extends DonutData<? extends Number>> s) {
                        return (StyleableProperty<Number>) s.borderGapProperty();
                    }
                };


        /**
         * The complete list of {@link CssMetaData} supported by {@code NfxDoughnutChart}.
         *
         * <p>This list includes all styleable properties defined by this control
         * (legend sizing, gaps, inner radius, title/subtitle styling, legend labels, etc.),
         * combined with the default styleables inherited from {@link Control}.</p>
         *
         * <p>The list is built once in a static initializer and exposed as an
         * unmodifiable list, ensuring consistent CSS integration across all
         * instances of {@code NfxDoughnutChart}.</p>
         *
         * <p>Developers typically access this list via
         * {@link NfxDoughnutChart#getClassCssMetaData()} or
         * {@link NfxDoughnutChart#getControlCssMetaData()} for integration with
         * the JavaFX CSS engine.</p>
         */
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> cssMetaData =
                    new ArrayList<>(Control.getClassCssMetaData());
            cssMetaData.add(LEGEND_SQUARE_SIZE);
            cssMetaData.add(LEGEND_GAP);
            cssMetaData.add(LEGEND_ITEM_GAP);
            cssMetaData.add(LEGEND_LINE_GAP);
            cssMetaData.add(INNER_RADIUS);
            cssMetaData.add(TITLE_FONT);
            cssMetaData.add(TITLE_FILL);
            cssMetaData.add(SUBTITLE_FONT);
            cssMetaData.add(SUBTITLE_FILL);
            cssMetaData.add(LEGEND_MARKER_CORNER_RADIUS);
            cssMetaData.add(LEGEND_LABEL_FONT);
            cssMetaData.add(LEGEND_LABEL_FILL);
            cssMetaData.add(CHART_BACKGROUND);
            cssMetaData.add(BORDER_GAP);
            STYLEABLES = Collections.unmodifiableList(cssMetaData);
        }
    }


    /**
     * Shapes available for legend markers (swatches) rendered next to legend labels.
     *
     * <p>The selected shape influences only the marker geometry; size, stroke, and fill
     * are determined elsewhere (e.g., chart/series style).</p>
     */
    public enum LegendMarkerShape {

        /** Circular swatch; diameter equals the configured marker size. */
        CIRCLE,

        /** Rounded-rectangle swatch; corner radius is style-dependent (e.g., a fraction of size). */
        ROUNDED,

        /** Square swatch with sharp corners; side length equals the marker size. */
        SQUARE
    }

    /**
     * Ordering policies for legend items.
     *
     * <p>Controls how legend entries are arranged relative to their source data.</p>
     */
    public enum LegendOrder {

        /** Preserve the incoming data order (no re-sorting). */
        AS_IS,

        /** Sort alphabetically by the item's display name (A → Z). */
        BY_NAME_ASC,

        /** Sort by the item's numeric value, ascending (small → large). */
        BY_VALUE_ASC,

        /** Sort by the item's numeric value, descending (large → small). */
        BY_VALUE_DESC
    }

    /**
     * Positions for rendering the legend relative to the chart content.
     *
     * <p>These side placements affect layout and available plotting area.
     * (The control's property typically defaults to {@code RIGHT}.)</p>
     */
    public enum LegendPos {

        /** Place the legend to the left of the chart area. */
        LEFT,

        /** Place the legend to the right of the chart area. */
        RIGHT
    }
}
