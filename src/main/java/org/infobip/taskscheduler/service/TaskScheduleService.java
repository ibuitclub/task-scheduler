package org.infobip.taskscheduler.service;

import org.infobip.taskscheduler.model.ScheduleStatus;

public interface TaskScheduleService {

	ScheduleStatus schedule(int taskId);

	ScheduleStatus cancel(int taskId);

	boolean scheduled(int taskId);

}