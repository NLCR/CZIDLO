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
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import java.util.Date;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import org.hibernate.PropertyValueException;

/**
 *
 * @author Martin Řehánek
 */
public class ProcessDAOImplTest extends TestCase {

    private ProcessDAO dao;
    private LoginGenerator loginGenerator;

    public ProcessDAOImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dao = ProcessDAOImpl.instanceOf();
        // dao = new ProcessDAOImpl();
        loginGenerator = new LoginGenerator();
        List<Process> processes = dao.getProcesses();
        for (Process process : processes) {
            dao.deleteProcess(process);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private String randomParam(int maxParamSize, Random rand) {
        int size = rand.nextInt(maxParamSize) + 1;
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            char c = (char) (rand.nextInt(26) + 'a');
            result.append(c);
        }
        return result.toString();
    }

    private String[] randomParams() {
        return randomParms(10);
    }

    private String[] randomParms(int maxParams) {
        int maxParamSize = 100;
        Random rand = new Random();
        String[] result = new String[maxParams];
        for (int i = 0; i < result.length; i++) {
            result[i] = randomParam(maxParamSize, rand);
        }
        return result;
    }

    private Process buildProcess() {
        Process process = new Process();
        process.setOwnerLogin(loginGenerator.getUniqueLogin());
        process.setParams(randomParams());
        process.setStarted(new Date());
        process.setState(ProcessState.SCHEDULED);
        process.setType(ProcessType.TEST);
        return process;
    }

    private Process buildProcess(String login) {
        Process result = buildProcess();
        result.setOwnerLogin(login);
        return result;
    }

    private Process buildScheduledProcess() {
        Process result = buildProcess();
        result.setState(ProcessState.SCHEDULED);
        result.setScheduled(new Date());
        return result;
    }

