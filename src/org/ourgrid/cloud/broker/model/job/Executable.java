package org.ourgrid.cloud.broker.model.job;

public abstract class Executable {
	
	private ExecutableState state = ExecutableState.UNSTARTED;
	
	public ExecutableState getState() {
		return state;
	}
	
	public void setState(ExecutableState state) {
		this.state = state;
	}
	
}
