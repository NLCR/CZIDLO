/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

import cz.nkp.urnnbn.core.ProcessType;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public interface ProcessService extends BusinessService {

    public Process planProces(String login, ProcessType processType) throws UnknownUserException, AccessException;

    public List<Process> getProcessesOfUser(String login) throws UnknownUserException, AccessException;

    public List<Process> getAllProcesses(String login) throws UnknownUserException, AccessException;

    //public Process getProcessById(Long id);
    public List<Process> getProcessesChangedSince(String login, DateTime since) throws UnknownUserException, AccessException;

    //TODO: ziskavani vystup procesu a dat specifickych pro konkretni proces
    //update operace bude delat sluzba interne (zmena stavu apod.)
    public void killProcess(String login, Long processId) throws UnknownUserException, AccessException;
}
