package org.infobip.taskscheduler.controller;

import java.util.List;

import org.infobip.taskscheduler.model.TaskResult;
import org.infobip.taskscheduler.model.Page;
import org.infobip.taskscheduler.service.TaskResultDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/** 
 * Wrapper for {@link TaskResultDataService}.
 */
@Controller
@RequestMapping(produces = "application/json", value = "api/result")
public final class TaskResultDataController {

	private final TaskResultDataService taskResultDataService;

	@Autowired
	public TaskResultDataController(TaskResultDataService taskResultDataService) {
		this.taskResultDataService = taskResultDataService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Page<TaskResult> select(
			@RequestParam(required = false, defaultValue = "0") int offset, 
			@RequestParam(required = false, defaultValue = "0") int limit, 
			@RequestParam(required = false, defaultValue = "0") int taskId) {

		if (limit <= 0) {
			limit = TaskResultDataService.DEFAULT_LIMIT;
		}

		List<TaskResult> list = taskId > 0 
				? taskResultDataService.select(offset, limit, taskId) 
				: taskResultDataService.select(offset, limit);

		return new Page<TaskResult>(count(taskId), list);
	}

	@RequestMapping(method = RequestMethod.GET, value = "count")
	public @ResponseBody int count(
			@RequestParam(required = false, defaultValue = "0") int taskId) {

		return taskId > 0 
				? taskResultDataService.count(taskId) 
				: taskResultDataService.count();
	}

//	@RequestMapping(method = RequestMethod.GET, value = "{id}")
//	public @ResponseBody TaskResult select(@PathVariable int id) {
//		return taskResultDataService.select(id);
//	}

//	@RequestMapping(method = RequestMethod.DELETE, value = "{id}")
//	public @ResponseBody TaskResult delete(@PathVariable int id) {
//		return taskResultDataService.delete(id);
//	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{taskId}")
	public @ResponseBody void delete(@PathVariable int taskId) {
		taskResultDataService.delete(taskId);
	}

}