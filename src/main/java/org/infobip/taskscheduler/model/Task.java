package org.infobip.taskscheduler.model;

/**
 * <b>Note</b>: Subject to change.
 */
public final class Task {

	private int id;

	private String title;
	private String description;

	private String requestType;
	private String requestUri;
	private String requestHeader;
	private String requestBody;

	private String scheduleType;
	private String schedule;
	private boolean scheduled;

	private long lastRun;
	private long nextRun;
	private boolean runningNow;

	private String serverId;

	public Task() {}

	public Task(String title, String description, String requestType,
			String requestUri, String requestHeader, String requestBody, 
			String scheduleType, String schedule) {

		this.title = title;
		this.description = description;
		this.requestType = requestType;
		this.requestUri = requestUri;
		this.requestHeader = requestHeader;
		this.requestBody = requestBody;
		this.scheduleType = scheduleType;
		this.schedule = schedule;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(String requestHeader) {
		this.requestHeader = requestHeader;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public long getLastRun() {
		return lastRun;
	}

	public void setLastRun(long lastRun) {
		this.lastRun = lastRun;
	}

	public long getNextRun() {
		return nextRun;
	}

	public void setNextRun(long nextRun) {
		this.nextRun = nextRun;
	}

	public boolean isRunningNow() {
		return runningNow;
	}

	public void setRunningNow(boolean runningNow) {
		this.runningNow = runningNow;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((requestBody == null) ? 0 : requestBody.hashCode());
		result = prime * result
				+ ((requestHeader == null) ? 0 : requestHeader.hashCode());
		result = prime * result
				+ ((requestType == null) ? 0 : requestType.hashCode());
		result = prime * result
				+ ((requestUri == null) ? 0 : requestUri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (requestBody == null) {
			if (other.requestBody != null)
				return false;
		} else if (!requestBody.equals(other.requestBody))
			return false;
		if (requestHeader == null) {
			if (other.requestHeader != null)
				return false;
		} else if (!requestHeader.equals(other.requestHeader))
			return false;
		if (requestType == null) {
			if (other.requestType != null)
				return false;
		} else if (!requestType.equals(other.requestType))
			return false;
		if (requestUri == null) {
			if (other.requestUri != null)
				return false;
		} else if (!requestUri.equals(other.requestUri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", description="
				+ description + ", requestType=" + requestType
				+ ", requestUri=" + requestUri + ", requestHeader="
				+ requestHeader + ", requestBody=" + requestBody
				+ ", scheduleType=" + scheduleType + ", schedule=" + schedule
				+ ", scheduled=" + scheduled + ", lastRun=" + lastRun
				+ ", nextRun=" + nextRun + ", runningNow=" + runningNow
				+ ", serverId=" + serverId + "]";
	}

}