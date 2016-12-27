package cz.benes.controllers.requesttimeoff;

import cz.benes.database.domain.RecordType;

public class FXMLDochazkaParagrafController extends AbstractTimeOffController{

    protected RecordType getRecordType() {
        return RecordType.PAR;
    }

    protected Enum[] getOtherRecordTypes() {
        return new Enum[]{RecordType.DOV, RecordType.NEM};
    }
}
