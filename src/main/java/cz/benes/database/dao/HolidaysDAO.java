package cz.benes.database.dao;

import cz.benes.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidaysDAO {
    
    static {
        spojeni = DBConnection.getConnection();
    }
    private static final Connection spojeni;
    
    public static final List<LocalDate> ALL = getAll();
    
    public static List<LocalDate> getAll() {
        List<LocalDate> svatky = new ArrayList();
        try {
            PreparedStatement dotazSvatky = spojeni.prepareStatement("SELECT * FROM svatky");
            ResultSet rs =  dotazSvatky.executeQuery();
            while (rs.next()){
                svatky.add(LocalDate.parse(rs.getString("datum")));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }  
        return svatky;
    }
        
    public static int insert(LocalDate date) {
        int pridano = 0;
        try {
            PreparedStatement dotazPridani = spojeni.prepareStatement("INSERT INTO svatky (datum) VALUES (?)");
            dotazPridani.setString(1, date.toString());
            pridano = dotazPridani.executeUpdate();      
        } catch (Exception e){
            System.err.println(e);
        }
        return pridano;
    }
    
    public static int delete(LocalDate svatek) {
        int smazano = 0;
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("DELETE FROM svatky WHERE datum=?");
            dotaz.setString(1, svatek.toString());
            smazano = dotaz.executeUpdate();
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return smazano;
    }    
    
    public static int getPocetSvatkuVmesici(LocalDate datum) {
        int dnuSvatku = 0;    
        for (LocalDate svatek : HolidaysDAO.ALL){
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
