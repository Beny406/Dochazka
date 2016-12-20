package cz.benes.controllers;

import cz.benes.beanfactory.HolidaysFactory;
import cz.benes.domain.AttendanceRecord;
import cz.benes.domain.RecordType;
import cz.benes.managers.db.AttendanceDAO;
import cz.benes.domain.Employee;
import cz.benes.managers.JasperManager;
import cz.benes.managers.db.HolidaysDAO;
import cz.benes.managers.WindowManager;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.time.Duration;
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


public class FXMLDochazkaController implements Initializable {
    
    static{
        System.out.println("Static");
    }
    
    public static final Employee ZAMESTNANEC = FXMLLoginController.zamestnanec;
    private final int dnuSvatku = HolidaysDAO.getPocetSvatkuVmesici(LocalDate.now());
    
    private Duration odpracovano;
    private int pracDnu;
    
    @FXML
    private Label Lden;
    
    @FXML
    private Label Ldatum;
    
    @FXML
    private Label Lcas;
    
    @FXML
    private Label prihlaseniLabel;
    
    @FXML
    private Label hodFondLabel;
    
    @FXML
    private Label odpracovanoLabel;
    
    @FXML
    private Label zbyvaLabel;
    
    @FXML
    private Button odchodButton;
    
    @FXML
    private Button prichodButton;
    
    @FXML
    private Button ButtonAdministrator;
    
    @FXML
    private Button minMesicButton;
    
    @FXML
    void handlePrichodButton(ActionEvent event) {
        if (AttendanceDAO.insert(null, null, null, RecordType.IN) != 0) {
            prihlaseniLabel.setText("Přihlášení proběhlo úspěšně.");
            odchodButton.setDisable(false);
            prichodButton.setDisable(true);
        } else {
            prihlaseniLabel.setText("Nastala chyba při přihlášení!!");
        }
    }
    
    @FXML
    void handleOdchodButton(ActionEvent event) {
        if (AttendanceDAO.insert(null, null, null, RecordType.OUT) != 0) {
            prihlaseniLabel.setText("Odhlášení proběhlo úspěšně.");
            odchodButton.setDisable(true);
            prichodButton.setDisable(false);
        } else {
            prihlaseniLabel.setText("Nastala chyba při odhlašování!!");
        }   

        //aktualizace odpracované doby při odchodu    
        odpracovano = AttendanceDAO.spocitejOdpracovano(AttendanceDAO.getThisMonth());
        odpracovanoLabel.setText(odpracovano.toHours() + " hodin " + odpracovano.toMinutes()%60 + " minut");

        //aktualizace doby k odpracování při odchodu
        Duration zbyvaOdpracovat = Duration.ofMinutes((long) ((pracDnu * ZAMESTNANEC.getUvazek() * 60) - odpracovano.toMinutes()));
        zbyvaLabel.setText(zbyvaOdpracovat.toHours() + " hodin " + zbyvaOdpracovat.toMinutes()%60 + " minut");
   
    }
    
    @FXML
    void handlePrehledButton(ActionEvent event) throws JRException, SQLException, IOException{
        JasperManager.getReport(AttendanceDAO.getThisMonth(), LocalDate.now(), getClass());
    } 
    
    @FXML
    void handleMinMesicButton(ActionEvent event) throws SQLException, JRException {
        JasperManager.getReport(AttendanceDAO.getLastMonth(), LocalDate.now().minusMonths(1), getClass());
    }
    
