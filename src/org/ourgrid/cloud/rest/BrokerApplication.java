package org.ourgrid.cloud.rest;

import java.util.Properties;

import org.ourgrid.cloud.broker.Scheduler;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class BrokerApplication extends Application {

	private static final String JOB_ENDPOINT = "/job";
	private static final String CANCEL_ACTION = "/cancel";
	private static final String CLEAN_ACTION = "/clean";
	
	public static final String JOB_ID_PARAM = "jobId";
	private final Properties properties;
	private Scheduler scheduler;
	
	public BrokerApplication(Properties properties) {
		this.properties = properties;
		this.scheduler = new Scheduler(properties);
	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach(JOB_ENDPOINT + "/{" + JOB_ID_PARAM + "}", JobResourceImpl.class);
		router.attach(JOB_ENDPOINT, JobResourceImpl.class);
		router.attach(JOB_ENDPOINT + "/{" + JOB_ID_PARAM + "}" + CANCEL_ACTION, CancelJobResourceImpl.class);
		router.attach(JOB_ENDPOINT + "/{" + JOB_ID_PARAM + "}" + CLEAN_ACTION, CleanJobResourceImpl.class);
		return router;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public Properties getProperties() {
		return properties;
	}
}
