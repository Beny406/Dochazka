/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.database.domain;

import cz.benes.database.dao.HolidaysDAO;
import cz.benes.database.dao.HolidaysDAOImpl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author PB
 */

// beana pro zadání nemocenské, dovolené, paragrafu
public class CheckableDay {

    private final BooleanProperty on = new SimpleBooleanProperty();

    private HolidaysDAO holidaysDAO = HolidaysDAOImpl.getInstance();

    private LocalDate localDate;

    public CheckableDay(LocalDate localDate, boolean on) {
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

    public boolean in(List<LocalDate> collection){
        return collection.contains(getLocalDate());
    }

    public boolean isWeekendOrHoliday() {
        return this.getLocalDate().getDayOfWeek() == DayOfWeek.SATURDAY
                || this.getLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY
                || holidaysDAO.getAll().stream().anyMatch(svatek -> svatek.equals(this.getLocalDate()));
    }
    @Override
    public String toString() {
        return localDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd EE"));
    }


}
