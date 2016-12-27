package cz.benes.services;

import cz.benes.database.domain.AttendanceRecord;
import net.sf.jasperreports.engine.JRException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public interface JasperService {

    void getReport(List<AttendanceRecord> zaznamyZaMesic, LocalDate datum, Class clazz) throws JRException, SQLException;

}

