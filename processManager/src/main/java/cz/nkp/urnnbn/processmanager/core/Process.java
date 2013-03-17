/*
 * Copyright (C) 2012 Martin Řehánek
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
package cz.nkp.urnnbn.processmanager.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Martin Řehánek
 */
@Entity
@Table(name = "PROCESS")
public class Process implements Serializable {

    private Long id;
    private String ownerLogin;
    private ProcessState state;
    private ProcessType type;
    private Date scheduled;
    private Date started;
    private Date finished;
    private String[] params;

    public static Process buildScheduledProcess(String userLogin, ProcessType type, String[] processParams) {
        Process process = new Process();
        process.setOwnerLogin(userLogin);
        process.setType(type);
        process.setParams(processParams);
        process.setState(ProcessState.SCHEDULED);
        process.setScheduled(new Date());
        return process;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@SequenceGenerator(name="process_id_seq", sequenceName="process_id_seq", allocationSize=1)
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "scheduled", nullable = true)
    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "started", nullable = true)
    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "finished", nullable = true)
    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "pType", nullable = false)
    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "pState", nullable = false)
    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    @Column(name = "owner", nullable = false)
    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Process other = (Process) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Process{" + "id=" + id + ", ownerLogin=" + ownerLogin + ", state=" + state + ", type=" + type + ", scheduled=" + scheduled + ", started=" + started + ", finished=" + finished + '}';
    }
}
