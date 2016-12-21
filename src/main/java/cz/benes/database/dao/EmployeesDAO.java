package cz.benes.database.dao;

import cz.benes.database.domain.Employee;
import cz.benes.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeesDAO {
    
    static {
        spojeni = DBConnection.getConnection();
    }
    private static final Connection spojeni;
    
    public static List<Employee> getAll(){
        List<Employee> seznam = new ArrayList<>();
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("SELECT * FROM zamestnanci");
            ResultSet vysledek = dotaz.executeQuery();
            while (vysledek.next()){
                seznam.add(new Employee(vysledek.getString("login_id"),
                                              vysledek.getString("jmeno"), 
                                              vysledek.getString("heslo"), 
                                              vysledek.getString("uvazek"), 
                                              vysledek.getString("prava")));
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return seznam;
    }
    
    public static Employee getByID(String login_id) {
        Employee zamestnanec = null;
        try {
            PreparedStatement dotazKontrola = spojeni.prepareStatement("SELECT * FROM zamestnanci WHERE login_id =?");
            dotazKontrola.setString(1, login_id);
            ResultSet vysledky = dotazKontrola.executeQuery();
            if (vysledky.next()) {   
                zamestnanec = new Employee(vysledky.getString("login_id"),
                                                 vysledky.getString("jmeno"), 
                                                 vysledky.getString("heslo"), 
                                                 vysledky.getString("uvazek"), 
                                                 vysledky.getString("prava"));
            }
        } catch(SQLException e){
            System.err.println("Chyba při výpisu výsledků " + e.getMessage());
        }
        return zamestnanec;
    }
    
    public static int delete(String login_id) {
        int smazano = 0;
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("DELETE FROM zamestnanci WHERE login_id = ?");
            dotaz.setString(1, login_id);
            smazano = dotaz.executeUpdate(); 
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return smazano;
    }
    
    public static int insert(Employee zamestnanec) {
        int smazano = 0;
        try {
            PreparedStatement dotazPridat = spojeni.prepareStatement("INSERT INTO zamestnanci (jmeno, login_id, heslo, prava, uvazek) VALUES (?, ?, ?, ?, ?)");
            dotazPridat.setString(1, zamestnanec.getJmeno());
            dotazPridat.setString(2, zamestnanec.getLogin_id());
            dotazPridat.setString(3, zamestnanec.getHeslo());
            dotazPridat.setString(4, zamestnanec.getAdmin() ? "1":"0");
            dotazPridat.setString(5, String.valueOf(zamestnanec.getUvazek()));
            smazano = dotazPridat.executeUpdate();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return smazano;
    } 
    
    public static int update(Employee zamestnanec) {
        int updated = 0;
        try {
            PreparedStatement dotaz = spojeni.prepareStatement("UPDATE zamestnanci SET jmeno=?, heslo=?, prava=?, uvazek=? WHERE login_id = ?");
            dotaz.setString(1, zamestnanec.getJmeno());
            dotaz.setString(2, zamestnanec.getHeslo());
            dotaz.setString(3, zamestnanec.getAdmin() ? "1":"0");
            dotaz.setString(4, String.valueOf(zamestnanec.getUvazek()));
            dotaz.setString(5, zamestnanec.getLogin_id());
            updated = dotaz.executeUpdate();         
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return updated;
    }    
    
}
