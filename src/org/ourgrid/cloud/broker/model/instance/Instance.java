package org.ourgrid.cloud.broker.model.instance;

public class Instance {

	private InstanceState state;
	private String id;
	private String address;
	
	public InstanceState getState() {
		return state;
	}
	
	public void setState(InstanceState state) {
		this.state = state;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
}
