package cz.benes.beanfactory;

import cz.benes.beans.JasperRadek;
import cz.benes.beans.DAODochazka;
import cz.benes.beans.InOut_enum;
import cz.benes.managers.db.Dochazka;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// factory of lines for days off
public class DovolenaDaysFactory {
    public static Collection generateDays() throws SQLException{
        List seznamDnu = new ArrayList();
        List<DAODochazka> dovolenaTentoMesic = Dochazka.getThisMonthWithCondition(InOut_enum.DOV);
        for (int i = 0; i<LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()) ;i++){
            String poznamka = "";
            LocalDate prvniDenMesice = LocalDate.now().withDayOfMonth(1);
            LocalDate denMesice = prvniDenMesice.plusDays(i);
            for (DAODochazka radek : dovolenaTentoMesic){
                if (LocalDate.parse(radek.getDate()).equals(denMesice) ){
                    poznamka = radek.getIn_out();
                }
            }
        seznamDnu.add(new JasperRadek(denMesice.format(DateTimeFormatter.ofPattern("dd EE")), poznamka));
        }
        return seznamDnu;
    }
}
