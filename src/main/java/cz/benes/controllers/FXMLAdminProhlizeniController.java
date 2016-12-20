package cz.benes.controllers;

import cz.benes.beans.DAODochazka;
import cz.benes.beans.InOut_enum;
import cz.benes.beans.DAOZamestnanec;
import cz.benes.connection.DBConnection;
import cz.benes.managers.db.Dochazka;
import cz.benes.managers.db.Zamestnanci;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.util.Callback;
import jfxtras.scene.control.LocalTimePicker;


public class FXMLAdminProhlizeniController implements Initializable {
    
    @FXML
    private TextField rokTextField;
    
    @FXML
    private TextField mesicTextField;
    
    @FXML
    private TableColumn<DAODochazka, String> dateColumn;
    
    @FXML
    private TableColumn<DAODochazka, String> timeColumn;
     
    @FXML
    private TableColumn<DAODochazka, String> in_outColumn;
    
    @FXML
    private ListView<DAOZamestnanec> zamestnanciListView;
    
    @FXML
    private TableView<DAODochazka> tableView;
    
    @FXML
    private Label infoLabel;
    
    @FXML
    private Button pridejButton;
    
    private static Connection spojeni;
    
    private ObservableList<DAOZamestnanec> zamestnanciObservable = FXCollections.observableArrayList();
    
    private ObservableList<DAODochazka> dochazkaObservable = FXCollections.observableArrayList();
    
    private DAOZamestnanec selectedEmploye;
    
