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

import cz.nkp.urnnbn.processmanager.core.XmlTransformation;
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
public class XmlTransformationDAOImpl extends AbstractDAO implements XmlTransformationDAO {

    private static final Logger logger = Logger.getLogger(XmlTransformationDAOImpl.class.getName());
    private static XmlTransformationDAOImpl instance = null;

    public static XmlTransformationDAOImpl instanceOf() {
        if (instance == null) {
            logger.log(Level.INFO, "instantiating {0}", XmlTransformationDAOImpl.class.getName());
            instance = new XmlTransformationDAOImpl();
        }
        return instance;
    }

    public XmlTransformation saveTransformation(XmlTransformation newTransformation) {
        Session session = factory.openSession();
        session.beginTransaction();
        session.save(newTransformation);
        session.getTransaction().commit();
        session.close();
        //logger.log(Level.INFO, "saved {0}", newProcess);
        return newTransformation;
    }

    public XmlTransformation getTransformation(Long transformationId) throws UnknownRecordException {
        Session session = factory.openSession();
        session.beginTransaction();
        XmlTransformation result = (XmlTransformation) session.get(XmlTransformation.class, transformationId);
        session.getTransaction().commit();
        session.close();
        if (result != null) {
            //logger.log(Level.INFO, "fetched {0}", result);
            return result;
        } else {
            throw new UnknownRecordException(XmlTransformation.class.getName() + " with id " + transformationId);
        }
    }

    public List<XmlTransformation> getTransformations() {
        Session session = factory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from XmlTransformation");
        List<XmlTransformation> results = query.list();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public List<XmlTransformation> getTransformationsOfUser(String userLogin) {
        Session session = factory.openSession();
        session.beginTransaction();
        //Query query = session.createQuery("select p from Process p where p.");
        Query query = session.createQuery("from XmlTransformation where ownerLogin = :login");
        query.setParameter("login", userLogin);
        List<XmlTransformation> results = query.list();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public void deleteTransformation(XmlTransformation transformation) throws UnknownRecordException {
        Session session = factory.openSession();
        try {
            session.beginTransaction();
            session.delete(transformation);
            session.getTransaction().commit();
            //logger.log(Level.INFO, "deleted {0}", process);
        } catch (StaleStateException ex) {
            logger.log(Level.WARNING, "trying to delete non-existing transformation {0}", transformation);
            session.getTransaction().rollback();
            throw new UnknownRecordException(ex);
        } finally {
            session.close();
        }
    }
}
