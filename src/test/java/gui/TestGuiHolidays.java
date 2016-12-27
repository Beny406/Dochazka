/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import org.junit.Test;

import java.time.LocalDate;

import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;
/**
 *
 * @author PB
 */
public class TestGuiHolidays extends CommonGuiTest {

    @Override
    protected String getFxml() {
        return "/fxml/FXMLAdminSvatky.fxml";
    }
    
    @Test
    public void testPridat(){
        DatePicker datePicker = find("#datePicker");
        datePicker.setValue(LocalDate.of(LocalDate.now().getYear(), 12, 31));
        Button pridatButton = find("#pridatButton");
        click(pridatButton);
        verifyThat("#infoLabel", hasText("Záznam přidán."));
    }
        
    @Test
    public void testOdebrat(){    
        Button odebratButton = find("#odebratButton");
        ListView svatekListView = find("#svatekListView");
        svatekListView.getSelectionModel().selectLast();
        click(odebratButton);
        verifyThat("#infoLabel", hasText("Záznam odebrán."));
    }
}
