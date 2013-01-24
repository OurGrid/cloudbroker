package org.ourgrid.cloud.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, JSONException, IOException {
		CommonParameters commonParams = new CommonParameters();
		CommandSubmit submitCommand = new CommandSubmit();
		CommandStatus statusCommand = new CommandStatus();
		
		JCommander commander = new JCommander(commonParams);
		commander.addCommand(CommandSubmit.NAME, submitCommand);
		commander.addCommand(CommandStatus.NAME, statusCommand);
		
		try {
			commander.parse(args);
			String parsedCommand = commander.getParsedCommand();
			if (parsedCommand.equals(CommandSubmit.NAME)) {
				submit(submitCommand.jobFile, commonParams.endpoint);
			} else if (parsedCommand.equals(CommandStatus.NAME)) {
				status(statusCommand.jobId, commonParams.endpoint);
			} else {
				commander.usage();
			}
		} catch (ParameterException e) {
			commander.usage();
		}
	}
	
	private static void status(String jobId, String endpoint) throws IOException {
		ClientResource clientResource = new ClientResource(endpoint + "/job/" + jobId);
		InputRepresentation result = (InputRepresentation) clientResource.get();
		System.out.println(IOUtils.toString(result.getStream()));
	}

	private static void submit(String jobFile, String endpoint) throws FileNotFoundException, JSONException, IOException {
		ClientResource clientResource = new ClientResource(endpoint + "/job");
		Representation request = new JsonRepresentation(new JSONObject(
				IOUtils.toString(new FileInputStream(jobFile))));
	    request.setMediaType(MediaType.APPLICATION_JSON);
		InputRepresentation response = (InputRepresentation) clientResource.post(request);
		System.out.println(IOUtils.toString(response.getStream()));
	}

	@Parameters(commandDescription = "Retrieves the status of a job.")
	private static class CommandStatus {
		static String NAME = "status";
		
		@Parameter(names = "-id", description = "The id of the job.", required = true)
		private String jobId;
	}
	
	private static class CommonParameters {
		@Parameter(names = "-e", description = "Scheduler endpoint.", required = true)
		private String endpoint;
	}
	
	@Parameters(commandDescription = "Submits a job to this scheduler.")
	private static class CommandSubmit {
		static String NAME = "submit";
		
		@Parameter(names = "-file", description = "Path to the JSON file representing the job.", required = true)
		private String jobFile;
	}
}
