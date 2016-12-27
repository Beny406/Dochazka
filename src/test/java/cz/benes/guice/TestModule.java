package cz.benes.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import cz.benes.database.dao.*;
import cz.benes.database.domain.Employee;
import cz.benes.services.JasperService;
import cz.benes.services.JasperServiceImpl;
import cz.benes.services.WindowService;
import cz.benes.services.WindowServiceImpl;

import java.io.IOException;
import java.util.Properties;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AttendanceDAO.class).to(AttendanceDAOImpl.class);
        bind(DBInitiator.class).to(DBInitiatorImpl.class);
        bind(EmployeeDAO.class).to(EmployeeDAOImpl.class);
        bind(HolidaysDAO.class).to(HolidaysDAOImpl.class);

        bind(WindowService.class).to(WindowServiceImpl.class);
        bind(JasperService.class).to(JasperServiceImpl.class);
        bind(Employee.class).toInstance(new Employee("1", "admin", "", "7.5", "1"));

        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("db.properties"));
        } catch (IOException e) {
            System.err.println("Error while loading properties: " + e);
        }
        Names.bindProperties(binder(), properties);
    }
}
