package cz.benes.managers;

import cz.benes.managers.db.Svatky;
import cz.benes.beanfactory.DaysFactory;
import cz.benes.beans.DAODochazka;
import cz.benes.beans.InOut_enum;
import static cz.benes.controllers.FXMLDochazkaController.ZAMESTNANEC;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class JasperManager {
    public static void getReport(List<DAODochazka> zaznamyZaMesic, LocalDate datum, Class clazz) throws JRException, SQLException {
        Duration odpracovano = Duration.ZERO;
        Duration odpracovanoVikend = Duration.ZERO;
        Duration odpracovanoSvatek = Duration.ZERO;
        int dnuSvatku, pracDnu, dnuDovolene, dnuNemocenske, dnuParagraf;
        dnuSvatku = pracDnu = dnuDovolene = dnuNemocenske = dnuParagraf = 0;
        
        // čerstvá data o přichodu a odchodu při otvírání přehledu
        
        // vstupní data
        LocalTime prichod = null;
         
        for (DAODochazka zaznam : zaznamyZaMesic){
            switch (zaznam.getIn_out()) {
                case InOut_enum.IN:
                    prichod = LocalTime.parse(zaznam.getTime());
                    break;
                case InOut_enum.OUT:
                    LocalTime odchod = LocalTime.parse(zaznam.getTime());
                    odpracovano = odpracovano.plus(Duration.between(prichod, odchod));
                    if ((LocalDate.parse(zaznam.getDate())).getDayOfWeek() == DayOfWeek.SATURDAY 
                            || (LocalDate.parse(zaznam.getDate())).getDayOfWeek() == DayOfWeek.SUNDAY){
                        odpracovanoVikend = odpracovano.plus(Duration.between(prichod, odchod));
                    }
                    break;
                case InOut_enum.DOV:
                    dnuDovolene++;
                    break;
                case InOut_enum.NEM:
                    dnuNemocenske++;
                    break;
                case InOut_enum.PAR:    
                    dnuParagraf++;
                    break;       
            }
        }
                
        // odpracované hodiny o svátku    
        for (LocalDate svatek : Svatky.ALL){
            LocalDate svatekTentoRok = LocalDate.of(datum.getYear(), svatek.getMonthValue(), svatek.getDayOfMonth());
            if (svatekTentoRok.getMonthValue() == datum.getMonthValue()
                    && svatekTentoRok.getDayOfWeek() != DayOfWeek.SATURDAY 
                    && svatekTentoRok.getDayOfWeek() != DayOfWeek.SUNDAY){
                dnuSvatku++;
            }            
           
            for (DAODochazka zaznam : zaznamyZaMesic){
                if (svatekTentoRok.equals(LocalDate.parse(zaznam.getDate()))){
                    switch (zaznam.getIn_out()) {
                        case InOut_enum.IN:
                            prichod = LocalTime.parse(zaznam.getTime());
                            break;
                        case InOut_enum.OUT:
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
            if (denMesice != DayOfWeek.SATURDAY && denMesice != DayOfWeek.SUNDAY){
                pracDnu++;
            }
        }
        pracDnu -= dnuSvatku;
        
        
        //passing informations through Beans   
        Map<String, Object> params = new HashMap<>();
        params.put("login_id", ZAMESTNANEC.getLogin_id());
        params.put("jmeno", ZAMESTNANEC.getJmeno());
        params.put("mesic", datum.format(DateTimeFormatter.ofPattern("LLLL")).toUpperCase());
        params.put("rok", String.valueOf(datum.getYear()));
        params.put("aktualniDatum", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.M.yyyy")));
        params.put("pracDnu", pracDnu);
        params.put("hodFond", ((double)pracDnu*ZAMESTNANEC.getUvazek()));
        params.put("odpracovano", ((double)(odpracovano.toHours())));
        params.put("dnuSvatku", dnuSvatku);
        params.put("odpracovanoSvatek", ((double)(odpracovanoSvatek.toHours())));
        params.put("odpracovanoVikend", ((double)(odpracovanoVikend.toHours())));
        params.put("uvazek", ZAMESTNANEC.getUvazek());
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
}