    private Process buildScheduledProcess(String login) {
        Process result = buildScheduledProcess();
        result.setOwnerLogin(login);
        return result;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Test of saveProcess method, of class ProcessDAOImpl.
     */
    public void testSaveProcess() {
        Process saved = dao.saveProcess(buildProcess());
        assertNotNull(saved.getId());
    }

    public void testSaveProcessEmptyLogin() {
        Process process = buildProcess();
        process.setOwnerLogin(null);
        try {
            dao.saveProcess(process);
            fail();
        } catch (PropertyValueException e) {
            assertEquals(Process.class.getName(), e.getEntityName());
            assertEquals("ownerLogin", e.getPropertyName());
        }
    }

    public void testSaveProcessEmptyType() {
        Process process = buildProcess();
        process.setType(null);
        try {
            dao.saveProcess(process);
            fail();
        } catch (PropertyValueException e) {
            assertEquals(Process.class.getName(), e.getEntityName());
            assertEquals("type", e.getPropertyName());
        }
    }

    public void testSaveProcessEmptyState() {
        Process process = buildProcess();
        process.setState(null);
        try {
            dao.saveProcess(process);
            fail();
        } catch (PropertyValueException e) {
            assertEquals(Process.class.getName(), e.getEntityName());
            assertEquals("state", e.getPropertyName());
        }
    }

    /**
     * Test of getProcess method, of class ProcessDAOImpl.
     */
    public void testGetProcess() {
        Process saved = dao.saveProcess(buildProcess());
        try {
            Process fetched = dao.getProcess(saved.getId());
            assertNotNull(fetched);
            assertEquals(saved, fetched);
        } catch (UnknownRecordException ex) {
            fail();
        }
    }

    public void testGetDeletedProcess() {
        // insert
        Process saved = dao.saveProcess(buildProcess());
        // delete
        try {
            dao.deleteProcess(saved);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        // get deleted
        try {
            dao.getProcess(saved.getId());
            fail();
        } catch (UnknownRecordException ex) {
            // ok
        }
    }

    public void testGetUnknownProcess() {
        try {
            dao.getProcess(Long.MAX_VALUE);
            fail();
        } catch (UnknownRecordException ex) {
            // ok
        }
    }

    public void testGetProcesses() {
        // insert
        Process first = dao.saveProcess(buildProcess());
        Process second = dao.saveProcess(buildProcess());
        Process third = dao.saveProcess(buildProcess());
        // fetch
        List<Process> allProcesses = dao.getProcesses();
        assertEquals(3, allProcesses.size());
        assertTrue(allProcesses.contains(first));
        assertTrue(allProcesses.contains(second));
        assertTrue(allProcesses.contains(third));
    }

    public void testGetProcessesByState() {
        // insert
        Process scheduled1 = buildScheduledProcess();
        dao.saveProcess(scheduled1);
        Process scheduled2 = buildScheduledProcess();
        dao.saveProcess(scheduled2);
        Process running = buildProcess();
        running.setState(ProcessState.RUNNING);
        dao.saveProcess(running);
        // fetch
        List<Process> processesOfUser = dao.getProcessesByState(ProcessState.SCHEDULED);
        assertEquals(2, processesOfUser.size());
        assertTrue(processesOfUser.contains(scheduled1));
        assertTrue(processesOfUser.contains(scheduled2));
        assertFalse(processesOfUser.contains(running));
    }

    public void testGetProcessesScheduledAfterDate() {
        // insert
        Process before1 = dao.saveProcess(buildScheduledProcess());
        Process before2 = dao.saveProcess(buildScheduledProcess());
        Date date = new Date();
        sleep(1000);
        Process after1 = dao.saveProcess(buildScheduledProcess());
        Process after2 = dao.saveProcess(buildScheduledProcess());
        // fetch
        List<Process> processesOfUser = dao.getProcessesScheduledAfter(date);
        assertEquals(2, processesOfUser.size());
        assertFalse(processesOfUser.contains(before1));
        assertFalse(processesOfUser.contains(before2));
        assertTrue(processesOfUser.contains(after1));
        assertTrue(processesOfUser.contains(after2));
    }

    public void testGetProcessesOfUser() {
        String user = loginGenerator.getUniqueLogin();
        // insert
        Process first = buildProcess(user);
        dao.saveProcess(first);
        Process second = buildProcess(user);
        dao.saveProcess(second);
        Process ofOtherUser = dao.saveProcess(buildProcess());
        // fetch
        List<Process> processesOfUser = dao.getProcessesOfUser(user);
        assertEquals(2, processesOfUser.size());
        assertTrue(processesOfUser.contains(first));
        assertTrue(processesOfUser.contains(second));
        assertFalse(processesOfUser.contains(ofOtherUser));
    }

    public void testGetProcessesOfUserScheduledAfterDate() {
        String user1 = loginGenerator.getUniqueLogin();
        String user2 = loginGenerator.getUniqueLogin();
        // insert
        Process beforeUser1 = dao.saveProcess(buildScheduledProcess(user1));
        Process beforeUser2 = dao.saveProcess(buildScheduledProcess(user2));
        Date date = new Date();
        sleep(1000);
        Process afterUser1 = dao.saveProcess(buildScheduledProcess(user1));
        Process afterUser2 = dao.saveProcess(buildScheduledProcess(user2));
        // get
        List<Process> processesOfUser1 = dao.getProcessesOfUserScheduledAfter(user1, date);
        assertEquals(1, processesOfUser1.size());
        assertTrue(processesOfUser1.contains(afterUser1));

        List<Process> processesOfUser2 = dao.getProcessesOfUserScheduledAfter(user2, date);
        assertEquals(1, processesOfUser2.size());
        assertTrue(processesOfUser2.contains(afterUser2));
    }

    /**
     * Test of updateProcess method, of class ProcessDAOImpl.
     */
    public void testUpdateProcess() {
        // insert
        Process process = dao.saveProcess(buildProcess());
        // update
        ProcessState newState = process.getState() == ProcessState.SCHEDULED ? ProcessState.RUNNING : ProcessState.SCHEDULED;
        process.setState(newState);
        process.setScheduled(new Date());
        try {
            dao.updateProcess(process);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        // fetch
        try {
            Process fetched = dao.getProcess(process.getId());
            assertEquals(newState, fetched.getState());
            assertNotNull(fetched.getScheduled());
        } catch (UnknownRecordException ex) {
            fail();
        }
    }

    public void testUpdateDeletedProcess() {
        // insert
        Process process = dao.saveProcess(buildProcess());
        // delete
        try {
            dao.deleteProcess(process);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        // update
        ProcessState newState = process.getState() == ProcessState.SCHEDULED ? ProcessState.RUNNING : ProcessState.SCHEDULED;
        process.setState(newState);
        process.setScheduled(new Date());
        try {
            dao.updateProcess(process);
            fail();
        } catch (UnknownRecordException ex) {
            // ok
        }
    }

    /**
     * Test of deleteProcess method, of class ProcessDAOImpl.
     */
    public void testDeleteProcess() {
        // insert
        Process process = dao.saveProcess(buildProcess());
        // delete
        try {
            dao.deleteProcess(process);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        // fetch
        try {
            dao.getProcess(process.getId());
            fail();
        } catch (UnknownRecordException ex) {
            // ok
        }
    }

    public void testDeleteDeletedProcess() {
        // insert
        Process process = dao.saveProcess(buildProcess());
        // delete
        try {
            dao.deleteProcess(process);
        } catch (UnknownRecordException ex) {
            throw new RuntimeException(ex);
        }
        // delete again
        try {
            dao.deleteProcess(process);
            fail();
        } catch (UnknownRecordException ex) {
            // ok
        }
    }
}
