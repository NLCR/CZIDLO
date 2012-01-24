/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

/**
 *
 * @author Martin Řehánek
 */
public interface ProcessService extends BusinessService {

    public void planProces(Object process);

    public Object getProcessStatus();

    public Object getProcessLog();
}
