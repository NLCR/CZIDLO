package cz.nkp.urnnbn.client.processes.oaiAdapterConfigPanel;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

/**
 * Created by Martin Řehánek on 28.8.18.
 */
public class TransformationButtonHasCell implements HasCell<XmlTransformationDTO, XmlTransformationDTO> {

    private TransformationButtonCell cell;

    public TransformationButtonHasCell(TransformationButtonAction action) {
        cell = new TransformationButtonCell(action);
    }

    @Override
    public Cell<XmlTransformationDTO> getCell() {
        return cell;
    }

    @Override
    public FieldUpdater<XmlTransformationDTO, XmlTransformationDTO> getFieldUpdater() {
        return null;
    }

    @Override
    public XmlTransformationDTO getValue(XmlTransformationDTO transformation) {
        return transformation;
    }
}
