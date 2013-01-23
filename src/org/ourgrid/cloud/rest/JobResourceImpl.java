package org.ourgrid.cloud.rest;

import org.json.JSONObject;
import org.ourgrid.cloud.broker.JobParser;
import org.ourgrid.cloud.broker.Scheduler;
import org.ourgrid.cloud.broker.model.job.Job;
import org.restlet.Request;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class JobResourceImpl extends ServerResource {
	
	@Post("json")
	public Representation createJob(JsonRepresentation representation) throws Exception {
		Job job = JobParser.parse(representation.getJsonObject());
		String jobId = getScheduler().addJob(job);
		JSONObject jobJson = new JSONObject();
		jobJson.put("id", jobId);
		return new JsonRepresentation(jobJson);
	}
	
	@Get
	public Representation getJob() throws Exception {
		Request request = getRequest();
		String jobId = (String) request.getAttributes().get(BrokerApplication.JOB_ID_PARAM);
		Job job = getScheduler().getJob(jobId);
		JSONObject jobJson = job == null ? new JSONObject() : JobParser.toJson(job);
		return new JsonRepresentation(jobJson);
	}
	
	private Scheduler getScheduler() {
		BrokerApplication application = (BrokerApplication) getApplication();
		return application.getScheduler();
	}
	
}
