package cz.nkp.urnnbn.oaiadapter;

public class RecordResult {
	private final String urnnbn;
	private final DigitalDocumentStatus ddStatus;
	private final DigitalInstanceStatus diStatus;

	public RecordResult(String urnnbn, DigitalDocumentStatus ddStatus, DigitalInstanceStatus diStatus) {
		this.urnnbn = urnnbn;
		this.ddStatus = ddStatus;
		this.diStatus = diStatus;
	}

	public String getUrnnbn() {
		return urnnbn;
	}

	public DigitalInstanceStatus getDiStatus() {
		return diStatus;
	}

	public DigitalDocumentStatus getDdStatus() {
		return ddStatus;
	}

	public static enum DigitalInstanceStatus {
		IMPORTED, UPDATED, UNTOUCHED;
	}

	public static enum DigitalDocumentStatus {
		NOW_REGISTERED, ALREADY_REGISTERED, IGNORED
	}

}
