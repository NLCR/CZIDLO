package cz.nkp.urnnbn.server.services.logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class AdminLogsQueue {

    private volatile CircularFifoBuffer fifo;
    private volatile Date lastChanged = new Date();

    public AdminLogsQueue(int capacity) {
        fifo = new CircularFifoBuffer(capacity);
    }

    private synchronized CircularFifoBuffer getQueue() {
        return fifo;
    }

    public void addLine(String line) {
        getQueue().add(line);
        lastChanged = new Date();
        // System.err.println("inserting \"" + line + "\", size: " +
        // getQueue().size());
    }

    public List<String> getQueueContentInverted() {
        Object[] queueAsArray = getQueue().toArray();
        List<String> result = new ArrayList<String>(queueAsArray.length);
        for (int i = queueAsArray.length - 1; i >= 0; i--) {
            result.add(queueAsArray[i].toString());
        }
        return result;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

}
