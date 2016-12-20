package cz.benes.beanfactory;

import cz.benes.domain.AttendanceRecord;
import cz.benes.domain.RecordType;
import cz.benes.domain.JasperRow;
import cz.benes.managers.db.HolidaysDAO;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// factory of lines for overview last month
public class DaysFactory {
    
    public static Collection generateDays(List<AttendanceRecord> vysledky, LocalDate datum) throws SQLException{
        List seznamDnu = new ArrayList();
        LocalTime prichod = LocalTime.MIN;            

        for (int i = 0; i<datum.getMonth().length(datum.isLeapYear()) ;i++){
            String prichodColumn = "";
            String odchodColumn = "";
            String odpracovanoColumn = "";
            String pauza = "";
            String poznamka = "";
            Duration odpracovano = Duration.ZERO;
            LocalDate prvniDenMesice = datum.withDayOfMonth(1);
            LocalDate denMesice = prvniDenMesice.plusDays(i);
            
            for (AttendanceRecord zaznam : vysledky){
                String datumZaznamu = zaznam.getDate();
                String akceZaznamu = zaznam.getIn_out();
                String casZaznamu = zaznam.getTime();

                if (LocalDate.parse(datumZaznamu).equals(denMesice) ){
                    switch (akceZaznamu){
                        case RecordType.IN:
                            prichod = LocalTime.parse(casZaznamu);
                            if (prichodColumn.equals("")){
                                prichodColumn = casZaznamu;
                            }
                            break;
                        case RecordType.OUT:
                            LocalTime odchod = LocalTime.parse(casZaznamu);
                            odchodColumn = casZaznamu;
                            odpracovano = odpracovano.plus(Duration.between(prichod, odchod));
                            odpracovanoColumn = odpracovano.toHours() + " h. " + odpracovano.toMinutes() % 60 + " m.";
                            pauza = Duration.between(LocalTime.parse(prichodColumn), LocalTime.parse(odchodColumn)).minus(odpracovano).toMinutes() + " minut";
                            break;
                        default:
                            poznamka = akceZaznamu;
                    }
                }
            }

            for (LocalDate svatek : HolidaysDAO.ALL){
                if (svatek.getMonthValue() == denMesice.getMonthValue() && svatek.getDayOfMonth() == denMesice.getDayOfMonth()){
                    poznamka = "SVA";
                }
            }
            seznamDnu.add(new JasperRow(denMesice.format(DateTimeFormatter.ofPattern("dd EE")), prichodColumn, odchodColumn, pauza, odpracovanoColumn, poznamka));
        }
        return seznamDnu;
        
    }     
}
