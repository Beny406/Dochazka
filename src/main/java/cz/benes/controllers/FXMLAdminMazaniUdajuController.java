/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.controllers;

import cz.benes.domain.CheckListMesic;
import cz.benes.connection.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckListView;

/**
 * FXML Controller class
 *
 * @author PB
 */
public class FXMLAdminMazaniUdajuController implements Initializable {

    
    private CheckListView<CheckListMesic> checkList;
    
    @FXML
    private TextField textFieldRok;
    
    ObservableList<CheckListMesic> data = FXCollections.observableArrayList();
    
    @FXML
    private Pane pane;
    
    @FXML
    private Label infoLabel;
    private static Connection spojeni;
    
    @FXML
    void handleSmazatButton(ActionEvent event) {
        int pocetSmazanychRadku = 0;
        if (textFieldRok.getText().equals("")){
            infoLabel.setText("Vyplňte rok!");
        } else {
            try {
                PreparedStatement dotaz = spojeni.prepareStatement("DELETE FROM dochazka WHERE year(date)=? AND month(date)=?");
                dotaz.setString(1, textFieldRok.getText());
                ObservableList<CheckListMesic> checkedItems = checkList.getCheckModel().getCheckedItems();
                for (CheckListMesic mesic : checkedItems){
                    dotaz.setString(2, String.valueOf(mesic.getLocalDate().getMonth().getValue()));
                    pocetSmazanychRadku += dotaz.executeUpdate();
                }
                infoLabel.setText("Smazaných řádků: " + pocetSmazanychRadku);
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }
    
    @FXML
    void handleOznacVseButton(ActionEvent event) {
        checkList.getCheckModel().checkAll();
    }
    
    @FXML
    void handleOdoznacButton(ActionEvent event) {
    checkList.getCheckModel().clearChecks();
    }
    
    
    @FXML
    void handleZpetButton(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spojeni = DBConnection.getConnection();
        for (int i = 1; i <= 12; i++){
            data.add((new CheckListMesic(LocalDate.now().withMonth(i), false)));
        }
        
        checkList = new CheckListView<>();
        checkList.setPrefWidth(110);
        pane.setPrefWidth(110);
        pane.getChildren().add(checkList);
        checkList.setItems(data);
    }    
    
}
