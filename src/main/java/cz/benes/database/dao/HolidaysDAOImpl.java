package cz.benes.database.dao;

import org.sql2o.Connection;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HolidaysDAOImpl extends AbstractDAO implements HolidaysDAO {

    public static HolidaysDAO getInstance(){
        return injector.getInstance(HolidaysDAOImpl.class);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public List<LocalDate> getAll() {
        try (Connection conn = sql2o.open()) {
            List<String> stringDates = conn.createQuery("SELECT * FROM svatky").executeScalarList(String.class);
            return stringDates.stream().map(LocalDate::parse).collect(Collectors.toList());
        }
    }
        
    @Override
    public boolean insert(LocalDate date) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("INSERT INTO svatky (datum) VALUES (:date)")
                    .addParameter("date", date.toString())
                    .executeUpdate().getResult() == 1;
        }
    }
    
    @Override
    public boolean delete(LocalDate svatek) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("DELETE FROM svatky WHERE datum= :datum")
                    .addParameter("datum", svatek.toString())
                    .executeUpdate().getResult() == 1;
        }
    }    
    
    @Override
    public int getPocetSvatkuVmesici(LocalDate datum) {
        int dnuSvatku = 0;    
        for (LocalDate svatek : getAll()){
            LocalDate svatekTentoRok = LocalDate.of(datum.getYear(), svatek.getMonthValue(), svatek.getDayOfMonth());
            if (svatekTentoRok.getMonthValue() == datum.getMonthValue()
                    && svatekTentoRok.getDayOfWeek() != DayOfWeek.SATURDAY
                    && svatekTentoRok.getDayOfWeek() != DayOfWeek.SUNDAY){
                dnuSvatku++;
            }
        }
        return dnuSvatku;
    }

}
