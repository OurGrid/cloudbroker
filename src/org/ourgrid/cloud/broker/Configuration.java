package org.ourgrid.cloud.broker;

public class Configuration {

	public static final String SSH_USER = "ssh.user";
	public static final String SSH_KEY = "ssh.key";
	public static final String SSH_PASSWORD = "ssh.password";
	public static final String SSH_SANDBOX = "ssh.sandbox";
	public static final String SSH_AUTH = "ssh.auth";
	
	public static final String SSH_AUTH_PK = "publickey";
	public static final String SSH_AUTH_PASSWORD = "password";
	
	public static final String EC2_ACCESSKEY = "ec2.accesskey";
	public static final String EC2_SECRETKEY = "ec2.secretkey";
	public static final String EC2_ENDPOINT = "ec2.endpoint";
	
	public static final String INSTANCE_TYPE = "instance.type";
	public static final String INSTANCE_IMAGEID = "instance.imageid";
	public static final String INSTANCE_SECGROUP = "instance.securitygroup";
	public static final String INSTANCE_KEYNAME = "instance.keyname";
	
	public static final String REST_PORT = "rest.port";
}
