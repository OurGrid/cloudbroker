package org.ourgrid.cloud.broker.model.job;

public enum ExecutableState {
	UNSTARTED, RUNNING, FAILED, FINISHED, CANCELLED;
	
	public boolean isComplete() {
		return equals(FAILED) || equals(FINISHED) || equals(CANCELLED);
	}
}
