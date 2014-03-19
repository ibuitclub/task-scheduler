package org.infobip.taskscheduler.service;

import java.util.List;

import org.infobip.taskscheduler.model.TaskResult;

/** 
 * {@link TaskResult} specific data service.
 */
public interface TaskResultDataService extends DataService<Integer, TaskResult> {

	/** Select multiple task results by {@code offset}, {@code limit} and {@code taskId}. */
	List<TaskResult> select(int offset, int limit, int taskId);

	/** Delete task results by {@code taskId}. */
	void delete(int taskId);

	/** Count task results by {@code taskId}. */
	int count(int taskId);

}