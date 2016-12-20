/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import org.junit.Test;
import static org.loadui.testfx.Assertions.verifyThat;
import org.loadui.testfx.GuiTest;
import static org.loadui.testfx.controls.Commons.hasText;
/**
 *
 * @author PB
 */
public class TestSvatky extends GuiTest {
    // jak zavolat přímo metody? - nebudu muset označovat jednotlivá tlačítka
    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("/fxml/FXMLAdminSvatky.fxml"));
            return parent;
        } catch (Exception e) {
            Logger.getLogger(TestSvatky.class.getName()).log(Level.SEVERE, null, e);
        }
        return parent;
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
