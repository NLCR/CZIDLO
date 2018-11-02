package cz.nkp.urnnbn.client.processes;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

public class TransformationFormater extends AbstractFormater {

    private final XmlTransformationDTO transformation;
    private final ConstantsImpl constants;

    public TransformationFormater(XmlTransformationDTO transformation, ConstantsImpl constants) {
        this.transformation = transformation;
        this.constants = constants;
    }

    String getCreated() {
        return formatDateTime(transformation.getCreated());
    }

}


