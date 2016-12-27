package cz.benes.controllers;

import com.google.inject.Inject;
import cz.benes.database.dao.EmployeeDAO;
import cz.benes.database.domain.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class FXMLLoginController extends AbstractController {

    @FXML
    private Label loginLabel;

    @FXML
    private TextField uzivatelskeId;

    @FXML
    private TextField uzHeslo;

    @Inject
    private EmployeeDAO employeeDAO;

    @Inject
    private Employee employee;

    @FXML
    public void handleLoginButton(ActionEvent event) throws IOException, InterruptedException {
        String zadaneId = uzivatelskeId.getText();
        if (zadaneId.matches("\\d+.\\d+.\\d+.\\d+")) {
            Preferences.systemRoot().put("DOCHAZKA_DB_ADDRESS", zadaneId);
            zadaneId = "1";
        }
        if (!zadaneId.equals("")) {
            employee.from(employeeDAO.getByID(zadaneId));
            if (employee != null) {
                if (zadaneId.equals(employee.getLogin_id()) && uzHeslo.getText().equals(employee.getHeslo())) {
                    windowService.getWindow(getClass(), null, "/fxml/FXMLDochazka.fxml", employee.getJmeno(), Boolean.TRUE);
                    windowService.hideNode(event);
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
        super.initialize(url, rb);

//       Effect glow = new DropShadow(15, Color.BLUE);
//       loginButton.setEffect(glow);
    }

}
