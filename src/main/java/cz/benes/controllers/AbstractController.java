package cz.benes.controllers;

import com.google.inject.Inject;
import cz.benes.guice.InjectorAware;
import cz.benes.services.WindowService;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractController implements InjectorAware, Initializable {

    @Inject
    protected WindowService windowService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