    @FXML
    void handleZadostDovolenaButton(ActionEvent event) throws JRException, SQLException{ 
        Map<String, Object> params = new HashMap<>();
        params.put("login_id", ZAMESTNANEC.getLogin_id());
        params.put("jmeno", ZAMESTNANEC.getJmeno());
        params.put("mesic", LocalDate.now().format(DateTimeFormatter.ofPattern("LLLL")).toUpperCase());
        params.put("rok", String.valueOf(LocalDate.now().getYear()));
        params.put("aktualniDatum", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.M.yyyy")));
        // změna čárek na tečku při zaokrouhlování parametrů
        params.put(JRParameter.REPORT_LOCALE, new Locale("en", "US"));
        JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("/reports/zadostDovolena.jrxml")); 
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint  jasperPrint  = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(HolidaysFactory.generateDays()));
        JasperViewer.viewReport(jasperPrint, false); 
    }
    
    @FXML
    void handleDovolenaButton(ActionEvent event) throws IOException{
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLDochazkaDovolena.fxml", "Přidat dovolenou", Boolean.TRUE);
    }
    
    @FXML
    void handleAdminButton(ActionEvent event) throws IOException {
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLAdmin.fxml", "Administrátor", Boolean.TRUE);
    }
    
    
    @FXML
    void handleNemocButton(ActionEvent event) throws IOException{
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLDochazkaNemoc.fxml", "Přidat nemocenskou", Boolean.TRUE);
    }
    
    @FXML
    void handleParagrafButton(ActionEvent event) throws IOException{
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLDochazkaParagraf.fxml", "Přidat paragraf", Boolean.TRUE);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("initialize");
        Lden.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE")));
        Ldatum.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.M.yyyy")));
        
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("HH:mm:ss");
        Lcas.setText(LocalTime.now().format(formater));
        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), (ActionEvent event) -> {
            Lcas.setText(LocalTime.now().format(formater));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
        
        // --------------- User specific ----------------------//
        ButtonAdministrator.setDisable(!ZAMESTNANEC.getAdmin());
        
        
        //zjištění zda poslední akce byla IN nebo OUT
        AttendanceRecord poslZaznam = AttendanceDAO.getLastRecord();
        if (poslZaznam != null){
            String datumPoslZaznamu = poslZaznam.getDate();
            String casPoslZaznamu = poslZaznam.getTime();
            String poslAkce = poslZaznam.getIn_out();

            if ((poslAkce).equals(RecordType.IN)){
                prichodButton.setDisable(true);
                odchodButton.setDisable(false);
                prihlaseniLabel.setText("Příchod: " + datumPoslZaznamu + " " + casPoslZaznamu);
                // kontrola, zda se uživatel nezapomněl předchozí den odhlásit a případné přidání odhlášení s hodnotou + 1s
                // pokud je možná práce přes půlnoc - nastavit jinak!
                if (!LocalDate.parse(datumPoslZaznamu).equals(LocalDate.now())){
                    int pridani = AttendanceDAO.insert(null, datumPoslZaznamu, Time.valueOf(LocalTime.parse(casPoslZaznamu).plusSeconds(1)).toString(), RecordType.OUT);
                    prichodButton.setDisable(false);
                    odchodButton.setDisable(true);
                    if (pridani != 0){
                        prihlaseniLabel.setText("Naposledy jste se zapomněl/a odhlásit. Kontaktujte admina pro opravu údajů.");
                    }
                }
            } else {
                prihlaseniLabel.setText("Odchod: " + datumPoslZaznamu + " " + casPoslZaznamu);    
            }
        } else {
            prihlaseniLabel.setText("Vítejte. Zatím nemáte žádný záznam.");
        }


        //hodinový fond podle úvazku
        int pocetDniMesice = LocalDate.now().getMonth().length(LocalDate.now().isLeapYear());
        for (int i = 1; i <= pocetDniMesice ; i++){
            DayOfWeek denMesice = LocalDate.now().withDayOfMonth(i).getDayOfWeek();
            if (denMesice != DayOfWeek.SATURDAY && denMesice != DayOfWeek.SUNDAY){
                pracDnu++;
            }
        }
        pracDnu -= dnuSvatku;
        hodFondLabel.setText(ZAMESTNANEC.getUvazek() * pracDnu + " hodin (" + pracDnu + " dnů)");

        // zjištění odpracované doby v aktuálním měsíci při startupu
        odpracovano = AttendanceDAO.spocitejOdpracovano(AttendanceDAO.getThisMonth());
        odpracovanoLabel.setText(odpracovano.toHours() + " hodin " + odpracovano.toMinutes()%60 + " minut");


        // Zbývá odpracovat on startup
        Duration zbyvaOpdracovat = Duration.ofMinutes((long) (pracDnu * ZAMESTNANEC.getUvazek() * 60 - odpracovano.toMinutes()));
        String odpracovat = zbyvaOpdracovat.toHours() + " hodin " + zbyvaOpdracovat.toMinutes()%60 + " minut";
        zbyvaLabel.setText(odpracovat);

        // vypnutí přehledu pro předchozí měsíc
        if (AttendanceDAO.getLastMonth().isEmpty()){
            minMesicButton.setDisable(true);
        }
            
       
          
      
    }

}
