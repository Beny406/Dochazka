package cz.benes.controllers;

import cz.benes.database.dao.AbstractDAO;
import cz.benes.database.dao.AttendanceDAO;
import cz.benes.database.dao.EmployeeDAO;
import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.Employee;
import cz.benes.database.domain.RecordType;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.util.Callback;
import jfxtras.scene.control.LocalTimePicker;
import org.sql2o.Connection;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;


public class FXMLAdminProhlizeniController extends AbstractController implements Initializable {

    AttendanceDAO attendanceDAO = getInstance(AttendanceDAO.class);

    EmployeeDAO employeeDAO = getInstance(EmployeeDAO.class);

    @FXML
    private TextField rokTextField;
    @FXML
    private TextField mesicTextField;
    @FXML
    private TableColumn<AttendanceRecord, String> dateColumn;
    @FXML
    private TableColumn<AttendanceRecord, String> timeColumn;
    @FXML
    private TableColumn<AttendanceRecord, String> in_outColumn;
    @FXML
    private ListView<Employee> zamestnanciListView;
    @FXML
    private TableView<AttendanceRecord> tableView;
    @FXML
    private Label infoLabel;
    @FXML
    private Button pridejButton;
    private ObservableList<Employee> zamestnanciObservable = FXCollections.observableArrayList();

    private ObservableList<AttendanceRecord> dochazkaObservable = FXCollections.observableArrayList();

    private Employee selectedEmploye;

    private AdminDAO adminDAO = new AdminDAO();


