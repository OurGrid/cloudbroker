package org.ourgrid.cloud.rest;

import org.ourgrid.cloud.broker.Scheduler;
import org.ourgrid.cloud.broker.model.job.Job;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class CancelJobResourceImpl extends ServerResource {
	
	@Post("json")
	public Representation createJob(JsonRepresentation representation) throws Exception {
		String jobId = (String) getRequest().getAttributes().get(BrokerApplication.JOB_ID_PARAM);
		Job job = getScheduler().getJob(jobId);
		getScheduler().clean(job);
		return new JsonRepresentation(job);
	}
	
	private Scheduler getScheduler() {
		BrokerApplication application = (BrokerApplication) getApplication();
		return application.getScheduler();
	}
	
}
