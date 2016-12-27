package cz.benes.controllers;

import cz.benes.database.dao.HolidaysDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class FXMLAdminSvatkyController extends AbstractController implements Initializable {

   
    @FXML
    private ListView<LocalDate> svatekListView;

    @FXML
    private DatePicker datePicker;
    
    @FXML
    private Label infoLabel;
    
    @FXML
    private Button pridatButton;
    
    @FXML
    private Button odebratButton;
    
    private ObservableList<LocalDate> svatky = FXCollections.observableArrayList();
    
    private LocalDate selected;

    protected HolidaysDAO holidaysDAO = getInstance(HolidaysDAO.class);

    @FXML
    void handlePridatButton(ActionEvent event) {
        if (datePicker.getValue() == null){
            infoLabel.setText("Pole je prázdné!");
        } else {
            if (holidaysDAO.insert(datePicker.getValue())){
                svatky.add(datePicker.getValue());
                infoLabel.setText("Záznam přidán.");             
            } else{
                infoLabel.setText("Chyba.");
            }
        }
    }

    @FXML
    void handleOdebratButton(ActionEvent event) {
        if (holidaysDAO.delete(selected)) {
            infoLabel.setText("Záznam odebrán.");
            svatky.remove(svatekListView.getSelectionModel().getSelectedIndex());       
        } else {
            infoLabel.setText("Chyba.");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        holidaysDAO.getAll().forEach(e -> svatky.add(e));
        svatekListView.setItems(svatky);
        svatekListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selected = newValue;
        });
    }
}
