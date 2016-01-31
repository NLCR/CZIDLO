package cz.nkp.urnnbn.core.dto;

public class Statistic {

	private String registrarCode;
	private int year;
	private int month;
	private int volume;

	public String getRegistrarCode() {
		return registrarCode;
	}

	public void setRegistrarCode(String registrarCode) {
		this.registrarCode = registrarCode;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

}
