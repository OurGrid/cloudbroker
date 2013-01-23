package org.ourgrid.cloud.broker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ourgrid.cloud.broker.model.job.IOOperation;
import org.ourgrid.cloud.broker.model.job.Job;
import org.ourgrid.cloud.broker.model.job.Task;

public class JobParser {

	public static JSONObject toJson(Job job) throws JSONException {
		JSONObject jobJson = new JSONObject();
		jobJson.put("name", job.getName());
		JSONArray tasksJson = new JSONArray();
		for (Task task : job.getTasks()) {
			tasksJson.put(toJson(task));
		}
		jobJson.put("tasks", tasksJson);
		jobJson.put("status", job.getState().toString());
		return jobJson;
	}
	
	private static JSONObject toJson(Task task) throws JSONException {
		JSONObject taskJson = new JSONObject();
		taskJson.put("remote", task.getRemote());
		taskJson.put("status", task.getState().toString());
		
		JSONArray initJson = new JSONArray();
		for (IOOperation initOperation : task.getInitOperations()) {
			initJson.put(toJson(initOperation));
		}
		taskJson.put("init", initJson);

		JSONArray finalJson = new JSONArray();
		for (IOOperation finalOperation : task.getFinalOperations()) {
			finalJson.put(toJson(finalOperation));
		}
		taskJson.put("final", finalJson);
		
		return taskJson;
	}

	private static JSONObject toJson(IOOperation initOperation) throws JSONException {
		JSONObject ioJson = new JSONObject();
		ioJson.put("local", initOperation.getLocal());
		ioJson.put("remote", initOperation.getRemote());
		return ioJson;
	}

	public static Job parse(JSONObject jobJson) throws JSONException {
		Job job = new Job();
		String jobName = jobJson.getString("name");
		job.setName(jobName);
		JSONArray tasks = jobJson.getJSONArray("tasks");
		for (int i = 0; i < tasks.length(); i++) {
			JSONObject taskEl = tasks.getJSONObject(i);
			Task task = parseTask(taskEl);
			job.addTask(task);
		}
		return job;
	}

	private static Task parseTask(JSONObject taskJson) throws JSONException {
		Task task = new Task();
		String remote = taskJson.getString("remote");
		task.setRemote(remote);
		
		JSONArray initArray = taskJson.getJSONArray("init");
		for (int i = 0; i < initArray.length(); i++) {
			JSONObject initEl = initArray.getJSONObject(i);
			IOOperation ioOperation = parseIOOperation(initEl);
			task.addInitOperation(ioOperation);
		}
		
		JSONArray finalArray = taskJson.getJSONArray("final");
		for (int i = 0; i < finalArray.length(); i++) {
			JSONObject finalEl = finalArray.getJSONObject(i);
			IOOperation ioOperation = parseIOOperation(finalEl);
			task.addFinalOperation(ioOperation);
		}
		return task;
	}

	private static IOOperation parseIOOperation(JSONObject ioJson) throws JSONException {
		IOOperation ioOperation = new IOOperation();
		String localFile = ioJson.getString("local");
		ioOperation.setLocal(localFile);
		String remoteFile = ioJson.getString("remote");
		ioOperation.setRemote(remoteFile);
		return ioOperation;
	}
	
}
