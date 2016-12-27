package gui;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import cz.benes.guice.TestInjectorAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.h2.tools.RunScript;
import org.junit.After;
import org.loadui.testfx.GuiTest;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CommonGuiTest extends GuiTest implements TestInjectorAware {

    @Inject
    @Named("db")
    protected String db;

    @Inject
    @Named("login")
    protected String login;

    @Inject
    @Named("password")
    protected String password;

    public void setUp() {
        try {
            Class.forName("org.h2.Driver");
            injector.injectMembers(this);
            RunScript.execute(db, login, password, "classpath:setup.sql", Charset.defaultCharset(), true);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error while preparing environment for test", e);
        }
    }

    @Override
    protected Parent getRootNode() {
        setUp();
        Parent parent = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getFxml()));
            fxmlLoader.setControllerFactory(injector::getInstance);
            parent = fxmlLoader.load();
            return parent;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
        return parent;
    }

    protected abstract String getFxml();

    @After
    public void tearDown() throws Exception {
        RunScript.execute(db, login, password, "classpath:teardown.sql", Charset.defaultCharset(), true);
    }

}
