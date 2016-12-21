/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.controllers;

import cz.benes.database.dao.AttendanceDAO;
import cz.benes.database.domain.CheckableMonth;
import cz.benes.managers.WindowManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckListView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author PB
 */
public class FXMLAdminMazaniUdajuController implements Initializable {

    ObservableList<CheckableMonth> data = FXCollections.observableArrayList();

    private CheckListView<CheckableMonth> checkList;

    @FXML
    private TextField textFieldRok;

    @FXML
    private Pane pane;

    @FXML
    private Label infoLabel;

    @FXML
    void handleSmazatButton(ActionEvent event) {

        if (textFieldRok.getText().equals("")) {
            infoLabel.setText("Vyplňte rok!");
        } else {
            try {
                ObservableList<CheckableMonth> checkedItems = checkList.getCheckModel().getCheckedItems();
                int pocetSmazanychRadku = AttendanceDAO.deleteRecords(checkedItems, textFieldRok.getText());
                infoLabel.setText("Smazaných řádků: " + pocetSmazanychRadku);
            } catch (Exception e) {
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
        WindowManager.hideNode(event);
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 1; i <= 12; i++) {
            data.add((new CheckableMonth(LocalDate.now().withMonth(i), false)));
        }

        checkList = new CheckListView<>();
        checkList.setPrefWidth(110);
        pane.setPrefWidth(110);
        pane.getChildren().add(checkList);
        checkList.setItems(data);
    }

}
