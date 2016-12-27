package cz.benes.database.dao;

import com.google.inject.Inject;
import cz.benes.CommonTest;
import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.CheckableMonth;
import cz.benes.database.domain.Employee;
import cz.benes.database.domain.RecordType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import junit.framework.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAOImplTest extends CommonTest {

    @Inject
    Employee employee;

    @Inject
    AttendanceDAO attendanceDAO;

    @Inject
    DBInitiator dbInitiator;

    @Test
    public void insert() throws Exception {
        Assert.assertTrue("", attendanceDAO.insert(employee, null, null, RecordType.IN));
    }

    @Test
    public void getByLoginAndDate() throws Exception {
        Assert.assertTrue(!attendanceDAO.getByLoginAndDate(employee, "12", "2016").isEmpty());

    }

    @Test
    public void getThisMonth() throws Exception {
        Assert.assertTrue(!attendanceDAO.getThisMonth().isEmpty());
    }

    @Test
    public void getLastMonth() throws Exception {
        Assert.assertTrue(!attendanceDAO.getLastMonth().isEmpty());

    }

    @Test
    public void getLastRecord() throws Exception {
        Assert.assertTrue(attendanceDAO.getLastRecord() != null);
    }

    @Test
    public void getThisMonthWithCondition() throws Exception {
        Assert.assertTrue(!attendanceDAO.getThisMonthWithCondition(RecordType.IN).isEmpty());
    }

    @Test
    public void deleteWithCondition() throws Exception {
        Assert.assertTrue(!attendanceDAO.deleteWithCondition(RecordType.IN));
    }

    @Test
    public void countDurationWorked() throws Exception {
        List<AttendanceRecord> thisMonthAttendance = attendanceDAO.getThisMonthWithCondition(RecordType.IN, RecordType.OUT);
        Duration duration = attendanceDAO.countDurationWorked(thisMonthAttendance);
        // TODO assert
    }

    @Test
    public void deleteRecords() throws Exception {
        ArrayList<CheckableMonth> checkableMonths = new ArrayList<>();
        checkableMonths.add(new CheckableMonth(LocalDate.now().withMonth(12), true));
        ObservableList<CheckableMonth> checkableMonths1 = FXCollections.observableArrayList(checkableMonths);
        Assert.assertTrue(attendanceDAO.deleteRecords(checkableMonths1, "2016") > 0);
    }
}
