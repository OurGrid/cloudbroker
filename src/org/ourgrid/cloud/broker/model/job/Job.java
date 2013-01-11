package org.ourgrid.cloud.broker.model.job;

import java.util.LinkedList;
import java.util.List;

public class Job extends Executable {

	private String name;
	private List<Task> tasks = new LinkedList<Task>();
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	public void addTask(Task task) {
		task.setJob(this);
		tasks.add(task);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
