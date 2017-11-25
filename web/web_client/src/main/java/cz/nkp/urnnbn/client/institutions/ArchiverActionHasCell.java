package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

/**
 * Created by Martin Řehánek on 25.11.17.
 */
public class ArchiverActionHasCell implements HasCell<ArchiverDTO, ArchiverDTO> {

    private ActionCell<ArchiverDTO> cell;

    public ArchiverActionHasCell(String text, ActionCell.Delegate<ArchiverDTO> delegate) {
        cell = new ActionCell<ArchiverDTO>(text, delegate);
    }

    @Override
    public Cell<ArchiverDTO> getCell() {
        return cell;
    }

    @Override
    public FieldUpdater<ArchiverDTO, ArchiverDTO> getFieldUpdater() {
        return null;
    }

    @Override
    public ArchiverDTO getValue(ArchiverDTO registrarDTO) {
        return registrarDTO;
    }
}
