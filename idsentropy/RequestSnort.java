package net.floodlightcontroller.fyp.idsentropy;


import net.floodlightcontroller.fyp.http.IdsHttpRequest;
import net.floodlightcontroller.fyp.request.RequestManager;


public class RequestSnort {
	int status = 1;
	IdsHttpRequest idsHttpRequest = new IdsHttpRequest();
	public int request(){
		
		if(!RequestManager.isStarted)
		{
			RequestManager.isStarted = true;
			RequestManager requestManager = new RequestManager();
			requestManager.start();
		}
		
		status = 1;
		RequestManager.requestCount++;
		RequestManager.responseCount++;
		String url = "http://localhost:8083/wm/snortids/consult";
		String data = "sample.pcap";
		status = idsHttpRequest.postJSONHttpSync(url, data);
		
		if(status == -1){
			RequestManager.responseCount --;
		}
		
		return status;
	}
}
