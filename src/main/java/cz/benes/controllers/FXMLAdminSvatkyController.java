package cz.benes.controllers;

import cz.benes.managers.db.Svatky;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


public class FXMLAdminSvatkyController implements Initializable {

   
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

    @FXML
    void handlePridatButton(ActionEvent event) {
        if (datePicker.getValue() == null){
            infoLabel.setText("Pole je prázdné!");
        } else {
            if (Svatky.insert(datePicker.getValue()) == 1){
                svatky.add(datePicker.getValue());
                infoLabel.setText("Záznam přidán.");             
            } else{
                infoLabel.setText("Chyba.");
            }
        }
    }

    @FXML
    void handleOdebratButton(ActionEvent event) {
        if (Svatky.delete(selected) == 1) {
            infoLabel.setText("Záznam odebrán.");
            svatky.remove(svatekListView.getSelectionModel().getSelectedIndex());       
        } else {
            infoLabel.setText("Chyba.");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Svatky.ALL.forEach(e -> svatky.add(e));
        svatekListView.setItems(svatky);
        svatekListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selected = newValue;
        });
    }
}
