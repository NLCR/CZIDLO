package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;

/**
 * Created by Martin Řehánek on 28.8.18.
 */
public class ProcessButtonHasCell implements HasCell<ProcessDTO, ProcessDTO> {

    private ProcessButtonCell cell;

    public ProcessButtonHasCell(ProcessButtonAction action) {
        cell = new ProcessButtonCell(action);
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
