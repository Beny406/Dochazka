package cz.benes.beanfactory;

import cz.benes.database.domain.JasperRow;
import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.RecordType;
import cz.benes.database.dao.AttendanceDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// factory of lines for days off
public class HolidaysFactory {
    public static Collection generateDays() throws SQLException{
        List seznamDnu = new ArrayList();
        List<AttendanceRecord> dovolenaTentoMesic = AttendanceDAO.getThisMonthWithCondition(RecordType.DOV);
        for (int i = 0; i<LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()) ;i++){
            String poznamka = "";
            LocalDate prvniDenMesice = LocalDate.now().withDayOfMonth(1);
            LocalDate denMesice = prvniDenMesice.plusDays(i);
            for (AttendanceRecord radek : dovolenaTentoMesic){
                if (LocalDate.parse(radek.getDate()).equals(denMesice) ){
                    poznamka = radek.getType();
                }
            }
        seznamDnu.add(new JasperRow(denMesice.format(DateTimeFormatter.ofPattern("dd EE")), poznamka));
        }
        return seznamDnu;
    }
}
