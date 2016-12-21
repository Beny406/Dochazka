package cz.benes.controllers;

import cz.benes.database.dao.EmployeesDAO;
import cz.benes.database.domain.Employee;
import cz.benes.managers.WindowManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class FXMLLoginController implements Initializable {

    @FXML
    private Label loginLabel;

    @FXML
    private TextField uzivatelskeId;

    @FXML
    private TextField uzHeslo;

    static Employee zamestnanec;

    @FXML
    public void handleLoginButton(ActionEvent event) throws IOException, InterruptedException {
        String zadaneId = uzivatelskeId.getText();
        if (zadaneId.matches("\\d+.\\d+.\\d+.\\d+")) {
            Preferences.systemRoot().put("DOCHAZKA_DB_ADDRESS", zadaneId);
            zadaneId = "1";
        }
        if (!zadaneId.equals("")) {
            zamestnanec = EmployeesDAO.getByID(zadaneId);
            if (zamestnanec != null) {
                if (zadaneId.equals(zamestnanec.getLogin_id()) && uzHeslo.getText().equals(zamestnanec.getHeslo())) {
                    WindowManager.getWindow(getClass(), null, "/fxml/FXMLDochazka.fxml", zamestnanec.getJmeno(), Boolean.TRUE);
                    WindowManager.hideNode(event);
                } else {
                    loginLabel.setText("Chybné heslo nebo ID!");
                }
            } else {
                loginLabel.setText("ID neexistuje!");
            }
        } else {
            loginLabel.setText("Vyplňte údaje.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//       Effect glow = new DropShadow(15, Color.BLUE);
//       loginButton.setEffect(glow);
    }

}
