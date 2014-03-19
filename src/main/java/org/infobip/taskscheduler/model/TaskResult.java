package org.infobip.taskscheduler.model;

/** 
 * <b>Note</b>: Subject to change.
 */
public final class TaskResult {

	private int id;
	private int taskId;
	private String result;
	private long timestamp;

	public TaskResult() {}

	public TaskResult(int taskId, String result) {
		this(taskId, result, System.currentTimeMillis());
	}

	public TaskResult(int taskId, String result, long timestamp) {
		this.taskId = taskId;
		this.result = result;
		this.timestamp = timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + taskId;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		TaskResult other = (TaskResult) obj;
		if (id != other.id)
			return false;
		if (taskId != other.taskId)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaskResult [id=" + id + ", taskId=" + taskId + ", result="
				+ result + ", timestamp=" + timestamp + "]";
	}

}