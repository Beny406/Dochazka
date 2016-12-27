package cz.benes.services;

import cz.benes.beanfactory.DaysFactory;
import cz.benes.database.dao.HolidaysDAO;
import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.Employee;
import cz.benes.database.domain.RecordType;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class JasperServiceImpl extends AbstractService implements JasperService {

    Employee employee = getInstance(Employee.class);

    @Override
    public void getReport(List<AttendanceRecord> zaznamyZaMesic, LocalDate datum, Class clazz) throws JRException, SQLException {
        HolidaysDAO holidaysDAO = getInstance(HolidaysDAO.class);

        Duration odpracovano = Duration.ZERO;
        Duration odpracovanoVikend = Duration.ZERO;
        Duration odpracovanoSvatek = Duration.ZERO;
        int dnuSvatku, pracDnu, dnuDovolene, dnuNemocenske, dnuParagraf;
        dnuSvatku = pracDnu = dnuDovolene = dnuNemocenske = dnuParagraf = 0;
        
        // čerstvá data o přichodu a odchodu při otvírání přehledu
        
        // vstupní data
        LocalTime prichod = null;
         
        for (AttendanceRecord zaznam : zaznamyZaMesic){
            switch (RecordType.valueOf(zaznam.getType())) {
                case IN:
                    prichod = LocalTime.parse(zaznam.getTime());
                    break;
                case OUT:
                    LocalTime odchod = LocalTime.parse(zaznam.getTime());
                    if (isRecordWeekend(zaznam)) odpracovanoVikend = odpracovano.plus(Duration.between(prichod, odchod));
                    else odpracovano = odpracovano.plus(Duration.between(prichod, odchod));
                    break;
                case DOV:
                    dnuDovolene++;
                    break;
                case NEM:
                    dnuNemocenske++;
                    break;
                case PAR:
                    dnuParagraf++;
                    break;       
            }
        }
                
        // odpracované hodiny o svátku    
        for (LocalDate svatek : holidaysDAO.getAll()){
            LocalDate svatekTentoRok = LocalDate.of(datum.getYear(), svatek.getMonthValue(), svatek.getDayOfMonth());
            if (svatekTentoRok.getMonthValue() == datum.getMonthValue()
                    && svatekTentoRok.getDayOfWeek() != DayOfWeek.SATURDAY 
                    && svatekTentoRok.getDayOfWeek() != DayOfWeek.SUNDAY){
                dnuSvatku++;
            }            
           
            for (AttendanceRecord zaznam : zaznamyZaMesic){
                if (svatekTentoRok.equals(LocalDate.parse(zaznam.getDate()))){
                    switch (RecordType.valueOf(zaznam.getType())) {
                        case IN:
                            prichod = LocalTime.parse(zaznam.getTime());
                            break;
                        case OUT:
                            LocalTime odchod = LocalTime.parse(zaznam.getTime());
                            odpracovanoSvatek = odpracovano.plus(Duration.between(prichod, odchod));
                            break;
                    }
                }
            }
        }
                
        //hodinový fond podle úvazku - svátky
        int pocetDniMesice = datum.getMonth().length(LocalDate.now().isLeapYear());
        for (int i = 1; i <= pocetDniMesice ; i++){
            DayOfWeek denMesice = datum.withDayOfMonth(i).getDayOfWeek();
            if (isWorkingDay(denMesice)){
                pracDnu++;
            }
        }
        pracDnu -= dnuSvatku;
        
        
        //passing informations through Beans   
        Map<String, Object> params = new HashMap<>();
        params.put("login_id", employee.getLogin_id());
        params.put("jmeno", employee.getJmeno());
        params.put("mesic", datum.format(DateTimeFormatter.ofPattern("LLLL")).toUpperCase());
        params.put("rok", String.valueOf(datum.getYear()));
        params.put("aktualniDatum", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.M.yyyy")));
        params.put("pracDnu", pracDnu);
        params.put("hodFond", ((double)pracDnu*employee.getUvazek()));
        params.put("odpracovano", ((double)(odpracovano.toHours())));
        params.put("dnuSvatku", dnuSvatku);
        params.put("odpracovanoSvatek", ((double)(odpracovanoSvatek.toHours())));
        params.put("odpracovanoVikend", ((double)(odpracovanoVikend.toHours())));
        params.put("uvazek", employee.getUvazek());
        params.put("dnuDovolene", dnuDovolene);
        params.put("dnuNemocenske", dnuNemocenske);
        params.put("dnuParagraf", dnuParagraf);
        // změna čárek na tečku při zaokrouhlování parametrů
        params.put(JRParameter.REPORT_LOCALE, new Locale("en", "US"));
        
        JasperDesign jasperDesign = JRXmlLoader.load(clazz.getResourceAsStream("/reports/prehled.jrxml")); 
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint  jasperPrint  = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(DaysFactory.generateDays(zaznamyZaMesic, datum)));
        JasperViewer.viewReport(jasperPrint, false);
    }

    private static boolean isWorkingDay(DayOfWeek denMesice) {
        return denMesice != DayOfWeek.SATURDAY && denMesice != DayOfWeek.SUNDAY;
    }

    private static boolean isRecordWeekend(AttendanceRecord zaznam) {
        return (LocalDate.parse(zaznam.getDate())).getDayOfWeek() == DayOfWeek.SATURDAY
                || (LocalDate.parse(zaznam.getDate())).getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
