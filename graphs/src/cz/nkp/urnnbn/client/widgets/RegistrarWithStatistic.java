package cz.nkp.urnnbn.client.widgets;

public class RegistrarWithStatistic implements Comparable<RegistrarWithStatistic> {
	private final String code;
	private final Integer data;

	public RegistrarWithStatistic(String code, Integer data) {
		this.code = code;
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public Integer getData() {
		return data;
	}

	@Override
	public int compareTo(RegistrarWithStatistic other) {
		// decreasing order
		return -data.compareTo(other.data);
	}
}
