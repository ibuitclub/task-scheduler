package org.infobip.taskscheduler.service.internal;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import org.infobip.taskscheduler.model.Task;
import org.infobip.taskscheduler.model.TaskResult;
import org.infobip.taskscheduler.model.ScheduleStatus;
import org.infobip.taskscheduler.model.ScheduleType;
import org.infobip.taskscheduler.service.TaskDataService;
import org.infobip.taskscheduler.service.TaskResultDataService;
import org.infobip.taskscheduler.service.TaskScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.client.RestTemplate;

/** 
 * Wrapper service around Spring' {@link TaskScheduler} API.
 * <p>
 * <b>Note</b>: Subject to change.
 * <p>
 * <br>TODO Service should support various types of tasks, such as: 
 * <del>HTTP</del>, SSH, scripts, internal statistics, etc.
 */
public final class DefaultTaskScheduleService implements TaskScheduleService {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(DefaultTaskScheduleService.class);

	/** Holds currently running/scheduled tasks. */
	private final ConcurrentMap<Integer, ScheduledFuture<?>> registry = 
			new ConcurrentHashMap<Integer, ScheduledFuture<?>>();

	private final RestTemplate restTemplate;
	private final TaskScheduler taskScheduler;
	private final TaskDataService taskDataService;
	private final TaskResultDataService taskResultDataService;

	public DefaultTaskScheduleService(RestTemplate restTemplate,
			TaskScheduler taskScheduler, TaskDataService taskDataService,
			TaskResultDataService taskResultDataService) {

		this.restTemplate = restTemplate;
		this.taskScheduler = taskScheduler;
		this.taskDataService = taskDataService;
		this.taskResultDataService = taskResultDataService;
	}

