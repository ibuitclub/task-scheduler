package org.infobip.taskscheduler.controller;

import java.util.List;

import org.infobip.taskscheduler.model.Task;
import org.infobip.taskscheduler.service.TaskDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Wrapper for {@link TaskDataService}.
 */
@Controller
@RequestMapping(produces = "application/json", value = "api/task")
public final class TaskDataController {

	private final TaskDataService taskDataService;

	@Autowired
	public TaskDataController(TaskDataService taskDataService) {
		this.taskDataService = taskDataService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Task> select() {
		return taskDataService.select();
	}

//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody Page<Task> select(
//			@RequestParam(required = false, defaultValue = "0") int offset, 
//			@RequestParam(required = false, defaultValue = "0") int limit) {
//
//		if (limit <= 0) {
//			limit = TaskDataService.DEFAULT_LIMIT;
//		}
//		return new Page<Task>(count(), taskDataService.select(offset, limit));
//	}

	@RequestMapping(method = RequestMethod.GET, value = "{id}")
	public @ResponseBody Task select(@PathVariable int id) {
		return taskDataService.select(id);
	}

	@RequestMapping(method = RequestMethod.GET, value = "count")
	public @ResponseBody int count() {
		return taskDataService.count();
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody Task insert(@RequestBody Task task) {
		return taskDataService.insert(task);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json", value = "{id}")
	public @ResponseBody Task update(@PathVariable int id, @RequestBody Task task) {
		return taskDataService.update(task);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{id}")
	public @ResponseBody Task delete(@PathVariable int id) {
		return taskDataService.delete(id);
	}

}