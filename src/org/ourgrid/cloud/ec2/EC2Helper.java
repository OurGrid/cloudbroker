package org.ourgrid.cloud.ec2;

import java.util.Collections;
import java.util.Comparator;
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
				.withMinCount(1).withMaxCount(1);

		if (context.getInstanceType() != null) {
			runInstancesRequest.withInstanceType(context.getInstanceType());
		}
		
		if (context.getSecurityGroup() != null) {
			runInstancesRequest.withSecurityGroupIds(context.getSecurityGroup());
		}
		
		if (context.getKeyName() != null) {
			runInstancesRequest.withKeyName(context.getKeyName());
		}
		
//		RunInstancesResult result = null;
		
		try {
//			result = ec2.runInstances(runInstancesRequest);
			ec2.runInstances(runInstancesRequest);
		} catch (Exception e) {
			// Not enough resources
			return null;
		}
		
//		Reservation reservation = result.getReservation();
//		if (reservation.getInstances().isEmpty()) {
//			return null;
//		}
//		Instance instance = reservation.getInstances().iterator().next();
//		return instance;
		
		//FIXME EUCA crazy hack
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		DescribeInstancesResult describeInstancesResult = ec2.describeInstances(describeInstancesRequest);
		
		List<Instance> instances = new LinkedList<Instance>();
		
		List<Reservation> reservations = describeInstancesResult.getReservations();
		for (Reservation reservation : reservations) {
			for (Instance instance : reservation.getInstances()) {
				instances.add(instance);
			}
		}
		
		Collections.sort(instances, new Comparator<Instance>() {
			@Override
			public int compare(Instance o1, Instance o2) {
				return o2.getLaunchTime().compareTo(o1.getLaunchTime());
			}
		});
		
		for (Instance instance : instances) {
			return instance;
		}
		
		return null;
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
