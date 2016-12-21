package cz.benes.controllers.timeoff;

import cz.benes.database.dao.AttendanceDAO;
import cz.benes.database.domain.AttendanceRecord;
import cz.benes.database.domain.CheckableDay;
import cz.benes.database.domain.RecordType;
import cz.benes.managers.WindowManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.controlsfx.control.CheckListView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public abstract class AbstractTimeOffController implements Initializable {

    protected CheckListView<CheckableDay> checkList = new CheckListView<>();

    protected ObservableList<CheckableDay> data = FXCollections.observableArrayList();

    protected List<LocalDate> occupied = new ArrayList<>();

    List<LocalDate> resolvedType = new ArrayList<>();

    abstract protected RecordType getRecordType();

    abstract protected Enum[] getOtherRecordTypes();

    @FXML
    protected Pane pane;

    @FXML
    void handleZpetButton(ActionEvent event) {
        WindowManager.hideNode(event);
    }

    @FXML
    void handleOKButton(ActionEvent event) {
        if (AttendanceDAO.deleteWithCondition(getRecordType())){
            ObservableList<CheckableDay> vyber = checkList.getCheckModel().getCheckedItems();
            for (CheckableDay d : vyber){
                AttendanceDAO.insert(null, d.getLocalDate().toString(), null, getRecordType());
            }
            WindowManager.hideNode(event);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<AttendanceRecord> dov_tentoMesic = AttendanceDAO.getThisMonthWithCondition(getRecordType());
        dov_tentoMesic.forEach(e -> resolvedType.add(LocalDate.parse(e.getDate())));

        List<AttendanceRecord> nem_par_tentoMesic = AttendanceDAO.getThisMonthWithCondition(getOtherRecordTypes());
        nem_par_tentoMesic.forEach(e -> occupied.add(LocalDate.parse(e.getDate())));

        int pocetDniMesice = LocalDate.now().getMonth().length(LocalDate.now().isLeapYear());
        for (int i = 1; i <= pocetDniMesice; i++) {
            LocalDate denMesice = LocalDate.now().withDayOfMonth(i);
            CheckableDay den = new CheckableDay(denMesice, false);
            data.add(den);
        }

        checkList.setPrefWidth(150);
        pane.getChildren().add(checkList);
        pane.setPrefWidth(150);
        checkList.setItems(data);
        setCellFactory();
    }

    protected void setCellFactory() {
        checkList.setCellFactory(new Callback<ListView<CheckableDay>, ListCell<CheckableDay>>() {
            @Override
            public ListCell<CheckableDay> call(ListView<CheckableDay> param) {
                return new CheckBoxListCell<CheckableDay>(CheckableDay::onProperty) {
                    @Override
                    public void updateItem(CheckableDay item, boolean empty) {
                       super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        }
                        if (item != null) {
                            setDisable(false);
                            item.onProperty().addListener((obs, oldValue, newValue) -> {
                                if (newValue) checkList.getCheckModel().check(item);
                                else checkList.getCheckModel().clearCheck(item);
                            });
                            if (item.isWeekendOrHoliday()) {
                                setDisable(true);
                            }
                            if (item.in(occupied)) {
                                setDisable(true);
                                setText("occupied");
                            }
                            if (item.in(resolvedType)) {
                                item.setOn(true);
                            }
                        }
                    }
                };
            }
        });
    }

}