    @FXML
    void handleSmazButton(ActionEvent event) throws IOException {
        AttendanceRecord selectedRow = tableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Mazání záznamu");
            alert.setHeaderText("Opravdu chcete smazat záznam?");
            alert.setContentText(selectedEmploye + " | " + selectedRow.getDate() + " | " + selectedRow.getTime() + " | " + selectedRow.getType());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (adminDAO.delete(selectedRow) != 0) {
                    infoLabel.setText("Mazání proběhlo úspěšně.");
                    dochazkaObservable.remove(selectedRow);
                } else {
                    infoLabel.setText("Chyba při mazání položky.");
                }
            }
        }
    }

    @FXML
    void handlePridejButton(ActionEvent event) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Přidání záznamu uživateli " + selectedEmploye.toString() + ":");
        alert.setTitle("Přidat záznam");
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(((Node) (event.getSource())).getScene().getWindow());
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

        ObservableList<Enum> in_out = FXCollections.observableArrayList(RecordType.IN, RecordType.OUT, RecordType.DOV, RecordType.NEM, RecordType.PAR);
        ChoiceBox choiceBox = new ChoiceBox(in_out);
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(RecordType.DOV) || newValue.equals(RecordType.NEM) || newValue.equals(RecordType.PAR)) {
                timePicker.setDisable(true);
                timePicker.setLocalTime(LocalTime.MIN);
            } else {
                timePicker.setDisable(false);
            }
        });

        flowPane.getChildren().addAll(datePicker, timePicker, choiceBox);

        alert.getDialogPane().setContent(flowPane);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            if (attendanceDAO.insert(selectedEmploye, datePicker.getValue().toString(), timePicker.getLocalTime().toString(), RecordType.valueOf((String) choiceBox.getValue()))) {
                infoLabel.setText("Zápis proběhl úspěšně.");
                dochazkaObservable.add(new AttendanceRecord(datePicker.getValue().toString(),
                        timePicker.getLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        choiceBox.getValue().toString()));
            } else {
                infoLabel.setText("Nastala chyba při přidávání záznamu!");
            }
        }
    }

    @FXML
    void handleDatumEditCommit(TableColumn.CellEditEvent<AttendanceRecord, String> event) throws SQLException {
        boolean leapYear = Year.of(Integer.parseInt(event.getNewValue().substring(0, 4))).isLeap();
        int denMesice = Integer.parseInt(event.getNewValue().substring(8));
        int delkaMesice = Month.of(Integer.parseInt(event.getNewValue().substring(5, 7))).length(leapYear);
        if (event.getNewValue().matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])") && (denMesice <= delkaMesice)) {
            if (adminDAO.updateDate(event) != 0) {
                infoLabel.setText("Údaj byl úspěšně přepsán.");
            } else {
                infoLabel.setText("Chyba při přepisování údajů.");
            }
        } else {
            infoLabel.setText("Chyba! Požadovaný formát 2015-07-31!");
        }
    }


    @FXML
    void handleCasEditCommit(TableColumn.CellEditEvent<AttendanceRecord, String> event) throws SQLException {
        if (event.getNewValue().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")) {
            if (adminDAO.updateTime(event) != 0) {
                infoLabel.setText("Údaj byl úspěšně přepsán.");
            }
        } else {
            infoLabel.setText("Chyba! Požadovaný formát 23:59:59!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        rokTextField.setText(String.valueOf(LocalDate.now().getYear()));
        mesicTextField.setText(String.valueOf(LocalDate.now().getMonthValue()));

        // vypíše zaměstnance z databáze
        zamestnanciObservable.addAll(employeeDAO.getAll());
        zamestnanciListView.setItems(zamestnanciObservable);

        // umožní editovat sloupce
        dateColumn.setCellFactory(TextFieldTableCell.<AttendanceRecord>forTableColumn());
        timeColumn.setCellFactory(TextFieldTableCell.<AttendanceRecord>forTableColumn());

        // vyplní tableview
        dateColumn.setCellValueFactory(new PropertyValueFactory<AttendanceRecord, String>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<AttendanceRecord, String>("time"));
        in_outColumn.setCellValueFactory(new PropertyValueFactory<AttendanceRecord, String>("in_out"));

        // aktualizace při označení jiného uživatele
        zamestnanciListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedEmploye = newValue;
            dochazkaObservable.setAll(attendanceDAO.getByLoginAndDate(newValue, mesicTextField.getText(), rokTextField.getText()));
            tableView.setItems(dochazkaObservable);
            pridejButton.setDisable(false);
        });

        // aktualizace údajů při změně roku
        rokTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dochazkaObservable.setAll(attendanceDAO.getByLoginAndDate(selectedEmploye, mesicTextField.getText(), newValue));
            tableView.setItems(dochazkaObservable);
        });

        // aktualizace údajů při změně měsíce
        mesicTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dochazkaObservable.setAll(attendanceDAO.getByLoginAndDate(selectedEmploye, newValue, rokTextField.getText()));
            tableView.setItems(dochazkaObservable);
        });
    }

    class AdminDAO extends AbstractDAO {

        protected void init(){
        }

        public int delete(AttendanceRecord selectedRow) {
            try (Connection conn = sql2o.open()) {
                return conn.createQuery("DELETE FROM dochazka WHERE login_id= :login_id AND date= :date AND time= :time")
                        .addParameter("login_id", selectedEmploye.getLogin_id())
                        .addParameter("date", selectedRow.getDate())
                        .addParameter("time", selectedRow.getTime())
                        .executeUpdate().getResult();
            }
        }

        public int updateDate(TableColumn.CellEditEvent<AttendanceRecord, String> event) {
            try (Connection conn = sql2o.open()) {
                return conn.createQuery("UPDATE dochazka SET date= :dateSet WHERE jmeno= :jmeno AND date= :dateWhere AND time= :time")
                        .addParameter("dateSet", event.getNewValue())
                        .addParameter("jmeno", selectedEmploye.getJmeno())
                        .addParameter("dateWhere", event.getOldValue())
                        .addParameter("time", tableView.getSelectionModel().getSelectedItem().getTime())
                        .executeUpdate().getResult();
            }
        }

        public int updateTime(TableColumn.CellEditEvent<AttendanceRecord, String> event) {
            try (Connection conn = sql2o.open()) {
                return conn.createQuery("UPDATE dochazka SET time= :timeSet WHERE jmeno= :jmeno AND date= :date AND time= timeWhere")
                        .addParameter("timeSet", event.getNewValue())
                        .addParameter("jmeno", selectedEmploye.getJmeno())
                        .addParameter("date", tableView.getSelectionModel().getSelectedItem().getDate())
                        .addParameter("timeWhere", event.getOldValue()).executeUpdate().getResult();
            }
        }
    }

}
