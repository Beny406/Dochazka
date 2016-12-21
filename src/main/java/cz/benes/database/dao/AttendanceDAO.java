package cz.benes.database.dao;

import cz.benes.controllers.FXMLDochazkaController;
import cz.benes.database.DBConnection;
import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.CheckableMonth;
import cz.benes.database.domain.Employee;
import cz.benes.database.domain.RecordType;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static cz.benes.controllers.FXMLDochazkaController.ZAMESTNANEC;

public class AttendanceDAO {

    static {
        spojeni = DBConnection.getConnection();
    }
    private static final Connection spojeni;

    public static int insert(Employee employe, String date, String time, Enum recordType) {
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
                if (recordType.equals(RecordType.DOV) || recordType.equals(RecordType.NEM) || recordType.equals(RecordType.PAR)) {
                    dotaz.setString(4, "00:00:00");
                }
            } else {
                dotaz.setString(4, time);
            }
            dotaz.setString(5, recordType.name());
            pridano = dotaz.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return pridano;
    }

    public static List<AttendanceRecord> getByLoginAndDate(Employee employe, String mesic, String rok) {
        List<AttendanceRecord> seznam = new ArrayList<>();
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id=? AND month(date) = ? AND year(date) = ?");
            dotaz.setString(1, employe.getLogin_id());
            dotaz.setString(2, mesic);
            dotaz.setString(3, rok);
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                AttendanceRecord dochazkaTable = new AttendanceRecord(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return seznam;
    }

    public static List<AttendanceRecord> getThisMonth() {
        List<AttendanceRecord> seznam = new ArrayList<>();
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id = ? AND month(date)=? AND year(date)=? ORDER BY date,time ASC");
            dotaz.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            dotaz.setString(2, String.valueOf(LocalDate.now().getMonthValue()));
            dotaz.setString(3, String.valueOf(LocalDate.now().getYear()));
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                AttendanceRecord dochazkaTable = new AttendanceRecord(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return seznam;
    }

    public static List<AttendanceRecord> getLastMonth() {
        List<AttendanceRecord> seznam = new ArrayList<>();
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id = ? AND month(date)=? AND year(date)=? ORDER BY date,time ASC");
            dotaz.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            dotaz.setString(2, String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()));
            dotaz.setString(3, String.valueOf(LocalDate.now().minusMonths(1).getYear()));
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                AttendanceRecord dochazkaTable = new AttendanceRecord(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return seznam;
    }

    public static AttendanceRecord getLastRecord() {
        AttendanceRecord poslZaznam = null;
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM dochazka WHERE login_id=? AND (in_out = 'IN' OR in_out='OUT') ORDER BY date AND time DESC LIMIT 1");
            dotaz.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            ResultSet vysledky = dotaz.executeQuery();
            if (vysledky.next()) {
                poslZaznam = new AttendanceRecord(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return poslZaznam;
    }

    public static List<AttendanceRecord> getThisMonthWithCondition(Enum... in_out) {
        List<AttendanceRecord> seznam = new ArrayList<>();
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
            dotaz.setString(4, in_out[0].name());
            ResultSet vysledky = dotaz.executeQuery();
            while (vysledky.next()) {
                AttendanceRecord dochazkaTable = new AttendanceRecord(vysledky.getString("date"), vysledky.getString("time"), vysledky.getString("in_out"));
                seznam.add(dochazkaTable);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return seznam;
    }

    public static boolean deleteWithCondition(Enum in_out) {
        boolean provedeno = true;
        try {
            PreparedStatement dotazSmazat = spojeni.prepareStatement("DELETE FROM dochazka WHERE login_id = ? AND month(date) = ? AND year(date)= ? AND in_out = ?");
            dotazSmazat.setString(1, FXMLDochazkaController.ZAMESTNANEC.getLogin_id());
            dotazSmazat.setString(2, String.valueOf(LocalDate.now().getMonthValue()));
            dotazSmazat.setString(3, String.valueOf(LocalDate.now().getYear()));
            dotazSmazat.setString(4, in_out.name());
            dotazSmazat.executeUpdate();
        } catch (SQLException ex) {
            provedeno = false;
            System.err.println(ex.getMessage());
        }
        return provedeno;
    }

    public static Duration spocitejOdpracovano(final List<AttendanceRecord> seznam) {
        LocalTime prichod = null;
        Duration odpracovano = Duration.ZERO;
        for (AttendanceRecord zaznam : seznam) {
            switch (RecordType.valueOf(zaznam.getType())) {
                case IN:
                    prichod = LocalTime.parse(zaznam.getTime());
                    break;
                case OUT:
                    LocalTime odchod = LocalTime.parse(zaznam.getTime());
                    odpracovano = odpracovano.plus(Duration.between(prichod, odchod));
                    break;
            }
        }
        return odpracovano;
    }

    public static int deleteRecords(ObservableList<CheckableMonth> checkedItems, String year) throws SQLException {
        int pocetSmazanychRadku = 0;
        PreparedStatement dotaz = spojeni.prepareStatement("DELETE FROM dochazka WHERE year(date)=? AND month(date)=?");
        for (CheckableMonth mesic : checkedItems) {
            dotaz.setString(1, year);
            dotaz.setString(2, String.valueOf(mesic.getLocalDate().getMonth().getValue()));
            pocetSmazanychRadku += dotaz.executeUpdate();
        }
        return pocetSmazanychRadku;
    }
}
