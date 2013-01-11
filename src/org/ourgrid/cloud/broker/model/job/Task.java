package org.ourgrid.cloud.broker.model.job;

import java.util.LinkedList;
import java.util.List;

public class Task extends Executable {

	private Job job;
	private List<IOOperation> initOperations = new LinkedList<IOOperation>();
	private String remote;
	private List<IOOperation> finalOperations = new LinkedList<IOOperation>();
	
	public Job getJob() {
		return job;
	}
	
	public void setJob(Job job) {
		this.job = job;
	}
	
	public List<IOOperation> getInitOperations() {
		return initOperations;
	}
	
	public void addInitOperation(IOOperation initOperation) {
		initOperations.add(initOperation);
	}
	
	public List<IOOperation> getFinalOperations() {
		return finalOperations;
	}
	
	public void addFinalOperation(IOOperation finalOperation) {
		finalOperations.add(finalOperation);
	}
	
	public void setRemote(String remote) {
		this.remote = remote;
	}
	
	public String getRemote() {
		return remote;
	}
	
}
