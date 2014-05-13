package ms.globalclass;

public class TomorrowWeatherVO {
	String low;
	String high;
	String icon;
	String condition;
	String week;

	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getIcon() {
		return icon;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public TomorrowWeatherVO(String low, String high, String icon,
			String condition) {
		super();
		this.low = low;
		this.high = high;
		this.icon = icon;
		this.condition = condition;
	}

	public TomorrowWeatherVO() {

	}
}
