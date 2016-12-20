package cz.benes.connection;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.prefs.Preferences;

public class DBConnection {

    private static final String dbIP = Preferences.systemRoot().get("DOCHAZKA_DB_ADDRESS", "127.0.0.1");
    private static final String address = "jdbc:mysql://" + dbIP + "/dochazka";
    private static final String login = "root";
    private static final String password = "root";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(address, login, password);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Chyba při připojování k databázi: " + e.getMessage());
                if (e.getMessage().contains("Unknown database")) {
                    dotazVytvoreniDB();
                }
            }
            return connection;
        } else {
            return connection;
        }
    }

    public static void close() {
        try {
            connection.close();
            connection = null;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    private static void dotazVytvoreniDB() {
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

    private static void createDbAndTables() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + dbIP, login, password);
        connection.prepareStatement("CREATE SCHEMA `dochazka` ;").executeUpdate();
        connection.prepareStatement("CREATE TABLE `dochazka`.`svatky` (\n"
                + "  `datum` date NOT NULL,\n"
                + "  PRIMARY KEY (`datum`)\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;").executeUpdate();
        connection.prepareStatement("CREATE TABLE `dochazka`.`zamestnanci` (\n"
                + "  `login_id` int(11) NOT NULL,\n"
                + "  `jmeno` varchar(10) DEFAULT NULL,\n"
                + "  `heslo` varchar(10) DEFAULT NULL,\n"
                + "  `prava` int(11) DEFAULT NULL,\n"
                + "  `uvazek` varchar(3) DEFAULT NULL,\n"
                + "  PRIMARY KEY (`login_id`)\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;").executeUpdate();
        connection.prepareStatement("CREATE TABLE `dochazka`.`dochazka` (\n"
                + "  `login_id` int(11) NOT NULL,\n"
                + "  `jmeno` varchar(10) DEFAULT NULL,\n"
                + "  `date` date DEFAULT NULL,\n"
                + "  `time` time(6) DEFAULT NULL,\n"
                + "  `in_out` varchar(5) DEFAULT NULL,\n"
                + "  PRIMARY KEY (`login_id`)\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;").executeUpdate();
        connection.prepareStatement("INSERT INTO `dochazka`.`zamestnanci` "
                + "(`login_id`, `jmeno`, `heslo`, `prava`, `uvazek`) VALUES "
                + "('1', 'admin', '', '1', '7.5');").executeUpdate();
        connection = DriverManager.getConnection(address, login, password);
    }

}
