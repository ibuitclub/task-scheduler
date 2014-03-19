package org.infobip.taskscheduler.service.util;

import org.infobip.taskscheduler.model.Task;
import org.springframework.util.Assert;

/**
 * Task utilities.
 */
public final class Tasks {

	private Tasks() {}

	public static void validate(Task task, boolean isNew) {
		Assert.notNull(task, "Task cannot be null");
		Assert.isTrue(isNew || task.getId() > 0, "Task 'id' cannot be <= 0");
		Assert.hasLength(task.getTitle(), "Task 'title' cannot be null or empty");
		Assert.hasLength(task.getRequestType(), "Task 'requestType' cannot be null or empty");
		Assert.hasLength(task.getRequestUri(), "Task 'requestUri' cannot be null or empty");
		Assert.hasLength(task.getScheduleType(), "Task 'scheduleType' cannot be null or empty");
		Assert.hasLength(task.getSchedule(), "Task 'schedule' cannot be null or empty");

		if (isNew) {
			setDefaults(task);
		}
	}

	public static Task setDefaults(Task task) {
		task.setLastRun(0L);
		task.setNextRun(0L);
		task.setRunningNow(false);
		task.setScheduled(false);
		task.setServerId(null);
		return task;
	}

}