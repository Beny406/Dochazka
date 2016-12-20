package cz.benes.controllers;

import cz.benes.beans.CheckListDen;
import cz.benes.beans.DAODochazka;
import cz.benes.beans.InOut_enum;
import cz.benes.managers.db.Dochazka;
import cz.benes.managers.db.Svatky;
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
        if (Dochazka.deleteWithCondition(InOut_enum.NEM)){
            ObservableList<CheckListDen> vyber = checkList.getCheckModel().getCheckedItems();
            for (CheckListDen d : vyber){
                Dochazka.insert(null, d.getLocalDate().toString(), null, InOut_enum.NEM);
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
        List<DAODochazka> nem_tentoMesic = Dochazka.getThisMonthWithCondition(InOut_enum.NEM);
        nem_tentoMesic.forEach(e -> nemoc.add(LocalDate.parse(e.getDate())));
        
        List<DAODochazka> dov_par_tentoMesic = Dochazka.getThisMonthWithCondition(InOut_enum.DOV, InOut_enum.PAR);
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
                                        || Svatky.ALL.stream().anyMatch(svatek -> svatek.equals(item.getLocalDate()))){
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