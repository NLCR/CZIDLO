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
package cz.nkp.urnnbn.processmanager;

import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.Process;
import java.util.Date;
import java.util.Random;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author Martin Řehánek
 */
public class Tests {

    public static void createSomeObjects(SessionFactory factory) {
        String login = "testCreate";
        Session session = factory.openSession();

        session.beginTransaction();
        session.save(getScheduledProcess(login));
        session.getTransaction().commit();

        session.beginTransaction();
        session.save(getStartedProcess(login));
        session.getTransaction().commit();

        session.beginTransaction();
        session.save(getFinishedProcess(login));
        session.getTransaction().commit();

        session.beginTransaction();
        session.save(getFailedProcess(login));
        session.getTransaction().commit();

        session.beginTransaction();
        session.save(getKilledProcess(login));
        session.getTransaction().commit();

        session.close();
    }

    public static void createAndUpdateObject(SessionFactory factory) {
        String login = "createAndUpdate";

        // create
        Process process = getScheduledProcess(login);
        System.out.println("before persistence: " + process);
        Session saveSession = factory.openSession();
        saveSession.beginTransaction();
        saveSession.save(process);
        saveSession.getTransaction().commit();
        saveSession.close();
        System.out.println("after persistence: " + process);

        // update
        process.setState(ProcessState.RUNNING);
        // process.setJobId(randomPositiveLong());
        process.setStarted(plusRandomSeconds(new Date()));
        Session updateSession = factory.openSession();
        updateSession.beginTransaction();
        updateSession.update(process);
        updateSession.getTransaction().commit();
        updateSession.close();
        System.out.println("after update: " + process);
    }

    public static void createAndDeleteObject(SessionFactory factory) {
        String login = "createAndDelete";

        // create
        Process process = getScheduledProcess(login);
        Session saveSession = factory.openSession();
        saveSession.beginTransaction();
        saveSession.save(process);
        saveSession.getTransaction().commit();
        saveSession.close();

        // delete
        process.setState(ProcessState.RUNNING);
        process.setStarted(plusRandomSeconds(new Date()));
        Session updateSession = factory.openSession();
        updateSession.beginTransaction();
        updateSession.delete(process);
        updateSession.getTransaction().commit();
        updateSession.close();
    }

    public static SessionFactory initFactory() {
        // return new AnnotationConfiguration().configure().buildSessionFactory();
        // return new Configuration().configure().buildSessionFactory();
        return new AnnotationConfiguration().configure().buildSessionFactory();
    }

    private static Process getScheduledProcess(String login) {
        Process process = new Process();
        process.setType(ProcessType.TEST);
        process.setOwnerLogin(login);
        // process.setJobId(randomPositiveLong());
        process.setScheduled(plusRandomSeconds(new Date()));
        process.setState(ProcessState.SCHEDULED);
        return process;
    }

    private static Process getStartedProcess(String login) {
        Process process = getScheduledProcess(login);
        // process.setJobId(randomPositiveLong());
        process.setStarted(plusFiveSeconds(process.getScheduled()));
        process.setState(ProcessState.RUNNING);
        return process;
    }

    private static Process getFinishedProcess(String login) {
        Process process = getStartedProcess(login);
        process.setFinished(plusFiveSeconds(process.getStarted()));
        process.setState(ProcessState.FINISHED);
        return process;
    }

    private static Process getFailedProcess(String login) {
        Process process = getStartedProcess(login);
        process.setFinished(plusFiveSeconds(process.getStarted()));
        process.setState(ProcessState.FAILED);
        // TODO: jak to bude opravdu s cas. znamkama?
        process.setFinished(null);
        return process;
    }

    private static Process getKilledProcess(String login) {
        Process process = getStartedProcess(login);
        process.setFinished(plusFiveSeconds(process.getStarted()));
        process.setState(ProcessState.KILLED);
        return process;
    }

    private static Long randomPositiveLong() {
        Random rand = new Random();
        long result = rand.nextLong();
        return result > 0 ? result : result * (-1);
    }

    private static Date plusFiveSeconds(Date date) {
        return plusSeconds(date, Long.valueOf(5000));
    }

    private static Date plusRandomSeconds(Date date) {
        Random rand = new Random();
        Long plus = rand.nextLong() % 5000;
        return new Date(date.getTime() + plus);
    }

    private static Date plusSeconds(Date date, Long seconds) {
        return new Date(date.getTime() + seconds);
    }
}
