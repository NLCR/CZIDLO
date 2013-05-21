package cz.nkp.urnnbn.server.services.logs;

import java.util.List;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import cz.nkp.urnnbn.client.services.LogsService;
import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.server.services.AbstractService;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class LogsServiceImpl extends AbstractService implements LogsService {

	private static final long serialVersionUID = -4193116571783640059L;
	private static final int ADMIN_LOGS_QUEUE_SIZE = 30;
	private volatile AdminLogsQueue queu;

	public LogsServiceImpl() {
		super();
		queu = new AdminLogsQueue(ADMIN_LOGS_QUEUE_SIZE);
		Tailer tailer = new Tailer(AdminLogger.getLogFile(), tailerListener(), 100, false);
		Thread thread = new Thread(tailer);
		thread.start();
	}

	private TailerListenerAdapter tailerListener() {
		return new TailerListenerAdapter() {
			private final AdminLogsQueue queue = LogsServiceImpl.this.queu;

			public void handle(String line) {
				queue.addLine(line);
			}
		};
	}

	@Override
	public long getAdminLogLastUpdatedTime() throws ServerException {
		try {
			checkUserIsAdmin();
			return queu.getLastChanged().getTime();
		} catch (Exception e) {
			throw new ServerException(e.getMessage());
		}
	}

	@Override
	public List<String> getAdminLogs() throws ServerException {
		try {
			checkUserIsAdmin();
			return queu.getQueueContentInverted();
		} catch (Exception e) {
			throw new ServerException(e.getMessage());
		}
	}

}
