package cz.benes.database.dao;

import java.time.LocalDate;
import java.util.List;

public interface HolidaysDAO {

    List<LocalDate> getAll();

    boolean insert(LocalDate date);

    boolean delete(LocalDate svatek);

    int getPocetSvatkuVmesici(LocalDate datum);
}
