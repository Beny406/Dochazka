package cz.benes.controllers.timeoff;

import cz.benes.database.domain.RecordType;
import javafx.fxml.Initializable;

public class FXMLDochazkaNemocController extends AbstractTimeOffController implements Initializable {

    protected RecordType getRecordType() {
        return RecordType.NEM;
    }

    protected Enum[] getOtherRecordTypes() {
        return new Enum[]{RecordType.DOV, RecordType.PAR};
    }
}
