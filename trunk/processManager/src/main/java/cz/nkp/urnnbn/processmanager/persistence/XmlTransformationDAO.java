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
import cz.nkp.urnnbn.processmanager.core.XmlTransformationType;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface XmlTransformationDAO {

    public XmlTransformation saveTransformation(XmlTransformation newTransformation);

    public XmlTransformation getTransformation(Long transformationId) throws UnknownRecordException;

    public List<XmlTransformation> getTransformations();

    public List<XmlTransformation> getTransformationsOfUser(String userLogin);

    public List<XmlTransformation> getTransformationsOfUserAndByType(String userLogin, XmlTransformationType type);

    public void deleteTransformation(XmlTransformation transformation) throws UnknownRecordException;
    
    public void deleteTransformation(Long transformationId);
}
