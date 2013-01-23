package org.ourgrid.cloud.broker.model.job;

import java.util.LinkedList;
import java.util.List;

public class Job extends Executable {

	private String name;
	private List<Task> tasks = new LinkedList<Task>();
	private String id;
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	public void addTask(Task task) {
		task.setJob(this);
		task.setId(tasks.size() + 1);
		tasks.add(task);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}
