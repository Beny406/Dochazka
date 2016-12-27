package cz.benes.database.dao;

import cz.benes.database.domain.Employee;

import java.util.List;

/**
 * Created by Beny on 23.12.2016.
 */
public interface EmployeeDAO {
    List<Employee> getAll();

    Employee getByID(String login_id);

    int delete(String login_id);

    int insert(Employee zamestnanec);

    int update(Employee zamestnanec);
}
