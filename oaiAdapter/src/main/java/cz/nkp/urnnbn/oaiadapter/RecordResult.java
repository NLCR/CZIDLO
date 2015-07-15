package cz.nkp.urnnbn.oaiadapter;

public class RecordResult {
	private final String urnnbn;
	private final DigitalInstanceStatus diStatus;

	public RecordResult(String urnnbn, DigitalInstanceStatus diStatus) {
		this.urnnbn = urnnbn;
		this.diStatus = diStatus;
	}

	public String getUrnnbn() {
		return urnnbn;
	}

	public DigitalInstanceStatus getDiStatus() {
		return diStatus;
	}

}
