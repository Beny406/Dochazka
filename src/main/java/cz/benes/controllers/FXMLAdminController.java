package cz.benes.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;


public class FXMLAdminController extends AbstractController{
    
    @FXML
    void handleZamestnanciButton(ActionEvent event) throws IOException{
        windowService.getWindow(getClass(), event, "/fxml/FXMLAdminZamestnanci.fxml", "Správa zaměstnanců", Boolean.TRUE);
     }
    
    @FXML
    void handleSvatkyButton(ActionEvent event) throws IOException{
        windowService.getWindow(getClass(), event, "/fxml/FXMLAdminSvatky.fxml", "Správa svátků", Boolean.TRUE);
     }
     
    @FXML
    void handleProhlizeniAOprava(ActionEvent event) throws IOException{
        windowService.getWindow(getClass(), event, "/fxml/FXMLAdminProhlizeni.fxml", "Prohlížení a oprava údajů", Boolean.TRUE);
    }
     
    @FXML
    void handleMazaniUdaju(ActionEvent event) throws IOException{
        windowService.getWindow(getClass(), event, "/fxml/FXMLAdminMazaniUdaju.fxml", "Mazání údajů", Boolean.TRUE);

    }

    
}
