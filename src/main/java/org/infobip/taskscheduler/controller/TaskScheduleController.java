package org.infobip.taskscheduler.controller;

import org.infobip.taskscheduler.model.ScheduleStatus;
import org.infobip.taskscheduler.service.TaskScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Wrapper for {@link TaskScheduleService}.
 */
@Controller
@RequestMapping(produces = "application/json", value = "api/schedule")
public final class TaskScheduleController {

	private final TaskScheduleService taskScheduleService;

	@Autowired
	public TaskScheduleController(TaskScheduleService taskScheduleService) {
		this.taskScheduleService = taskScheduleService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "{taskId}")
	public @ResponseBody boolean scheduled(@PathVariable int taskId) {
		return taskScheduleService.scheduled(taskId);
	}

	@RequestMapping(method = RequestMethod.POST, value = "{taskId}")
	public @ResponseBody ScheduleStatus schedule(@PathVariable int taskId) {
		return taskScheduleService.schedule(taskId);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{taskId}")
	public @ResponseBody ScheduleStatus cancel(@PathVariable int taskId) {
		return taskScheduleService.cancel(taskId);
	}

}