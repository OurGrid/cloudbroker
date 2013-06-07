package org.ourgrid.cloud.broker.model.job;

import java.util.LinkedList;
import java.util.List;

import org.ourgrid.cloud.broker.model.instance.Instance;

public class Task extends Executable {

	private Integer id;
	private Job job;
	private List<IOOperation> initOperations = new LinkedList<IOOperation>();
	private String remote;
	private List<IOOperation> finalOperations = new LinkedList<IOOperation>();
	private Instance instance;
	
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

	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}

	public void setAllocatedInstance(Instance instance) {
		this.instance = instance;
	}
	
	public Instance getAllocatedInstance() {
		return instance;
	}
	
}
