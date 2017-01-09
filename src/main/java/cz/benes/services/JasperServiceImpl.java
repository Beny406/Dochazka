package cz.benes.services;

import com.google.inject.Inject;
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

    @Inject
    Employee employee;

    @Inject
    HolidaysDAO holidaysDAO;

    @Override
    public void getReport(List<AttendanceRecord> monthRecords, LocalDate localDate, Class clazz) throws JRException, SQLException {

        Duration workedDuration = Duration.ZERO;
        Duration workedOnWeekendDuration = Duration.ZERO;
        Duration workedOnHolidayDuration = Duration.ZERO;
        int holidaysCount, workingDays, daysoffCount, sickdaysCount, paragraphdaysCount;
        holidaysCount = workingDays = daysoffCount = sickdaysCount = paragraphdaysCount = 0;
        
        // čerstvá data o přichodu a odchodu při otvírání přehledu
        
        // vstupní data
        LocalTime arrival = null;
         
        for (AttendanceRecord record : monthRecords){
            switch (RecordType.valueOf(record.getType())) {
                case IN:
                    arrival = LocalTime.parse(record.getTime());
                    break;
                case OUT:
                    LocalTime departure = LocalTime.parse(record.getTime());
                    if (isRecordWeekend(record)) workedOnWeekendDuration = workedDuration.plus(Duration.between(arrival, departure));
                    else workedDuration = workedDuration.plus(Duration.between(arrival, departure));
                    break;
                case DOV:
                    daysoffCount++;
                    break;
                case NEM:
                    sickdaysCount++;
                    break;
                case PAR:
                    paragraphdaysCount++;
                    break;       
            }
        }
                
        // odpracované hodiny o svátku    
        for (LocalDate holiday : holidaysDAO.getAll()){
            LocalDate holidayThisYear = holiday.withYear(localDate.getYear());

            if (holidayThisYear.getMonthValue() == localDate.getMonthValue() && isWorkingDay(holidayThisYear.getDayOfWeek())){
                holidaysCount++;
            }            
           
            for (AttendanceRecord record : monthRecords){
                if (holidayThisYear.equals(LocalDate.parse(record.getDate()))){
                    switch (RecordType.valueOf(record.getType())) {
                        case IN:
                            arrival = LocalTime.parse(record.getTime());
                            break;
                        case OUT:
                            LocalTime odchod = LocalTime.parse(record.getTime());
                            workedOnHolidayDuration = workedDuration.plus(Duration.between(arrival, odchod));
                            break;
                    }
                }
            }
        }
                
        //hodinový fond podle úvazku - svátky
        int totalDaysThisMonth = localDate.getMonth().length(LocalDate.now().isLeapYear());
        for (int i = 1; i <= totalDaysThisMonth ; i++){
            DayOfWeek dayOfWeek = localDate.withDayOfMonth(i).getDayOfWeek();
            if (isWorkingDay(dayOfWeek)){
                workingDays++;
            }
        }
        workingDays -= holidaysCount;
        
        
        //passing informations through Beans   
        Map<String, Object> params = new HashMap<>();
        params.put("login_id", employee.getLogin_id());
        params.put("jmeno", employee.getJmeno());
        params.put("mesic", localDate.format(DateTimeFormatter.ofPattern("LLLL")).toUpperCase());
        params.put("rok", String.valueOf(localDate.getYear()));
        params.put("aktualniDatum", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.M.yyyy")));
        params.put("pracDnu", workingDays);
        params.put("hodFond", ((double)workingDays*employee.getUvazek()));
        params.put("odpracovano", ((double)(workedDuration.toHours())));
        params.put("dnuSvatku", holidaysCount);
        params.put("odpracovanoSvatek", ((double)(workedOnHolidayDuration.toHours())));
        params.put("odpracovanoVikend", ((double)(workedOnWeekendDuration.toHours())));
        params.put("uvazek", employee.getUvazek());
        params.put("dnuDovolene", daysoffCount);
        params.put("dnuNemocenske", sickdaysCount);
        params.put("dnuParagraf", paragraphdaysCount);
        // změna čárek na tečku při zaokrouhlování parametrů
        params.put(JRParameter.REPORT_LOCALE, new Locale("en", "US"));
        
        JasperDesign jasperDesign = JRXmlLoader.load(clazz.getResourceAsStream("/reports/prehled.jrxml")); 
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint  jasperPrint  = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(DaysFactory.generateDays(monthRecords, localDate)));
        JasperViewer.viewReport(jasperPrint, false);
    }


    private boolean isRecordWeekend(AttendanceRecord zaznam) {
        return !isWorkingDay(LocalDate.parse(zaznam.getDate()).getDayOfWeek());
    }

    private boolean isWorkingDay(DayOfWeek denMesice) {
        return denMesice != DayOfWeek.SATURDAY && denMesice != DayOfWeek.SUNDAY;
    }
}
