package cz.benes.services;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;

public interface WindowService {

    FXMLLoader getWindow(Class clazz, Event event, String source, String title, Boolean resizable);

    void hideNode(ActionEvent event);
}
