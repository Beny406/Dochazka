package cz.benes.database.dao;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.sql2o.Connection;

import java.util.Optional;

public class DBInitiatorImpl extends AbstractDAO implements DBInitiator {

    @Override
    public void initiateDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error while initiating JDBC driver: " + e);
        }
        boolean dbExist;
        try (Connection conn = sql2oServer.open()) {
            dbExist = conn.createQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = :dbName")
                    .addParameter("dbName", dbName)
                    .executeScalar(String.class) != null;
        }
        if (!dbExist) {
            dotazVytvoreniDB();
        }
    }

    private void dotazVytvoreniDB() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Vytvoření schématu");
            alert.setHeaderText("Požadovaná databáze 'dochazka' neexistuje!");
            alert.setContentText("Chcete ji vytvořit?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                createDbAndTables();
            } else {
                System.exit(0);
            }
        } catch (Exception ex) {
            System.err.println("Vytvoření schématu se nezdařilo: " + ex);
        }
    }

    private void createDbAndTables() {
        try (Connection conn = sql2oServer.open()) {
            conn.createQuery("CREATE SCHEMA `dochazka` ;").executeUpdate();

            conn.createQuery("CREATE TABLE `dochazka`.`svatky` (\n"
                    + "  `datum` date NOT NULL,\n"
                    + "  PRIMARY KEY (`datum`)\n"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;").executeUpdate();
            conn.createQuery("CREATE TABLE `dochazka`.`zamestnanci` (\n"
                    + "  `login_id` int(11) NOT NULL,\n"
                    + "  `jmeno` varchar(10) DEFAULT NULL,\n"
                    + "  `heslo` varchar(10) DEFAULT NULL,\n"
                    + "  `prava` int(11) DEFAULT NULL,\n"
                    + "  `uvazek` varchar(3) DEFAULT NULL,\n"
                    + "  PRIMARY KEY (`login_id`)\n"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;").executeUpdate();
            conn.createQuery("CREATE TABLE `dochazka`.`dochazka` (\n"
                    + "  `login_id` int(11) NOT NULL,\n"
                    + "  `jmeno` varchar(10) DEFAULT NULL,\n"
                    + "  `date` date DEFAULT NULL,\n"
                    + "  `time` time(6) DEFAULT NULL,\n"
                    + "  `type` varchar(5) DEFAULT NULL,\n"
                    + "  PRIMARY KEY (`login_id`)\n"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;").executeUpdate();
            conn.createQuery("INSERT INTO `dochazka`.`zamestnanci` "
                    + "(`login_id`, `jmeno`, `heslo`, `prava`, `uvazek`) VALUES "
                    + "('1', 'admin', '', '1', '7.5');").executeUpdate();
        }
    }

}
