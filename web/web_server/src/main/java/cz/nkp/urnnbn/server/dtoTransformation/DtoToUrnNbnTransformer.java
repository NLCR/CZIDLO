package cz.nkp.urnnbn.server.dtoTransformation;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.UrnNbn.DocumentCode;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class DtoToUrnNbnTransformer {

    private final UrnNbnDTO original;

    public DtoToUrnNbnTransformer(UrnNbnDTO original) {
        this.original = original;
    }

    public UrnNbn transform() {
        return new UrnNbn(RegistrarCode.valueOf(original.getRegistrarCode()), DocumentCode.valueOf(original.getDocumentCode()),
                original.getDigdocId(), toDateTimeOrNull(original.getReserved()), original.getDeactivationNote());
    }

    private DateTime toDateTimeOrNull(String datetimeString) {
        return datetimeString == null ? null : DateTime.parse(datetimeString);
    }
}
