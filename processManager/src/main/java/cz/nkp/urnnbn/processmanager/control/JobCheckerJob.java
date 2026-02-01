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
package cz.nkp.urnnbn.processmanager.control;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Martin Řehánek
 */
@DisallowConcurrentExecution
public class JobCheckerJob implements Job {

    public static final String JOB_NAME = "job-checker";
    public static final String PM_KEY = "processManager";

    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object o = context.getMergedJobDataMap().get(PM_KEY);
        if (!(o instanceof ProcessManagerImpl pm)) {
            throw new JobExecutionException("ProcessManager not provided in JobDataMap under key=" + PM_KEY);
        }
        pm.runScheduledProcessIfPossible();
    }
}
