package org.ourgrid.cloud.ec2;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class EC2Helper {

	private AmazonEC2Client ec2;

	public EC2Helper(String accessKey, String secretKey, String endpoint) {
		AWSCredentials credentials = new BasicAWSCredentials(
				accessKey, secretKey);
		this.ec2 = new AmazonEC2Client(credentials);
		if (endpoint != null) {
			this.ec2.setEndpoint(endpoint);
		}
	}
	
	public Instance createInstance(InstanceContext context) {
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		
		runInstancesRequest.withImageId(context.getImageId())
				.withMinCount(1).withMaxCount(1)
				.withKeyName(context.getKeyName());

		if (context.getInstanceType() != null) {
			runInstancesRequest.withInstanceType(context.getInstanceType());
		}
		
		if (context.getSecurityGroup() != null) {
			runInstancesRequest.withSecurityGroupIds(context.getSecurityGroup());
		}
		
		RunInstancesResult result = ec2.runInstances(runInstancesRequest);
		Reservation reservation = result.getReservation();
		if (reservation.getInstances().isEmpty()) {
			return null;
		}
		
		Instance instance = reservation.getInstances().iterator().next();
		return instance;
	}
	
	public Instance getInstanceStatus(String instanceId) {
		DescribeInstancesRequest dir = new DescribeInstancesRequest().withInstanceIds(instanceId);
		DescribeInstancesResult result = ec2.describeInstances(dir);
		List<Reservation> reservations = result.getReservations();
		
		if (reservations.isEmpty()) {
			return null;
		}
		
		Reservation reservation = reservations.iterator().next();
		if (reservation.getInstances().isEmpty()) {
			return null;
		}
		Instance instance = reservation.getInstances().iterator().next();
		return instance;
	}
	
	public void startInstance(String instanceId) {
		StartInstancesRequest sir = new StartInstancesRequest();
		List<String> instancesIds = new LinkedList<String>();
		instancesIds.add(instanceId);
		sir.setInstanceIds(instancesIds);
		ec2.startInstances(sir);
	}
	
	public void terminateInstance(String instanceId) {
		TerminateInstancesRequest tir = new TerminateInstancesRequest();
		List<String> instancesIds = new LinkedList<String>();
		instancesIds.add(instanceId);
		tir.setInstanceIds(instancesIds);
		ec2.terminateInstances(tir);
	}
}
