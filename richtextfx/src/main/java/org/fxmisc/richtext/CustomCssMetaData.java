package org.fxmisc.richtext;

import javafx.css.*;

import java.util.function.Function;

/**
 * Reduces boilerplate when creating a custom {@link CssMetaData} object
 */
public class CustomCssMetaData<S extends Styleable, V> extends CssMetaData<S, V> {

    private final Function<S, StyleableObjectProperty<V>> property;

    CustomCssMetaData(String property, StyleConverter<?, V> converter, V initialValue,
                      Function<S, StyleableObjectProperty<V>> getStyleableProperty) {
        super(property, converter, initialValue);
        this.property = getStyleableProperty;
    }

    @Override
    public boolean isSettable(S styleable) {
        return !property.apply(styleable).isBound();
    }

    @Override
    public StyleableProperty<V> getStyleableProperty(S styleable) {
        return property.apply(styleable);
    }


}
