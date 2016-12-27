package cz.benes.database.dao;

import cz.benes.database.domain.Employee;
import org.sql2o.Connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAOImpl extends AbstractDAO implements EmployeeDAO {

    @Override
    protected void init() {
        super.init();
        Map<String, String> colMaps = new HashMap<String,String>();
        colMaps.put("prava", "admin");
        sql2o.setDefaultColumnMappings(colMaps);
    }

    @Override
    public List<Employee> getAll() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM zamestnanci")
                    .executeAndFetch(Employee.class);
        }
    }

    @Override
    public Employee getByID(String login_id) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM zamestnanci WHERE login_id = :login_id")
                    .addParameter("login_id", login_id)
                    .executeAndFetchFirst(Employee.class);
        }
    }

    @Override
    public int delete(String login_id) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("DELETE FROM zamestnanci WHERE login_id = :login_id")
                    .addParameter("login_id", login_id)
                    .executeUpdate()
                    .getResult();
        }
    }

    @Override
    public int insert(Employee zamestnanec) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("INSERT INTO zamestnanci (jmeno, login_id, heslo, prava, uvazek) VALUES (:jmeno, :login_id, :heslo, :prava, :uvazek)")
                    .addParameter("jmeno", zamestnanec.getJmeno())
                    .addParameter("login_id", zamestnanec.getLogin_id())
                    .addParameter("heslo", zamestnanec.getHeslo())
                    .addParameter("prava", zamestnanec.getAdmin() ? "1" : "0")
                    .addParameter("uvazek", String.valueOf(zamestnanec.getUvazek()))
                    .executeUpdate().getResult();
        }
    }

    @Override
    public int update(Employee zamestnanec) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("UPDATE zamestnanci SET jmeno= :jmeno, heslo= :heslo, prava= :prava, uvazek= :uvazek WHERE login_id = :login_id")
                    .addParameter("jmeno", zamestnanec.getJmeno())
                    .addParameter("heslo", zamestnanec.getHeslo())
                    .addParameter("prava", zamestnanec.getAdmin() ? "1" : "0")
                    .addParameter("uvazek", String.valueOf(zamestnanec.getUvazek()))
                    .addParameter("login_id", zamestnanec.getLogin_id())
                    .executeUpdate().getResult();
        }
    }

}
