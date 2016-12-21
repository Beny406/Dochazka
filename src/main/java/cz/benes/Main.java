package cz.benes;

import cz.benes.database.DBConnection;
import cz.benes.managers.WindowManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
//        setUserAgentStylesheet(STYLESHEET_CASPIAN);

        WindowManager.getWindow(getClass(), null, "/fxml/FXMLLogin.fxml", "Login", Boolean.TRUE);
    }

    @Override
    public void stop() throws Exception {
        DBConnection.close();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    

    
}
