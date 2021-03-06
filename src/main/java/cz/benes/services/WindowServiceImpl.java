/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.benes.services;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author PB
 */
public class WindowServiceImpl extends AbstractService implements WindowService {

    @Override
    public FXMLLoader getWindow(Class clazz, Event event, String source, String title, Boolean resizable){
        FXMLLoader fxmlLoader = new FXMLLoader(clazz.getResource(source));
        fxmlLoader.setControllerFactory(injector::getInstance);
        try {
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            if(event != null){
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(((Node)(event.getSource())).getScene().getWindow());
            }
//          //bug - false způsobuje odsazení
            stage.setResizable(resizable);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.err.println("Chyba při otevírání okna: " + ex.getCause());
        }
        return fxmlLoader;
    }

    @Override
    public void hideNode(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
