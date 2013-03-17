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
package cz.nkp.urnnbn.processmanager.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

/**
 *
 * @author Martin Řehánek
 */
public class MyTriggerListener implements TriggerListener {

    public String getName() {
        return MyTriggerListener.class.getName();
    }

    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        String key = trigger.getKey().toString();
        //String jobId = (String) context.getJobDetail().getJobDataMap().get("id");
        System.err.println("trigger " + key + " fired");
    }

    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        String key = trigger.getKey().toString();
        //String jobId = (String) context.getJobDetail().getJobDataMap().get("id");
        System.err.println("trigger " + key + " veto");
        //tady rozhodnu po spusteni triggeru nad jobDetail, jestli vetovat. Asi vzdycky false
        return false;
    }

    public void triggerMisfired(Trigger trigger) {
        String key = trigger.getKey().toString();
        //String jobId = (String) context.getJobDetail().getJobDataMap().get("id");
        System.err.println("trigger " + key + " misfiered");
    }

    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
        String key = trigger.getKey().toString();
        //String jobId = (String) context.getJobDetail().getJobDataMap().get("id");
        System.err.println("trigger " + key + " completed");
    }
}
