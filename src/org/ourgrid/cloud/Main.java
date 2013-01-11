package org.ourgrid.cloud;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.ourgrid.cloud.broker.JobParser;
import org.ourgrid.cloud.broker.Scheduler;
import org.ourgrid.cloud.broker.model.job.Job;

import com.google.gson.JsonParser;

public class Main {

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream("broker.properties"));
		
		Job job = JobParser.parse(new JsonParser().parse(
				IOUtils.toString(new FileInputStream("hello.json"))));
		
		new Scheduler(properties).addJob(job);
		
		Thread.currentThread().suspend();
	}
	
}
