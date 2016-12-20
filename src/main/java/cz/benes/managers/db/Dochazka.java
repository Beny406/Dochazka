package cz.benes.managers.db;

import cz.benes.beans.DAODochazka;
import cz.benes.beans.InOut_enum;
import cz.benes.beans.DAOZamestnanec;
import cz.benes.connection.DBConnection;
import cz.benes.controllers.FXMLDochazkaController;
import static cz.benes.controllers.FXMLDochazkaController.ZAMESTNANEC;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Dochazka {

    static {
        spojeni = DBConnection.getConnection();
    }
    private static final Connection spojeni;

    public static int insert(DAOZamestnanec employe, String date, String time, String in_out) {
        int pridano = 0;
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("INSERT INTO dochazka (login_id, jmeno, date, time, in_out) VALUES (?, ?, ?, ?, ?)");
            if (employe == null) {
                dotaz.setString(1, ZAMESTNANEC.getLogin_id());
                dotaz.setString(2, ZAMESTNANEC.getJmeno());
            } else {
                dotaz.setString(1, employe.getLogin_id());
                dotaz.setString(2, employe.getJmeno());
            }
            if (date == null) {
                dotaz.setDate(3, Date.valueOf(LocalDate.now()));
            } else {
                dotaz.setDate(3, Date.valueOf(date));
            }
            if (time == null) {
                dotaz.setTime(4, Time.valueOf(LocalTime.now()));
                if (in_out.equals(InOut_enum.DOV) || in_out.equals(InOut_enum.NEM) || in_out.equals(InOut_enum.PAR)) {
                    dotaz.setString(4, "00:00:00");
                }
            } else {
                dotaz.setString(4, time);
            }
            dotaz.setString(5, in_out);
            pridano = dotaz.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return pridano;
    }

    public static List<DAODochazka> getByLoginAndDate(DAOZamestnanec employe, String mesic, String rok) {
        List<DAODochazka> seznam = new ArrayList<>();
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id=? AND month(date) = ? AND year(date) = ?");
            dotaz.setString(1, employe.getLogin_id());
            dotaz.setString(2, mesic);
            dotaz.setString(3, rok);
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                DAODochazka dochazkaTable = new DAODochazka(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return seznam;
    }

    public static List<DAODochazka> getThisMonth() {
        List<DAODochazka> seznam = new ArrayList<>();
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id = ? AND month(date)=? AND year(date)=? ORDER BY date,time ASC");
            dotaz.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            dotaz.setString(2, String.valueOf(LocalDate.now().getMonthValue()));
            dotaz.setString(3, String.valueOf(LocalDate.now().getYear()));
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                DAODochazka dochazkaTable = new DAODochazka(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return seznam;
    }

    public static List<DAODochazka> getLastMonth() {
        List<DAODochazka> seznam = new ArrayList<>();
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id = ? AND month(date)=? AND year(date)=? ORDER BY date,time ASC");
            dotaz.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            dotaz.setString(2, String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()));
            dotaz.setString(3, String.valueOf(LocalDate.now().minusMonths(1).getYear()));
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                DAODochazka dochazkaTable = new DAODochazka(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return seznam;
    }

    public static DAODochazka getLastRecord() {
        DAODochazka poslZaznam = null;
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id=? AND (in_out = 'IN' OR in_out='OUT') ORDER BY date AND time DESC LIMIT 1");
            dotaz.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            ResultSet vysledky = dotaz.executeQuery();
            if (vysledky.next()) {
                poslZaznam = new DAODochazka(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return poslZaznam;
    }

    public static List<DAODochazka> getThisMonthWithCondition(String... in_out) {
        List<DAODochazka> seznam = new ArrayList<>();
        try {
            String sql = "SELECT * FROM dochazka WHERE login_id = ? AND month(date)= ? AND year(date)= ? AND in_out = ? ORDER BY date,time ASC";
            StringBuilder sb = new StringBuilder(sql);
            if (in_out.length > 1) {
                for (int i = 0; i < in_out.length - 1; i++) {
                    sb.insert(sql.lastIndexOf("ORDER BY"), "OR in_out = '" + in_out[i + 1] + "' ");
                }
            }
            PreparedStatement dotaz = spojeni.prepareStatement(sb.toString());
            dotaz.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            dotaz.setString(2, String.valueOf(LocalDate.now().getMonthValue()));
            dotaz.setString(3, String.valueOf(LocalDate.now().getYear()));
            dotaz.setString(4, in_out[0]);
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                DAODochazka dochazkaTable = new DAODochazka(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return seznam;
    }

    public static boolean deleteWithCondition(String in_out) {
        boolean provedeno = true;
        try {
            PreparedStatement dotazSmazat = spojeni.prepareStatement("DELETE FROM dochazka WHERE login_id = ? AND month(date) = ? AND year(date)= ? AND in_out = ?");
            dotazSmazat.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            dotazSmazat.setString(2, String.valueOf(LocalDate.now().getMonthValue()));
            dotazSmazat.setString(3, String.valueOf(LocalDate.now().getYear()));
            dotazSmazat.setString(4, in_out);
            dotazSmazat.executeUpdate();
        } catch (SQLException ex) {
            provedeno = false;
            System.err.println(ex.getMessage());
        }
        return provedeno;
    }

    public static Duration spocitejOdpracovano(final List<DAODochazka> seznam) {
        LocalTime prichod = null;
        Duration odpracovano = Duration.ZERO;
        for (DAODochazka zaznam : seznam) {
            switch (zaznam.getIn_out()) {
                case InOut_enum.IN:
                    prichod = LocalTime.parse(zaznam.getTime());
                    break;
                case InOut_enum.OUT:
                    LocalTime odchod = LocalTime.parse(zaznam.getTime());
                    odpracovano = odpracovano.plus(Duration.between(prichod, odchod));
                    break;
            }
        }
        return odpracovano;
    }
}
