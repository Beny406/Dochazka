package cz.benes.controllers;

import com.google.inject.Inject;
import cz.benes.database.dao.EmployeeDAO;
import cz.benes.database.domain.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLAdminZamestnanciController extends AbstractController {

    
    @FXML
    private TextField TextAreaHeslo;

    @FXML
    private TextField TextAreaJmenoPrijmeni;

    @FXML
    private TextField TextAreaLoginID;
    
    @FXML
    private TextField TextAreaUvazek;
    
    @FXML
    private RadioButton AdminRadio;
    
    @FXML
    private Label infoLabel;
    
    @FXML
    private ListView<Employee> zamestnanciListView;
    
    private ObservableList<Employee> zamestnanci = FXCollections.observableArrayList();

    @Inject
    private EmployeeDAO employeeDAO;

    private Employee selected;

    private Employee getZamOfValues(){
        return new Employee(TextAreaLoginID.getText(),
                                  TextAreaJmenoPrijmeni.getText(), 
                                  TextAreaHeslo.getText(),
                                  TextAreaUvazek.getText(),
                                  AdminRadio.isSelected() ? "1" : "0");
    }
   
    @FXML
    void handlePridatZamestnanceButton(ActionEvent event) {
        if (!TextAreaJmenoPrijmeni.getText().equals("") && !TextAreaLoginID.getText().equals("") && !TextAreaUvazek.getText().equals("")){
            // kontrola zda login_id již neexistuje
            if (employeeDAO.getByID(TextAreaLoginID.getText()) == null){
                if (employeeDAO.insert(getZamOfValues()) != 0) {
                    infoLabel.setText("Přidání proběhlo úspěšně.");
                    zamestnanci.add(new Employee(getZamOfValues()));
                } else {
                    infoLabel.setText("Chyba!!!");
                }   
            } else {
                infoLabel.setText("Tento login_id již existuje!");
            }
        } else {
            infoLabel.setText("Povinné údaje: id, jméno a úvazek.");
        }
    }
    
    @FXML
    void handleOpravitButton(ActionEvent event){
        if (selected != null){
            if (TextAreaLoginID.getText().equals(selected.getLogin_id())){
                if (employeeDAO.update(getZamOfValues()) != 0) {
                    infoLabel.setText("Aktualizace proběhla úspěšně.");
                } else {
                    infoLabel.setText("Chyba!!!");
                }    
            } else {
                infoLabel.setText("Login_id nelze editovat!");
            }
        }    
    }
    
    
    @FXML
    void handleSmazatButton(ActionEvent event){
        if (employeeDAO.delete(selected.getLogin_id()) != 0) {
            infoLabel.setText("Smazání proběhlo úspěšně.");
            zamestnanci.remove(selected);
        } else {
            infoLabel.setText("Chyba!!!");
        }     
         
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // vyplnění seznamu zaměstnanců
        zamestnanci.addAll(employeeDAO.getAll());
        zamestnanciListView.setItems(zamestnanci);
        zamestnanciListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{ 
            selected = newValue;       
            TextAreaJmenoPrijmeni.setText(newValue.getJmeno());
            TextAreaLoginID.setText(newValue.getLogin_id());
            TextAreaHeslo.setText(newValue.getHeslo());
            TextAreaUvazek.setText(String.valueOf(newValue.getUvazek()));
            AdminRadio.setSelected(newValue.getAdmin());
        });
    }    


    
}
