package cz.benes.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class CheckListMesic {
    
    private LocalDate localDate;
    
    private final BooleanProperty on = new SimpleBooleanProperty();
    
    public final BooleanProperty onProperty() {
        return this.on;
    }
    
    public CheckListMesic(LocalDate localDate, boolean on) {
        this.localDate = localDate;
        setOn(on);
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

    public CheckListMesic() {
    }
    
    @Override
    public String toString() {
        return localDate.format(DateTimeFormatter.ofPattern("MMM"));
    }
    
}
