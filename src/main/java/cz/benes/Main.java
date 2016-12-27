package cz.benes;

import cz.benes.database.dao.DBInitiator;
import cz.benes.guice.InjectorAware;
import cz.benes.services.WindowService;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application implements InjectorAware{
    
    @Override
    public void start(Stage stage) throws Exception {
//        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        getInstance(DBInitiator.class).initiateDB();
        getInstance(WindowService.class).getWindow(getClass(), null, "/fxml/FXMLLogin.fxml", "Login", Boolean.TRUE);
    }

    @Override
    public void stop() throws Exception {

    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    

    
}