    private TableManager tableManager = new TableManager();

    
    @FXML
    void handleSmazButton (ActionEvent event) throws IOException{
        DAODochazka selectedRow = tableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null){
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Mazání záznamu");
            alert.setHeaderText("Opravdu chcete smazat záznam?");
            alert.setContentText(selectedEmploye + " | " + selectedRow.getDate() + " | " +  selectedRow.getTime() + " | " + selectedRow.getIn_out());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                if (tableManager.delete(selectedRow) != 0) {
                    infoLabel.setText("Mazání proběhlo úspěšně.");
                    dochazkaObservable.remove(selectedRow);
                } else {
                    infoLabel.setText("Chyba při mazání položky.");
                }
            }
        }
    }
    
    @FXML
    void handlePridejButton (ActionEvent event) throws SQLException{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Přidání záznamu uživateli " + selectedEmploye.toString() + ":");
        alert.setTitle("Přidat záznam");
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(((Node)(event.getSource())).getScene().getWindow());
        alert.getDialogPane().setPrefWidth(350);
        
        FlowPane flowPane = new FlowPane(10, 0);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(120);
        
        // nastavení limitu na DatePicker
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override 
                    public void updateItem(LocalDate item, boolean empty) {
                     super.updateItem(item, empty);
                        if (item.isAfter(LocalDate.now())) {
                            setDisable(true);
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
        
        LocalTimePicker timePicker = new LocalTimePicker();
        timePicker.setPrefWidth(120);
        
        ObservableList<String> in_out = FXCollections.observableArrayList(InOut_enum.IN , InOut_enum.OUT ,InOut_enum.DOV , InOut_enum.NEM, InOut_enum.PAR ); 
        ChoiceBox choiceBox = new ChoiceBox(in_out);
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(InOut_enum.DOV) || newValue.equals(InOut_enum.NEM) || newValue.equals(InOut_enum.PAR)){
                timePicker.setDisable(true);
                timePicker.setLocalTime(LocalTime.MIN);
            } else {
                timePicker.setDisable(false);
            }
        });
        
        flowPane.getChildren().addAll(datePicker, timePicker, choiceBox);
        
        alert.getDialogPane().setContent(flowPane);
        Optional<ButtonType> result = alert.showAndWait();
       
        if (result.get() == ButtonType.OK){
            if (Dochazka.insert(selectedEmploye, datePicker.getValue().toString(), timePicker.getLocalTime().toString(), (String) choiceBox.getValue()) != 0){
                infoLabel.setText("Zápis proběhl úspěšně.");
                dochazkaObservable.add(new DAODochazka(datePicker.getValue().toString(), 
                                         timePicker.getLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
                                         choiceBox.getValue().toString()));
            } else {
                infoLabel.setText("Nastala chyba při přidávání záznamu!");
            }
        }
    }
    
    @FXML
    void handleDatumEditCommit (TableColumn.CellEditEvent<DAODochazka, String> event) throws SQLException {
        boolean leapYear = Year.of(Integer.parseInt(event.getNewValue().substring(0, 4))).isLeap();
        int denMesice = Integer.parseInt(event.getNewValue().substring(8));
        int delkaMesice = Month.of(Integer.parseInt(event.getNewValue().substring(5,7))).length(leapYear);
        if (event.getNewValue().matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && (denMesice <= delkaMesice)){
            if (tableManager.updateDate(event) != 0){
                infoLabel.setText("Údaj byl úspěšně přepsán.");
            } else {
                infoLabel.setText("Chyba při přepisování údajů.");
            }
        } else {
            infoLabel.setText("Chyba! Požadovaný formát 2015-07-31!");  
        }
    }


    
    @FXML
    void handleCasEditCommit (TableColumn.CellEditEvent<DAODochazka, String> event) throws SQLException {
        if (event.getNewValue().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")){
            if (tableManager.updateTime(event) != 0){
                infoLabel.setText("Údaj byl úspěšně přepsán.");
            }      
        } else {
            infoLabel.setText("Chyba! Požadovaný formát 23:59:59!");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spojeni = DBConnection.getConnection();
        
        rokTextField.setText(String.valueOf(LocalDate.now().getYear()));
        mesicTextField.setText(String.valueOf(LocalDate.now().getMonthValue()));
        
        // vypíše zaměstnance z databáze
        zamestnanciObservable.addAll(Zamestnanci.getAll());
        zamestnanciListView.setItems(zamestnanciObservable);
        
        // umožní editovat sloupce
        dateColumn.setCellFactory(TextFieldTableCell.<DAODochazka>forTableColumn());
        timeColumn.setCellFactory(TextFieldTableCell.<DAODochazka>forTableColumn());
        
        // vyplní tableview
        dateColumn.setCellValueFactory(new PropertyValueFactory<DAODochazka,String>("date"));   
        timeColumn.setCellValueFactory(new PropertyValueFactory<DAODochazka,String>("time"));
        in_outColumn.setCellValueFactory(new PropertyValueFactory<DAODochazka,String>("in_out"));
        
        // aktualizace při označení jiného uživatele
        zamestnanciListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedEmploye = newValue;
            dochazkaObservable.setAll(Dochazka.getByLoginAndDate(newValue, mesicTextField.getText(), rokTextField.getText()));
            tableView.setItems(dochazkaObservable);
            pridejButton.setDisable(false);
        });
        
        // aktualizace údajů při změně roku
        rokTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dochazkaObservable.setAll(Dochazka.getByLoginAndDate(selectedEmploye, mesicTextField.getText(), newValue));
            tableView.setItems(dochazkaObservable);
        });
        
        // aktualizace údajů při změně měsíce
        mesicTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> { 
            dochazkaObservable.setAll(Dochazka.getByLoginAndDate(selectedEmploye, newValue, rokTextField.getText()));
            tableView.setItems(dochazkaObservable);
        });
    }
    
    class TableManager {
        public int delete(DAODochazka selectedRow) {
            int smazano = 0;
            try {
                PreparedStatement dotaz = spojeni.prepareStatement("DELETE FROM dochazka WHERE login_id=? AND date=? AND time=?");
                dotaz.setString(1, selectedEmploye.getLogin_id());
                dotaz.setString(2, selectedRow.getDate());
                dotaz.setString(3, selectedRow.getTime());
                smazano = dotaz.executeUpdate();
            } catch(Exception e){
                System.err.println(e.getMessage());
            }
            return smazano;
        }   
        
        public int updateDate(TableColumn.CellEditEvent<DAODochazka, String> event) {
            int upraveno = 0;
            try {
                PreparedStatement dotaz = spojeni.prepareStatement("UPDATE dochazka SET date=? WHERE jmeno=? AND date=? AND time=?");
                dotaz.setString(1, event.getNewValue());
                dotaz.setString(2, selectedEmploye.getJmeno());
                dotaz.setString(3, event.getOldValue());
                dotaz.setString(4, tableView.getSelectionModel().getSelectedItem().getTime());
                upraveno = dotaz.executeUpdate();
            } catch(Exception e){
                System.out.println("Chyba při přepisování údajů: " + e.getMessage());
            }
            return upraveno;
        }
        
        public int updateTime(TableColumn.CellEditEvent<DAODochazka, String> event) {
            int upraveno = 0;
            try {
                PreparedStatement dotaz = spojeni.prepareStatement("UPDATE dochazka SET time=? WHERE jmeno=? AND date=? AND time=?");
                dotaz.setString(1, event.getNewValue());
                dotaz.setString(2, selectedEmploye.getJmeno());
                dotaz.setString(3, tableView.getSelectionModel().getSelectedItem().getDate());
                dotaz.setString(4, event.getOldValue());
                upraveno = dotaz.executeUpdate();
            } catch(Exception e){
                System.out.println("Chyba při přepisování údajů: " + e.getMessage() );
            }
            return upraveno;
        }
    }
    
}
