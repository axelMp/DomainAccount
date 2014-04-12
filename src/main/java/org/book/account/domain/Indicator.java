package org.book.account.domain;

import java.util.Date;

public abstract class Indicator<TIndicatorValue> {
    public Indicator(String name) {
        if ( null == name ) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
    }

    public abstract TIndicatorValue indicatorValueAt(Date aDate);
    public abstract TIndicatorValue expectedValueAt(Date aDate);

    public String getName() {
        return name;
    }

    private final String name;
}
