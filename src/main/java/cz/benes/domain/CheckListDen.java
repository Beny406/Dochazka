/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author PB
 */

// beana pro zadání nemocenské, dovolené, paragrafu
public class CheckListDen {

    private final BooleanProperty on = new SimpleBooleanProperty();
    private LocalDate localDate;

    public CheckListDen(LocalDate localDate, boolean on) {
        this.localDate = localDate;
        setOn(on);
    }

    public final BooleanProperty onProperty() {
        return this.on;
    }

    public final void setOn(final boolean on) {
        this.onProperty().set(on);
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }


    @Override
    public String toString() {
        return localDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd EE"));
    }


}
