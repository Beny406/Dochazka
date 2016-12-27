package cz.benes.database.dao;

import com.google.inject.Inject;
import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.CheckableMonth;
import cz.benes.database.domain.Employee;
import cz.benes.database.domain.RecordType;
import javafx.collections.ObservableList;
import org.sql2o.Connection;
import org.sql2o.Query;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceDAOImpl extends AbstractDAO implements AttendanceDAO {

    @Inject
    Employee employee;

    @Override
    public boolean insert(Employee employe, String date, String time, Enum recordType) {
        try (Connection conn = sql2o.open()) {
            Query query = conn.createQuery("INSERT INTO dochazka (login_id, jmeno, date, time, type) VALUES (:login_id, :jmeno, :date, :time, :type)");
            if (employe == null) {
                query.addParameter("login_id", employee.getLogin_id());
                query.addParameter("jmeno", employee.getJmeno());
            } else {
                query.addParameter("login_id", employe.getLogin_id());
                query.addParameter("jmeno", employe.getJmeno());
            }
            if (date == null) {
                query.addParameter("date", Date.valueOf(LocalDate.now()));
            } else {
                query.addParameter("date", Date.valueOf(date));
            }
            if (time == null) {
                query.addParameter("time", Time.valueOf(LocalTime.now()));
                if (recordType.equals(RecordType.DOV) || recordType.equals(RecordType.NEM) || recordType.equals(RecordType.PAR)) {
                    query.addParameter("time", "00:00:00");
                }
            } else {
                query.addParameter("time", time);
            }
            query.addParameter("type", recordType.name());
            return query.executeUpdate().getResult() == 1;
        }
    }

    @Override
    public List<AttendanceRecord> getByLoginAndDate(Employee employe, String mesic, String rok) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM dochazka WHERE login_id= :login_id AND month(date) = :month AND year(date) = :year")
                    .addParameter("login_id", employe.getLogin_id())
                    .addParameter("month", mesic)
                    .addParameter("year", rok)
                    .executeAndFetch(AttendanceRecord.class);
        }
    }

    @Override
    public List<AttendanceRecord> getThisMonth() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM dochazka WHERE login_id = :login_id AND month(date)= :month AND year(date)= :year ORDER BY date,time ASC")
                    .addParameter("login_id", employee.getLogin_id())
                    .addParameter("month", String.valueOf(LocalDate.now().getMonthValue()))
                    .addParameter("year", String.valueOf(LocalDate.now().getYear()))
                    .executeAndFetch(AttendanceRecord.class);
        }
    }

    @Override
    public List<AttendanceRecord> getLastMonth() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM dochazka WHERE login_id = :login_id AND month(date)= :month AND year(date)= :year ORDER BY date,time ASC")
                    .addParameter("login_id", employee.getLogin_id())
                    .addParameter("month", String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()))
                    .addParameter("year", String.valueOf(LocalDate.now().minusMonths(1).getYear()))
                    .executeAndFetch(AttendanceRecord.class);
        }
    }

    @Override
    public AttendanceRecord getLastRecord() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM dochazka WHERE login_id= :login_id AND (type = 'IN' OR type='OUT') ORDER BY date,time DESC LIMIT 1")
                    .addParameter("login_id", employee.getLogin_id())
                    .executeAndFetchFirst(AttendanceRecord.class);
        }
    }

    @Override
    public List<AttendanceRecord> getThisMonthWithCondition(Enum... type) {
        String sql = "SELECT * FROM dochazka WHERE login_id = :login_id AND month(date)= :month AND year(date)= :year AND type = :type ORDER BY time,date ASC";
        StringBuilder sb = new StringBuilder(sql);
        if (type.length > 1) {
            for (int i = 0; i < type.length - 1; i++) {
                sb.insert(sql.lastIndexOf("ORDER BY"), "OR type = '" + type[i + 1] + "' ");
            }
        }
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sb.toString())
                    .addParameter("login_id", employee.getLogin_id())
                    .addParameter("month", String.valueOf(LocalDate.now().getMonthValue()))
                    .addParameter("year", String.valueOf(LocalDate.now().getYear()))
                    .addParameter("type", type[0].name())
                    .executeAndFetch(AttendanceRecord.class);
        }
    }

    @Override
    public boolean deleteWithCondition(Enum type) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("DELETE FROM dochazka WHERE login_id = :login_id AND month(date) = :month AND year(date)= :year AND type = :type")
                    .addParameter("login_id", employee.getLogin_id())
                    .addParameter("month", String.valueOf(LocalDate.now().getMonthValue()))
                    .addParameter("year", String.valueOf(LocalDate.now().getYear()))
                    .addParameter("type", type.name())
                    .executeUpdate().getResult() == 1;
        }
    }

    @Override
    public Duration countDurationWorked(List<AttendanceRecord> seznam) {
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

    @Override
    public int deleteRecords(ObservableList<CheckableMonth> checkedItems, String year) throws SQLException {
        int deletedRows = 0;
        try (Connection conn = sql2o.open()) {
            Query query = conn.createQuery("DELETE FROM dochazka WHERE year(date)= :year AND month(date)= :month");
            for (CheckableMonth month : checkedItems) {
                query.addParameter("year", year);
                query.addParameter("month", String.valueOf(month.getLocalDate().getMonth().getValue()));
                deletedRows += query.executeUpdate().getResult();
            }
            return deletedRows;
        }
    }
}
