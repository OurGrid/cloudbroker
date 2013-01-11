package org.ourgrid.cloud.broker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.ourgrid.cloud.broker.model.instance.Instance;
import org.ourgrid.cloud.broker.model.instance.InstanceState;
import org.ourgrid.cloud.broker.model.job.ExecutableState;
import org.ourgrid.cloud.broker.model.job.Job;
import org.ourgrid.cloud.broker.model.job.Task;
import org.ourgrid.cloud.ec2.EC2Helper;
import org.ourgrid.cloud.ec2.InstanceContext;
import org.ourgrid.cloud.executor.SSHExecutor;

public class Scheduler {

	private List<Job> jobs = new LinkedList<Job>();
	private List<Instance> instances = new LinkedList<Instance>();
	private EC2Helper ec2;
	private Properties properties;
	
	public Scheduler(Properties properties) {
		this.properties = properties;
		initEC2();
	}
	
	private void initEC2() {
		this.ec2 = new EC2Helper(
				properties.getProperty(Configuration.EC2_ACCESSKEY), 
				properties.getProperty(Configuration.EC2_SECRETKEY));
	}

	public void addJob(Job job) {
		jobs.add(job);
		schedule();
	}
	
	private void schedule() {
		for (Job job : jobs) {
			schedule(job);
		}
		disposeIdleWorkers();
	}

	private void disposeIdleWorkers() {
		Iterator<Instance> iterator = instances.iterator();
		while (iterator.hasNext()) {
			Instance instance = (Instance) iterator.next();
			if (instance.getState().equals(InstanceState.IDLE)) {
				ec2.terminateInstance(instance.getId());
				iterator.remove();
			}
		}
	}

	private void schedule(Job job) {
		if (job.getState().equals(ExecutableState.FINISHED)) {
			return;
		}
		for (Task task : job.getTasks()) {
			schedule(task);
		}
	}

	private void schedule(Task task) {
		if (task.getState().equals(ExecutableState.FINISHED)) {
			return;
		}
		Instance instance = nextAvailableInstance();
		if (instance == null) {
			instance = requestInstance();
		}
		
		if (instance != null) {
			instance.setState(InstanceState.BUSY);
			task.setState(ExecutableState.RUNNING);
			new SSHExecutor(this, instance, task).execute();
		}
	}
	
	public void finished(Task task, Instance instance) {
		task.setState(ExecutableState.FINISHED);
		instance.setState(InstanceState.IDLE);
		
		boolean siblingFinished = true;
		for (Task sibling : task.getJob().getTasks()) {
			if (!sibling.getState().equals(ExecutableState.FINISHED)) {
				siblingFinished = false;
				break;
			}
		}
		
		if (siblingFinished) {
			task.getJob().setState(ExecutableState.FINISHED);
		}
		
		schedule();
	}
	
	public void failed(Task task, Instance instance) {
		task.setState(ExecutableState.FAILED);
		instance.setState(InstanceState.IDLE);
		schedule();
	}
	
	private Instance requestInstance() {
		com.amazonaws.services.ec2.model.Instance ec2Instance = ec2.createInstance(createContext());
		if (ec2Instance == null) {
			return null;
		}
		
		Instance instance = new Instance();
		instance.setId(ec2Instance.getInstanceId());
		instance.setAddress(ec2Instance.getPublicIpAddress());
		
		instances.add(instance);
		
		return instance;
	}

	private Instance nextAvailableInstance() {
		for (Instance instance : instances) {
			if (instance.getState().equals(InstanceState.IDLE)) {
				return instance;
			}
		}
		return null;
	}
	
	private InstanceContext createContext() {
		InstanceContext context = new InstanceContext();
		context.setInstanceType(properties.getProperty(Configuration.INSTANCE_TYPE));
		context.setImageId(properties.getProperty(Configuration.INSTANCE_IMAGEID));
		context.setSecurityGroup(properties.getProperty(Configuration.INSTANCE_SECGROUP));
		context.setKeyName(properties.getProperty(Configuration.INSTANCE_KEYNAME));
		return context;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public EC2Helper getEc2() {
		return ec2;
	}
}