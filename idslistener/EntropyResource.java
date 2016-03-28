package net.floodlightcontroller.fyp.idslistener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.python.antlr.ast.Str;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import net.floodlightcontroller.fyp.idsentropy.EntropyConsultation;
import net.floodlightcontroller.fyp.request.RequestManager;
import net.floodlightcontroller.fyp.resourcemanagement.ResourceManagement;

public class EntropyResource extends ServerResource{

	Integer window;
	ArrayList<String> sourceIps = new ArrayList<>();
	ArrayList<String> destIps = new ArrayList<>();
	@Get("json")
	public ArrayList<String> check(String fmJson) {
		ArrayList<String> response = new ArrayList<>();
		response.add("1");
		response.add("2");
		response.add("3");
		return response;
	}

	@Post
	public String post(String json) {
		
		if(!RequestManager.isStarted)
		{
			RequestManager.isStarted = true;
			RequestManager requestManager = new RequestManager();
			requestManager.start();
		}
		
		RequestManager.otherRequestCount++;
		while(ResourceManagement.pUV==null){
			System.out.println("waiting");
		}
		System.out.println(ResourceManagement.pUV+","+RequestManager.otherResponseCount+","+RequestManager.otherRequestCount.doubleValue());
		if((RequestManager.otherRequestCount.doubleValue()) < ResourceManagement.pUV)	{
			RequestManager.otherResponseCount++;
			System.out.println("Testing $$$"+json);
	
			Map<String, String> retValue = new HashMap<String,String>();
			MappingJsonFactory f = new MappingJsonFactory();
			JsonParser jp = null;
	
			try{
				try {
					jp = f.createParser(json);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				jp.nextToken();
				if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
					throw new IOException("Expected START_OBJECT");
				}
				
				while (jp.nextToken() != null ) {
					String n = jp.getText();
					//System.out.println("Key::"+n);
					switch (n) {
					case "source-ips":
						jp.nextToken();
						if(jp.getText().equals("[")){
							while(jp.nextToken()!=null && !jp.getText().equals("]")){
								sourceIps.add(jp.getText());
								System.out.println("Ip: "+jp.getText());
							}
						}
						break;
					case "dest-ips":
						jp.nextToken();
						if(jp.getText().equals("[")){
							while(jp.nextToken()!=null && !jp.getText().equals("]")){
								destIps.add(jp.getText());
								System.out.println("Ip: "+jp.getText());
							}
						}
						break;
					case "window":
						jp.nextToken();
						window=jp.getIntValue();
						System.out.println("Window: "+window);
						break;
					default:
						break;
					}
				}
				String result = EntropyConsultation.getConsultation(sourceIps,destIps, window);
				if(result.equals("yes"))
					retValue.put("RESULT", "attack");
				else if(result.equals("no"))
					retValue.put("RESULT", "negative");
				else if(result.equals("unknown"))
					retValue.put("RESULT", "unknown");
			} catch (Exception e) {
				e.printStackTrace();
				retValue.put("ERROR", "Caught IOException while parsing JSON POST request in role request.");
			}
			return retValue.get("RESULT");
		}
		else{
			return "noshare";
		}
		
		
	}
	

}
