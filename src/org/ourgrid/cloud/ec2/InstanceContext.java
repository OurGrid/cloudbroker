package org.ourgrid.cloud.ec2;

public class InstanceContext {
	
	private String imageId;
	private String keyName;
	private String securityGroup;
	private String instanceType;
	
	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getSecurityGroup() {
		return securityGroup;
	}

	public void setSecurityGroup(String securityGroup) {
		this.securityGroup = securityGroup;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}
	
	public String getInstanceType() {
		return instanceType;
	}
}
