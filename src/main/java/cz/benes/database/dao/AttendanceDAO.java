package cz.benes.database.dao;

import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.CheckableMonth;
import cz.benes.database.domain.Employee;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

public interface AttendanceDAO {

    boolean insert(Employee employe, String date, String time, Enum recordType);

    List<AttendanceRecord> getByLoginAndDate(Employee employe, String mesic, String rok);

    List<AttendanceRecord> getThisMonth();

    List<AttendanceRecord> getLastMonth();

    AttendanceRecord getLastRecord();

    List<AttendanceRecord> getThisMonthWithCondition(Enum... type);

    boolean deleteWithCondition(Enum in_out);

    Duration countDurationWorked(List<AttendanceRecord> seznam);

    int deleteRecords(ObservableList<CheckableMonth> checkedItems, String year) throws SQLException;


}
