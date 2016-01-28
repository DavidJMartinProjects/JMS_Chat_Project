import java.io.Serializable;

public class Details implements Serializable {
	private static final long serialVersionUID = 1L;

	String userName;
	String timeStamp;
	String message;
	boolean online;

	public Details(String userName, String timeStamp, String message, boolean online) {
		this.userName = userName;
		this.timeStamp = timeStamp;
		this.message = message;
		this.online = online;
	}

	public boolean getOnline() {
		return online;
	}

	public void Online(boolean a) {
		this.online = a;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
