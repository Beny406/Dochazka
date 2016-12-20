package cz.benes.controllers;

import cz.benes.beans.DAOZamestnanec;
import cz.benes.managers.db.Zamestnanci;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class FXMLAdminZamestnanciController implements Initializable {

    
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
    private ListView<DAOZamestnanec> zamestnanciListView;
    
    private ObservableList<DAOZamestnanec> zamestnanci = FXCollections.observableArrayList();
    
    private DAOZamestnanec selected;

    private DAOZamestnanec getZamOfValues(){
        return new DAOZamestnanec(TextAreaLoginID.getText(), 
                                  TextAreaJmenoPrijmeni.getText(), 
                                  TextAreaHeslo.getText(),
                                  TextAreaUvazek.getText(),
                                  AdminRadio.isSelected() ? "1" : "0");
    }
   
    @FXML
    void handlePridatZamestnanceButton(ActionEvent event) {
        if (!TextAreaJmenoPrijmeni.getText().equals("") && !TextAreaLoginID.getText().equals("") && !TextAreaUvazek.getText().equals("")){
            // kontrola zda login_id již neexistuje
            if (Zamestnanci.getByID(TextAreaLoginID.getText()) == null){
                if (Zamestnanci.insert(getZamOfValues()) != 0) {
                    infoLabel.setText("Přidání proběhlo úspěšně.");
                    zamestnanci.add(new DAOZamestnanec(getZamOfValues()));
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
                if (Zamestnanci.update(getZamOfValues()) != 0) {
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
        if (Zamestnanci.delete(selected.getLogin_id()) != 0) {
            infoLabel.setText("Smazání proběhlo úspěšně.");
            zamestnanci.remove(selected);
        } else {
            infoLabel.setText("Chyba!!!");
        }     
         
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // vyplnění seznamu zaměstnanců
        zamestnanci.addAll(Zamestnanci.getAll());
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
