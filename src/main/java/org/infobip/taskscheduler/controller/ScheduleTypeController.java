package org.infobip.taskscheduler.controller;

import org.infobip.taskscheduler.model.ScheduleType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Wrapper for {@link ScheduleType} options.
 */
@Controller
@RequestMapping(produces = "application/json", value = "api/task/scheduleType")
public final class ScheduleTypeController {

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ScheduleType[] select() {
		return ScheduleType.values();
	}

}