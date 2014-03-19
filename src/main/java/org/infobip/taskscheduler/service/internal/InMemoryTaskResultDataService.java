package org.infobip.taskscheduler.service.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.infobip.taskscheduler.model.TaskResult;
import org.infobip.taskscheduler.service.TaskResultDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * In-memory data service and storage for {@link TaskResult} objects.
 * <p>
 * <b>Note</b>: For development only.
 */
public final class InMemoryTaskResultDataService implements TaskResultDataService {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(InMemoryTaskResultDataService.class);

	private final Random idGenerator = new Random();

	private final ConcurrentMap<Integer, TaskResult> data = 
			new ConcurrentHashMap<Integer, TaskResult>();

	public InMemoryTaskResultDataService() {}

	private static void log(String message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(message);
		}		
	}

	/** Note: For internal use only. */
	@Override
	public TaskResult insert(TaskResult taskResult) {
		log("Insert " + taskResult);
		taskResult.setId(idGenerator.nextInt(Integer.MAX_VALUE));
		data.put(taskResult.getId(), taskResult);
		return taskResult;
	}

	/** 
	 * Note: Unsupported.
	 * @throws UnsupportedOperationException 
	 */
	@Override
	public TaskResult update(TaskResult taskResult) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TaskResult delete(Integer id) {
		log("Delete task result by id=" + id);
		return data.remove(id);
	}

	@Override
	public void delete(int taskId) {
		log("Delete task results by taskId=" + taskId);
		for (TaskResult taskResult : data.values()) {
			if (taskResult.getTaskId() == taskId) {
				data.remove(taskResult.getId());
			}
		}
	}

	@Override
	public List<TaskResult> select() {
		log("Select all task results");
		return new ArrayList<TaskResult>(data.values());
	}

	@Override
	public TaskResult select(Integer id) {
		log("Select task result by id=" + id);
		return data.get(id);
	}

	@Override
	public List<TaskResult> select(int offset, int limit) {
		if (offset < 0 || limit <= 0) {
			log("Select n/a for offset=" + offset + ", limit=" + limit);
			return new ArrayList<TaskResult>(0);
		}
		log("Select task results with offset=" + offset + ", limit=" + limit);
		int toIndex = offset + limit;
		if (toIndex > data.size()) {
			toIndex = data.size();
		}
		return select().subList(offset, toIndex);
	}

	@Override
	public List<TaskResult> select(int offset, int limit, int taskId) {
		if (offset < 0 || limit <= 0) {
			log("Select n/a by taskId=" + taskId + " for offset=" + offset + ", limit=" + limit);
			return new ArrayList<TaskResult>(0);
		}
		List<TaskResult> list = new ArrayList<TaskResult>();
		for (TaskResult taskResult : data.values()) {
			if (taskResult.getTaskId() == taskId) {
				list.add(taskResult);
			}
		}
		if (offset > list.size()) {
			log("Select n/a by taskId=" + taskId + " for offset=" + offset + ", limit=" + limit);
			return new ArrayList<TaskResult>(0);
		}
		int toIndex = offset + limit;
		if (toIndex > list.size()) {
			toIndex = list.size();
		}
		log("Select task results by taskId=" + taskId + " with offset=" + offset + ", limit=" + limit);
		return list.subList(offset, toIndex);
	}

	@Override
	public int count() {
		log("Count task results");
		return data.size();
	}

	@Override
	public int count(int taskId) {
		log("Count task results by taskId=" + taskId);
		int count = 0;
		for (TaskResult taskResult : data.values()) {
			if (taskResult.getTaskId() == taskId) {
				count++;
			}
		}
		return count;
	}

}