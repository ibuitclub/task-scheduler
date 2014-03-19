package org.infobip.taskscheduler.model;

import org.infobip.taskscheduler.model.Task;
import org.infobip.taskscheduler.service.TaskScheduleService;

/** 
 * Response context for {@link TaskScheduleService} actions. Action exceptions 
 * are appended to the {@link #getMessage()} property.
 * <p>
 * <b>Note</b>: Subject to change.
 */
public final class ScheduleStatus {

	private final String message;
	private final Task task;

	public ScheduleStatus(String message) {
		this(message, null);
	}

	public ScheduleStatus(String message, Task task) {
		this.message = message;
		this.task = task;
	}

	public String getMessage() {
		return message;
	}

	public Task getTask() {
		return task;
	}

	@Override
	public String toString() {
		return "ScheduleStatus [message=" + message + ", task=" + task + "]";
	}

}