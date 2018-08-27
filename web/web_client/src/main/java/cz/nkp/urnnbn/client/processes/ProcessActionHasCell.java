package cz.nkp.urnnbn.client.processes;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;

public class ProcessActionHasCell implements HasCell<ProcessDTO, ProcessDTO> {

    private ActionCell<ProcessDTO> cell;

    public ProcessActionHasCell(String text, ActionCell.Delegate<ProcessDTO> delegate) {
        cell = new ActionCell<ProcessDTO>(text, delegate);
    }

    @Override
    public Cell<ProcessDTO> getCell() {
        return cell;
    }

    @Override
    public FieldUpdater<ProcessDTO, ProcessDTO> getFieldUpdater() {
        return null;
    }

    @Override
    public ProcessDTO getValue(ProcessDTO process) {
        return process;
    }
}
