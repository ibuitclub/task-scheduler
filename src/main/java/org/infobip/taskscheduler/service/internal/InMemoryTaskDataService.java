package org.infobip.taskscheduler.service.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.infobip.taskscheduler.model.Task;
import org.infobip.taskscheduler.model.ScheduleType;
import org.infobip.taskscheduler.service.TaskDataService;
import org.infobip.taskscheduler.service.util.Tasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * In-memory data service and storage for {@link Task} objects.
 * <p>
 * <b>Note</b>: For development only.
 */
public final class InMemoryTaskDataService implements TaskDataService {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(InMemoryTaskDataService.class);

	private final Random idGenerator = new Random();

	private final ConcurrentMap<Integer, Task> data = 
			new ConcurrentHashMap<Integer, Task>();

	public InMemoryTaskDataService() {}

	/** Fill storage with sample data. */
	public void init() {
		for (int i = 1; i <= 5; i++) {
			Task task = sample();
			task.setId(idGenerator.nextInt(Integer.MAX_VALUE));
			task.setTitle("Task sample " + i);
			task.setSchedule(i + "0000");
			data.put(task.getId(), task);
		}
	}

	/** Sample {@link Task} object. */
	private static Task sample() {
		Task task = new Task();
		task.setId(0);
		task.setTitle("Task sample");
		task.setDescription("Run every n seconds");
		task.setRequestType("GET");
		task.setRequestUri("http://google.com");
		task.setRequestBody(null);
		task.setSchedule("5000");
		task.setScheduleType(ScheduleType.FIXED_DELAY.toString());
		task.setServerId("localhost");
		return task;
	}

	private static void log(String message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(message);
		}		
	}

	@Override
	public Task insert(Task task) {
		Tasks.validate(task, true);
		log("Insert " + task);
		task.setId(idGenerator.nextInt());
		data.put(task.getId(), task);
		return task;
	}

	@Override
	public Task update(Task task) {
		Tasks.validate(task, false);
		log("Update " + task);
		data.put(task.getId(), task);
		return task;
	}

	@Override
	public Task delete(Integer id) {
		log("Delete task by id=" + id);
		return (id > 0) ? data.remove(id) : null;
	}

	@Override
	public List<Task> select() {
		log("Select all tasks");
		return new ArrayList<Task>(data.values());
	}

	@Override
	public Task select(Integer id) {
		log("Select task by id=" + id);
		return (id > 0) ? data.get(id) : null;
	}

	@Override
	public List<Task> select(int offset, int limit) {
		if (limit <= 0 || offset < 0 || offset > data.size()) {
			log("Select n/a for offset=" + offset + ", limit=" + limit);
			return new ArrayList<Task>(0);
		}
		int toIndex = offset + limit;
		if (toIndex > data.size()) {
			toIndex = data.size();
		}
		log("Select tasks with offset=" + offset + ", limit=" + limit);
		return select().subList(offset, toIndex);
	}

	@Override
	public int count() {
		log("Count tasks");
		return data.size();
	}

}