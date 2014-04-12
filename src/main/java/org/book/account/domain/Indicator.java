package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;

public abstract class Indicator<TIndicatorValue> {
    private final String name;

    public Indicator(String name) {
        Validate.notNull(name, "The name must not be null");
        this.name = name;
    }

    public abstract TIndicatorValue indicatorValueAt(Date aDate);

    public abstract TIndicatorValue expectedValueAt(Date aDate);

    public String getName() {
        return name;
    }
}
