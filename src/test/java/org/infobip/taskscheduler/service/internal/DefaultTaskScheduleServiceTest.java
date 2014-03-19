package org.infobip.taskscheduler.service.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.BasicConfigurator;
import org.infobip.taskscheduler.model.Task;
import org.infobip.taskscheduler.model.ScheduleStatus;
import org.infobip.taskscheduler.model.ScheduleType;
import org.infobip.taskscheduler.service.TaskDataService;
import org.infobip.taskscheduler.service.TaskResultDataService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.client.RestTemplate;

public class DefaultTaskScheduleServiceTest {

	private RestTemplate restTemplate = mock(RestTemplate.class);
	private TaskScheduler taskScheduler = mock(TaskScheduler.class);
	private TaskDataService taskDataService = mock(TaskDataService.class);
	private TaskResultDataService taskResultDataService = mock(TaskResultDataService.class);

	private DefaultTaskScheduleService taskScheduleService = new DefaultTaskScheduleService(
			restTemplate, taskScheduler, taskDataService, taskResultDataService);

	private ScheduleStatus scheduleStatus;
	private Task task;

	@BeforeClass
	public static void beforeClass() {
		BasicConfigurator.configure();
	}

	@Before
	public void before() {
		task = new Task();
		task.setId(1);
		task.setTitle("Test");
		task.setRequestType(HttpMethod.GET.toString());
		task.setRequestUri("http://google.com");
		task.setScheduleType(ScheduleType.FIXED_DELAY.toString());
		task.setSchedule("5000");
	}

	@Test
	public void init_noTask() {
		task.setScheduled(false);
		when(taskDataService.select()).thenReturn(Arrays.asList(task));
		taskScheduleService.init();
		verify(taskDataService, never()).update(task);
	}

	@Test
	public void init() {
		task.setScheduled(true);
		when(taskDataService.select()).thenReturn(Arrays.asList(task));
		when(taskScheduler.scheduleWithFixedDelay(any(Runnable.class), anyLong())).thenReturn(mock(ScheduledFuture.class));
		taskScheduleService.init();
		verify(taskDataService).update(task);
	}

	@Test
	public void schedule_noTask() {
		when(taskDataService.select(task.getId())).thenReturn(null);
		scheduleStatus = taskScheduleService.schedule(task.getId());
		assertNull("Task should be null", scheduleStatus.getTask());
		assertEquals("Status message should be equal", "Task does not exist", scheduleStatus.getMessage());
	}

	@Test
	public void schedule() {
		when(taskDataService.select(task.getId())).thenReturn(task);
		when(taskScheduler.scheduleWithFixedDelay(any(Runnable.class), anyLong())).thenReturn(mock(ScheduledFuture.class));
		scheduleStatus = taskScheduleService.schedule(task.getId());
		verify(taskDataService).update(task);
		assertNotNull("Task should not be null", scheduleStatus.getTask());
		assertEquals("Status message should be equal", "Task is scheduled", scheduleStatus.getMessage());
	}

	@Test
	public void schaduled_noTask() {
		assertFalse("Task schould not be scheduled", taskScheduleService.scheduled(task.getId() + 1));
	}

	@Test
	public void schaduled() {
		when(taskDataService.select(task.getId())).thenReturn(task);
		when(taskScheduler.scheduleWithFixedDelay(any(Runnable.class), anyLong())).thenReturn(mock(ScheduledFuture.class));
		taskScheduleService.schedule(task.getId());
		assertTrue("Task schould be scheduled", taskScheduleService.scheduled(task.getId()));
	}

	@Test
	public void cancel_noTask() {
		scheduleStatus = taskScheduleService.cancel(0);
		assertNull("Task should be null", scheduleStatus.getTask());
		assertEquals("Status message should be equal", "Task does not exist", scheduleStatus.getMessage());
	}

	@Test
	public void cancel() {
		when(taskDataService.select(task.getId())).thenReturn(task);
		when(taskScheduler.scheduleWithFixedDelay(any(Runnable.class), anyLong())).thenReturn(mock(ScheduledFuture.class));
		taskScheduleService.schedule(task.getId());
		scheduleStatus = taskScheduleService.cancel(task.getId());
		verify(taskDataService, times(2)).update(task);
		assertNotNull("Task should not be null", scheduleStatus.getTask());
		assertEquals("Status message should be equal", "Task is canceled", scheduleStatus.getMessage());
	}

}