	private static void log(String message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(message);
		}
	}

	/** Helper method for tracing schedule responses. */
	private static ScheduleStatus scheduleStatus(String message, Task task) {
		ScheduleStatus scheduleStatus = new ScheduleStatus(message, task);
		log(scheduleStatus.toString());
		return scheduleStatus;
	}

	/** Schedule all active tasks at startup. */
	public void init() {
		log("Initializing schedule for active tasks");
		List<Task> list = taskDataService.select();
		for (Task task : list) {
			if (task.isScheduled()) {
				schedule(task);
			} else {
				scheduleStatus("Task is inactive", task);
			}
		}
	}

	/** Discard all running tasks. */
	public void destroy() {
		log("Destroying running tasks");
		for (Integer key : registry.keySet()) {
			ScheduledFuture<?> scheduledFuture = registry.remove(key);
			scheduledFuture.cancel(true);
		}
	}

	/** Cancel scheduled task. */
	@Override
	public ScheduleStatus cancel(int taskId) {

		Task task = taskId > 0 ? taskDataService.select(taskId) : null;
		if (task == null) {
			return scheduleStatus("Task does not exist", null);
		}

		ScheduledFuture<?> scheduledFuture = registry.remove(taskId);
		if (scheduledFuture == null) {
			return scheduleStatus("Task is not scheduled", task);
		}
		scheduledFuture.cancel(true);

		task.setNextRun(0L);
		task.setRunningNow(false);
		task.setScheduled(false);
		taskDataService.update(task);

		return scheduleStatus("Task is canceled", task);
	}

	/** Check if task is scheduled. */
	@Override
	public boolean scheduled(int taskId) {
		return taskId > 0 ? registry.containsKey(taskId) : false;
	}

	/** Schedule task. */
	@Override
	public ScheduleStatus schedule(int taskId) {
		Task task = taskId > 0 ? taskDataService.select(taskId) : null;
		if (task == null) {
			return scheduleStatus("Task does not exist", null);
		}
		if (scheduled(taskId)) {
			return scheduleStatus("Task is already scheduled", task);
		}
		return schedule(task);
	}

	/** Actual magic behind the {@link #schedule(int)}. */
	private ScheduleStatus schedule(Task task) {

		TaskRunnable taskRunnable = new TaskRunnable(task);

		ScheduleType scheduleType = ScheduleType.valueOf(task.getScheduleType());
		String schedule = task.getSchedule();

		ScheduledFuture<?> scheduledFuture = null;

		try {
			if (ScheduleType.CRON.equals(scheduleType)) {
				scheduledFuture = taskScheduler.schedule(taskRunnable, new CronTrigger(schedule));
			}
			else if (ScheduleType.FIXED_RATE.equals(scheduleType)) {
				scheduledFuture = taskScheduler.scheduleAtFixedRate(taskRunnable, Long.valueOf(schedule));
			}
			else if (ScheduleType.FIXED_DELAY.equals(scheduleType)) {
				scheduledFuture = taskScheduler.scheduleWithFixedDelay(taskRunnable, Long.valueOf(schedule));
			}
		} catch (Exception e) { // TaskRejectedException & IllegalArgumentException
			return scheduleStatus("Task is rejected by scheduler, " + e.getMessage(), task);
		}

		if (scheduledFuture == null) {
			return scheduleStatus("Task is rejected by scheduler", task);
		}

		registry.put(task.getId(), scheduledFuture);
		task.setScheduled(true);

		long nextRun = calcNextRun(0L, task.getSchedule(), ScheduleType.valueOf(task.getScheduleType()));
		task.setNextRun(nextRun);

		taskDataService.update(task);

		return scheduleStatus("Task is scheduled", task);
	}

	private long calcNextRun(long lastRun, String schedule, ScheduleType scheduleType) {
		long nextRun;
		if (lastRun == 0L) {
			lastRun = System.currentTimeMillis();
		}
		if (ScheduleType.CRON.equals(scheduleType)) {
			CronSequenceGenerator cronSequenceGenerator = 
					new CronSequenceGenerator(schedule, TimeZone.getDefault());
			nextRun = cronSequenceGenerator.next(new Date(lastRun)).getTime();
		} else {
			nextRun = lastRun + Long.parseLong(schedule);
		}
		return nextRun;
	}

	/**
	 * HTTP task runnable.
	 * <p>
	 * <b>Note</b>: Subject to change.
	 * <p>
	 * <br>TODO Interface for task runnable.
	 * <br>TODO Rename to HttpTask*
	 */
	private class TaskRunnable implements Runnable {

		private Task task;

		public TaskRunnable(Task task) {
			this.task = task;
		}

		@Override
		public void run() {

			task.setRunningNow(true);
			taskDataService.update(task);

			log("Running " + task);
			String result = request(task);

			long lastRun = System.currentTimeMillis();
			long nextRun = calcNextRun(lastRun, task.getSchedule(), ScheduleType.valueOf(task.getScheduleType()));

			task.setLastRun(lastRun);
			task.setNextRun(nextRun);
			task.setRunningNow(false);
			taskDataService.update(task);

			TaskResult taskResult = new TaskResult(task.getId(), result);
			taskResultDataService.insert(taskResult);
		}

		/** 
		 * Make request with {@link RestTemplate#exchange(String, HttpMethod, 
		 * HttpEntity, Class, Object...)} method, and return response headers 
		 * and body.
		 * <p>
		 * Resulting string is formated, so headers and body are separated with 
		 * {@code \n} characters.
		 */
		private String request(final Task task) {

			HttpMethod httpMethod = HttpMethod.valueOf(task.getRequestType());
			HttpHeaders httpHeaders = createHttpHeaders(task.getRequestHeader());

			HttpEntity<String> httpEntity = new HttpEntity<String>(
					task.getRequestBody(), httpHeaders);

			ResponseEntity<String> responseEntity = restTemplate.exchange(
					task.getRequestUri(), httpMethod, httpEntity, String.class);

			String response = "";

			Map<String, String> map = responseEntity.getHeaders().toSingleValueMap();
			for (Entry<String, String> entry : map.entrySet()) {
				response += entry.getKey() + ":" + entry.getValue() + "\n";
			}

			if (responseEntity.hasBody()) {
				response += "\n" + responseEntity.getBody();
			}

			return response;
		}

		private HttpHeaders createHttpHeaders(final String header) {
			if (header == null || header.trim().length() == 0) {
				return null;
			}
			HttpHeaders httpHeaders = new HttpHeaders();
			String[] parts = header.split("\n");
			for (String part : parts) {
				String[] pair = part.split(":");
				if (pair.length == 2) {						
					httpHeaders.add(pair[0].trim(), pair[1].trim());
				}
			}
			return httpHeaders;
		}

	}

}