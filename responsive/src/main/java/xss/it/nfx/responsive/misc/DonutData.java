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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
 * @since September 08, 2025
 * <p>
 * Created on 09/08/2025 at 15:25
 * <p>
 * Data model class representing a single entry in a {@code NfxDoughnutChart} chart.
 * <p>
 * Each {@code DonutData} instance stores a {@code name} (label) and a {@code value}
 * of type {@link Number}, both implemented as observable JavaFX properties so they
 * can participate in bindings and trigger UI updates when changed.
 * </p>
 *
 * @param <T> the numeric type of the data value (e.g., {@code Integer}, {@code Double})
 */
public class DonutData<T extends Number> {
    /**
     * Creates a new {@code DonutData} entry with the given name and value.
     *
     * @param name  the label for this data entry
     * @param value the numeric value associated with this entry
     */
    public DonutData(String name, T value){
        setName(name);
        setValue(value);
    }

    /** Name property (label for the data entry). */
    private StringProperty name;

    /**
     * Returns the current name (label) of this data entry.
     *
     * @return the name string
     */
    public final String getName() {
        return nameProperty().get();
    }

    /**
     * Provides access to the name property.
     * <p>
     * If the property has not been initialized, it will be lazily created with
     * an empty string as the default.
     * </p>
     *
     * @return the string property representing the data entry name
     */
    public final StringProperty nameProperty() {
        if (name == null){
            name = new SimpleStringProperty(this, "name", "");
        }
        return name;
    }

    /**
     * Sets the name (label) of this data entry.
     *
     * @param name the new name string
     */
    public final void setName(String name) {
        nameProperty().set(name);
    }

    /** Value property (numeric value of the data entry). */
    private ObjectProperty<T> value;

    /**
     * Returns the current numeric value of this data entry.
     *
     * @return the numeric value
     */
    public final T getValue() {
        return valueProperty().get();
    }

    /**
     * Provides access to the value property.
     * <p>
     * If the property has not been initialized, it will be lazily created with
     * {@code null} as the default.
     * </p>
     *
     * @return the object property representing the data value
     */
    public final ObjectProperty<T> valueProperty() {
        if (value == null){
            value = new SimpleObjectProperty<>(this, "value");
        }
        return value;
    }

    /**
     * Sets the numeric value of this data entry.
     *
     * @param value the new numeric value
     */
    public final void setValue(T value) {
        valueProperty().set(value);
    }

    /**
     * Returns a string representation of this data entry in the format:
     * {@code "name: value"}.
     *
     * @return a string containing the name and value
     */
    @Override
    public String toString() {
        return getName() + ": " + getValue();
    }
}
