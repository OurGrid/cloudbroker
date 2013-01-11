package org.ourgrid.cloud.broker;

import org.ourgrid.cloud.broker.model.job.IOOperation;
import org.ourgrid.cloud.broker.model.job.Job;
import org.ourgrid.cloud.broker.model.job.Task;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JobParser {

	public static Job parse(JsonElement jobEl) {
		Job job = new Job();
		JsonObject jobJson = jobEl.getAsJsonObject();
		String jobName = jobJson.get("name").getAsString();
		job.setName(jobName);
		JsonArray tasks = jobJson.get("tasks").getAsJsonArray();
		for (JsonElement taskEl : tasks) {
			Task task = parseTask(taskEl);
			job.addTask(task);
		}
		
		return job;
	}

	private static Task parseTask(JsonElement taskEl) {
		Task task = new Task();
		JsonObject taskJson = taskEl.getAsJsonObject();
		String remote = taskJson.get("remote").getAsString();
		task.setRemote(remote);
		
		JsonArray initArray = taskJson.get("init").getAsJsonArray();
		for (JsonElement initEl : initArray) {
			IOOperation ioOperation = parseIOOperation(initEl);
			task.addInitOperation(ioOperation);
		}
		
		JsonArray finalArray = taskJson.get("final").getAsJsonArray();
		for (JsonElement finalEl : finalArray) {
			IOOperation ioOperation = parseIOOperation(finalEl);
			task.addFinalOperation(ioOperation);
		}
		return task;
	}

	private static IOOperation parseIOOperation(JsonElement initEl) {
		JsonObject ioJson = initEl.getAsJsonObject();
		IOOperation ioOperation = new IOOperation();
		String localFile = ioJson.get("local").getAsString();
		ioOperation.setLocal(localFile);
		String remoteFile = ioJson.get("remote").getAsString();
		ioOperation.setRemote(remoteFile);
		return ioOperation;
	}
	
}
