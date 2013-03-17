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
package cz.nkp.urnnbn.processmanager.scheduler.jobs;

import cz.nkp.urnnbn.processmanager.conf.Configuration;
import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;

/**
 *
 * @author Martin Řehánek
 */
public class ProcessFileUtils {

    public static final String LOG_FILE_NAME = "process.log";

    public static File buildProcessDir(Long processId) {
        File jobsDir = Configuration.getJobsDir();
        return new File(jobsDir, processId.toString());
    }

    public static File buildProcessFile(Long processId, String fileName) {
        if (null == fileName || fileName.isEmpty()) {
            throw new IllegalStateException("incorrect filename " + fileName);
        }
        return new File(buildProcessDir(processId), fileName);
    }

    public static File buildLogFile(Long processId) {
        return buildProcessFile(processId, LOG_FILE_NAME);
    }

    public static File createWriteableProcessFile(Long processId, String filename) throws IOException {
        File file = buildProcessFile(processId, filename);
        createFile(file);
        return file;
    }

    private static void createFile(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (!parentDir.canWrite()) {
            throw new AccessControlException("no rights to create files in directory " + parentDir.getAbsolutePath());
        }
        file.createNewFile();
    }

    public static void initProcessDir(Long processId) {
        File rootDir = buildProcessDir(processId);
        System.err.println("initializing process " + rootDir.getAbsolutePath());
        if (rootDir.exists()) {
            //  System.err.println("process dir exists, deleting");
            File failedToDelete = deleteFileRecursive(rootDir);
            if (failedToDelete != null) {
                throw new IllegalStateException("cannot remove file " + failedToDelete.getAbsolutePath());
            }
        }
        boolean createdDir = rootDir.mkdir();
        if (!createdDir) {
            throw new IllegalStateException("cannot create directory " + rootDir.getAbsolutePath());
        }
    }

    /**
     *
     * @param rootFile
     * @return File that could not be deleted, null if everything has been
     * deleted correcty
     */
    private static File deleteFileRecursive(File rootFile) {
        if (rootFile.isDirectory()) {
            File[] children = rootFile.listFiles();
            for (int i = 0; i < children.length; i++) {
                File childFailed = deleteFileRecursive(children[i]);
                if (childFailed != null) {
                    return childFailed;
                }
            }
        }
        boolean rootDeleted = rootFile.delete();
        return rootDeleted ? null : rootFile;
    }

    /**
     * 
     * @param processId
     * @return File failed to delete
     */
    public static File deleteProcessDir(Long processId) {
        File processDir = buildProcessDir(processId);
       // System.err.println("deleting " + processDir.getAbsolutePath());
        return deleteFileRecursive(processDir);
    }
}
