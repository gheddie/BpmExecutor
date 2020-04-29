package de.gravitex.bpm.executor.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import de.gravitex.bpm.executor.enumeration.LifeCycle;
import de.gravitex.bpm.executor.util.DiffContainer;
import lombok.Data;

@Data
public class ProcessEngineState {

	// divided items by class
	private HashMap<Class<?>, HashMap<String, Object>> processItems = new HashMap<Class<?>, HashMap<String,Object>>();

	public ProcessEngineState() {
		super();
	}

	public ProcessEngineState fromProcessInstance(ProcessInstance processInstance) {
		
		ProcessEngineState processEngineState = new ProcessEngineState();
		ProcessEngine processEngine = BpmExecutionSingleton.getInstance().getProcessEngine();
		
		// tasks
		for (Task task : processEngine.getTaskService().createTaskQuery().list()) {
			if (processItems.get(Task.class) == null) {
				processItems.put(Task.class, new HashMap<String, Object>());	
			}
			processItems.get(Task.class).put(task.getId(), task);
		}
		// jobs
		for (Job job : processEngine.getManagementService().createJobQuery().list()) {
			if (processItems.get(Job.class) == null) {
				processItems.put(Job.class, new HashMap<String, Object>());	
			}
			processItems.get(Job.class).put(job.getId(), job);
		}
		// messages
		for (EventSubscription eventSubscription : processEngine.getRuntimeService().createEventSubscriptionQuery().list()) {
			if (processItems.get(EventSubscription.class) == null) {
				processItems.put(EventSubscription.class, new HashMap<String, Object>());	
			}
			processItems.get(EventSubscription.class).put(eventSubscription.getId(), eventSubscription);
		}
		
		processEngineState.setProcessItems(processItems);
		return processEngineState;
	}
	
	public HashMap<Class<?>, DiffContainer> compareTo(ProcessEngineState newEngineState) {
		
		HashMap<Class<?>, DiffContainer> result = new HashMap<Class<?>, DiffContainer>();
		
		// tasks
		result.put(Task.class, diffsByType(getItemsByType(Task.class), newEngineState.getItemsByType(Task.class)));
		
		// jobs
		result.put(Job.class, diffsByType(getItemsByType(Job.class), newEngineState.getItemsByType(Job.class)));
		
		// event subscriptions
		result.put(EventSubscription.class, diffsByType(getItemsByType(EventSubscription.class), newEngineState.getItemsByType(EventSubscription.class)));
		
		return result;
	}

	private DiffContainer diffsByType(HashMap<String, Object> oldItemsByType, HashMap<String, Object> newItemsByType) {
		
		HashSet<String> commonKeys = new HashSet<String>();
		if (oldItemsByType != null) {
			commonKeys.addAll(oldItemsByType.keySet());			
		}
		if (newItemsByType != null) {
			commonKeys.addAll(newItemsByType.keySet());			
		}
		DiffContainer container = new DiffContainer();
		for (String key : commonKeys) {
			if (keyPresent(oldItemsByType, key) && keyPresent(newItemsByType, key)) {
				// persistent
				container.addItem(oldItemsByType.get(key), LifeCycle.PERSISTENT);
			} else if (!keyPresent(oldItemsByType, key) && keyPresent(newItemsByType, key)) {
				// created
				container.addItem(newItemsByType.get(key), LifeCycle.CREATED);
			} else if (keyPresent(oldItemsByType, key) && !keyPresent(newItemsByType, key)) {
				// removed
				container.addItem(oldItemsByType.get(key), LifeCycle.REMOVED);
			}
		}
		return container;
	}

	private boolean keyPresent(HashMap<String, Object> map, String key) {
		if (map == null) {
			return false;	
		}
		return map.containsKey(key);
	}

	private HashMap<String, Object> getItemsByType(Class<?> clazz) {
		return processItems.get(clazz);
	}
}