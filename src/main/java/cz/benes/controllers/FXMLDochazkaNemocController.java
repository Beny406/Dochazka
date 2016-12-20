package cz.benes.controllers;

import cz.benes.domain.CheckListDen;
import cz.benes.domain.AttendanceRecord;
import cz.benes.domain.RecordType;
import cz.benes.managers.db.AttendanceDAO;
import cz.benes.managers.db.HolidaysDAO;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.controlsfx.control.CheckListView;

public class FXMLDochazkaNemocController implements Initializable {

    ObservableList<CheckListDen> data = FXCollections.observableArrayList();
    List<LocalDate> nemoc = new ArrayList<>();;
    List<LocalDate> obsazeno = new ArrayList<>();;
    private CheckListView<CheckListDen> checkList = new CheckListView<>();
    
    @FXML
    private Pane pane;

    @FXML
    void handleOKButton(ActionEvent event) {           
        if (AttendanceDAO.deleteWithCondition(RecordType.NEM)){
            ObservableList<CheckListDen> vyber = checkList.getCheckModel().getCheckedItems();
            for (CheckListDen d : vyber){
                AttendanceDAO.insert(null, d.getLocalDate().toString(), null, RecordType.NEM);
            }
            ((Node)(event.getSource())).getScene().getWindow().hide();         
        }
    }

    @FXML
    void handleZpetButton(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<AttendanceRecord> nem_tentoMesic = AttendanceDAO.getThisMonthWithCondition(RecordType.NEM);
        nem_tentoMesic.forEach(e -> nemoc.add(LocalDate.parse(e.getDate())));
        
        List<AttendanceRecord> dov_par_tentoMesic = AttendanceDAO.getThisMonthWithCondition(RecordType.DOV, RecordType.PAR);
        dov_par_tentoMesic.forEach(e -> obsazeno.add(LocalDate.parse(e.getDate())));
        
        int pocetDniMesice = LocalDate.now().getMonth().length(LocalDate.now().isLeapYear());
        for (int i = 1; i <= pocetDniMesice ; i++){
            LocalDate denMesice = LocalDate.now().withDayOfMonth(i);
            CheckListDen den = new CheckListDen(denMesice, false);
            data.add(den);
        }
        
        checkList.setPrefWidth(150);
        pane.getChildren().add(checkList);
        pane.setPrefWidth(150);
        checkList.setItems(data);
        checkList.setCellFactory(new Callback<ListView<CheckListDen>, ListCell<CheckListDen>>(){
            @Override
            public ListCell<CheckListDen> call(ListView<CheckListDen> param) {
                ListCell<CheckListDen> den = new CheckBoxListCell<CheckListDen>(CheckListDen::onProperty){
                    @Override public void updateItem(CheckListDen item, boolean empty){
                        super.updateItem(item, empty);
                        if (item != null){
                            setDisable(false);
                            item.onProperty().addListener((obs, oldValue, newValue) -> {
                                if (newValue) checkList.getCheckModel().check(item);
                                else checkList.getCheckModel().clearCheck(item);});
                                if(item.getLocalDate().getDayOfWeek() == DayOfWeek.SATURDAY 
                                        || item.getLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY 
                                        || HolidaysDAO.ALL.stream().anyMatch(svatek -> svatek.equals(item.getLocalDate()))){
                                    setDisable(true);
                                }
                                if(obsazeno.stream().anyMatch(obsazeno -> obsazeno.equals(item.getLocalDate()))){
                                    setDisable(true);
                                    setText("obsazeno");
                                }
                                if (nemoc.stream().anyMatch(denDovolene -> denDovolene.equals(item.getLocalDate()))){
                                    item.setOn(true);
                                }
                        }
                    }
                };
            return den;    
            }
        });
        
    }
}
