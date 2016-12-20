package cz.benes.controllers;

import cz.benes.managers.WindowManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


public class FXMLAdminController implements Initializable {
    
    @FXML
    void handleZamestnanciButton(ActionEvent event) throws IOException{
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLAdminZamestnanci.fxml", "Správa zaměstnanců", Boolean.TRUE);
     }
    
    @FXML
    void handleSvatkyButton(ActionEvent event) throws IOException{
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLAdminSvatky.fxml", "Správa svátků", Boolean.TRUE);
     }
     
    @FXML
    void handleProhlizeniAOprava(ActionEvent event) throws IOException{
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLAdminProhlizeni.fxml", "Prohlížení a oprava údajů", Boolean.TRUE);
    }
     
    @FXML
    void handleMazaniUdaju(ActionEvent event) throws IOException{
        WindowManager.getWindow(getClass(), event, "/fxml/FXMLAdminMazaniUdaju.fxml", "Mazání údajů", Boolean.TRUE);

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }    
    
}
