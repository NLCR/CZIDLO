package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

/**
 * Created by Martin Řehánek on 25.11.17.
 */
public class RegistrarActionHasCell implements HasCell<RegistrarDTO, RegistrarDTO> {

    private ActionCell<RegistrarDTO> cell;

    public RegistrarActionHasCell(String text, ActionCell.Delegate<RegistrarDTO> delegate) {
        cell = new ActionCell<RegistrarDTO>(text, delegate);
    }

    @Override
    public Cell<RegistrarDTO> getCell() {
        return cell;
    }

    @Override
    public FieldUpdater<RegistrarDTO, RegistrarDTO> getFieldUpdater() {
        return null;
    }

    @Override
    public RegistrarDTO getValue(RegistrarDTO registrarDTO) {
        return registrarDTO;
    }
}
