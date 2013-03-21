/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.processmanager.persistence;

import cz.nkp.urnnbn.processmanager.core.Process;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleStateException;

/**
 *
 * @author Martin Řehánek
 */
public class ProcessDAOImpl extends AbstractDAO implements ProcessDAO {

    private static final Logger logger = Logger.getLogger(ProcessDAOImpl.class.getName());
    private static ProcessDAOImpl instance = null;

    public static ProcessDAO instanceOf() {
        if (instance == null) {
            logger.log(Level.INFO, "instantiating {0}", ProcessDAOImpl.class.getName());
            instance = new ProcessDAOImpl();
        }
        return instance;
    }

    public Process saveProcess(Process newProcess) {
        Session session = factory.openSession();
        session.beginTransaction();
        session.save(newProcess);
        session.getTransaction().commit();
        session.close();
        //logger.log(Level.INFO, "saved {0}", newProcess);
        return newProcess;
    }

    public Process getProcess(Long processId) throws UnknownRecordException {
        Session session = factory.openSession();
        session.beginTransaction();
        Process result = (Process) session.get(Process.class, processId);
        session.getTransaction().commit();
        session.close();
        if (result != null) {
            //logger.log(Level.INFO, "fetched {0}", result);
            return result;
        } else {
            throw new UnknownRecordException(Process.class.getName() + " with id " + processId);
        }
    }

    public List<Process> getProcesses() {
        Session session = factory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Process");
        List<Process> results = query.list();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public List<Process> getProcessesByState(ProcessState state) {
        Session session = factory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Process where pState = :state");
        query.setParameter("state", state.toString());
        List<Process> results = query.list();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public List<Process> getProcessesScheduledAfter(Date date) {
        Session session = factory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Process where scheduled > :date");
        query.setParameter("date", date);
        List<Process> results = query.list();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public List<Process> getProcessesOfUser(String userLogin) {
        Session session = factory.openSession();
        session.beginTransaction();
        //Query query = session.createQuery("select p from Process p where p.");
        Query query = session.createQuery("from Process where ownerLogin = :login");
        query.setParameter("login", userLogin);
        List<Process> results = query.list();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public List<Process> getProcessesOfUserScheduledAfter(String userLogin, Date date) {
        Session session = factory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Process where scheduled > :date and ownerLogin = :login");
        query.setParameter("date", date);
        query.setParameter("login", userLogin);
        List<Process> results = query.list();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public void updateProcess(Process process) throws UnknownRecordException {
        Session session = factory.openSession();
        try {
            session.beginTransaction();
            session.update(process);
            session.getTransaction().commit();
            //logger.log(Level.INFO, "updated {0}", process);
        } catch (StaleStateException ex) {
            logger.log(Level.WARNING, "trying to update non-existing process {0}", process);
            session.getTransaction().rollback();
            throw new UnknownRecordException(ex);
        } finally {
            session.close();
        }
    }

    public void deleteProcess(Process process) throws UnknownRecordException {
        Session session = factory.openSession();
        try {
            session.beginTransaction();
            session.delete(process);
            session.getTransaction().commit();
            //logger.log(Level.INFO, "deleted {0}", process);
        } catch (StaleStateException ex) {
            logger.log(Level.WARNING, "trying to delete non-existing process {0}", process);
            session.getTransaction().rollback();
            throw new UnknownRecordException(ex);
        } finally {
            session.close();
        }
    }
}
