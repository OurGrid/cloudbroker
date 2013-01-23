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

public class Main {

	public static void main(String[] args) throws FileNotFoundException, JSONException, IOException {
		ClientResource clientResource = new ClientResource("http://localhost:9192/job");
		Representation response = new JsonRepresentation(new JSONObject(
				IOUtils.toString(new FileInputStream("hello.json"))));
	    response.setMediaType(MediaType.APPLICATION_JSON);
		InputRepresentation result = (InputRepresentation) clientResource.post(response);
		System.out.println(IOUtils.toString(result.getStream()));
	}
	
}
