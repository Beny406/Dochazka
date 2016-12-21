package cz.benes.controllers.timeoff;

import cz.benes.database.domain.RecordType;
import javafx.fxml.Initializable;

public class FXMLDochazkaDovolenaController extends AbstractTimeOffController implements Initializable {

    protected RecordType getRecordType() {
        return RecordType.DOV;
    }

    protected Enum[] getOtherRecordTypes() {
        return new Enum[]{RecordType.NEM, RecordType.PAR};
    }
}
    

