package net.floodlightcontroller.fyp.idsentropy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class EntropyConsultation {

	
	public static String getConsultation(ArrayList<String> sourceIps,ArrayList<String> destIps, Integer window){
		HashMap<String, Integer> srcIpMap,dstIpMap;
		Queue<String> srcipQ,dstipQ;
		Double sourceIpEntropy,destIpEntropy;
		Double averageSrcIpEntropy,averageDstIpEntropy;
		averageSrcIpEntropy=0.0;
		averageDstIpEntropy=0.0;
		int status=2;
		srcipQ = new LinkedList<String>();
		dstipQ = new LinkedList<String>();
		Entropy entropy = new Entropy();
		srcIpMap = new HashMap<>();
		dstIpMap = new HashMap<>();
		int count = 0;
		
		//for the first window source ips
		for(String ip:sourceIps){
			if(count>=window) 
				break;
			Integer c = srcIpMap.get(ip);
			srcipQ.add(ip);
			if(c!=null){
				srcIpMap.put(ip, c+1);
			}
			else{
				srcIpMap.put(ip, 1);
			}
			count++;
		}

		//for the first window destination ips
		count=0;
		for(String ip:destIps){
			if(count>=window) 
				break;
			Integer c = dstIpMap.get(ip);
			dstipQ.add(ip);
			if(c!=null){
				dstIpMap.put(ip, c+1);
			}
			else{
				dstIpMap.put(ip, 1);
			}
			count++;
		}
		
		// for the next consecutive packet
		while(count < sourceIps.size() && count < destIps.size()){
			String oldSrcIp,oldDstIp;
			
			//Processing source ip
			Integer c = srcIpMap.get(sourceIps.get(count));
			srcipQ.add(sourceIps.get(count));
			if(c!=null){
				srcIpMap.put(sourceIps.get(count), c+1);
			}
			else{
				srcIpMap.put(sourceIps.get(count), 1);
			}
			
			//removing old source ip from the queue
			oldSrcIp = srcipQ.poll();
			c = srcIpMap.get(oldSrcIp);
			srcIpMap.put(oldSrcIp, c-1);
			
			//process destination ip
			
			c = dstIpMap.get(destIps.get(count));
			dstipQ.add(destIps.get(count));
			if(c!=null){
				dstIpMap.put(destIps.get(count), c+1);
			}
			else{
				dstIpMap.put(destIps.get(count), 1);
			}
			
			//removing old destination ip from the queue
			oldDstIp = dstipQ.poll();
			c = dstIpMap.get(oldDstIp);
			dstIpMap.put(oldDstIp, c-1);
			
			sourceIpEntropy = entropy.calculateEntropy(srcIpMap, window);
			destIpEntropy = entropy.calculateEntropy(dstIpMap, window);
			
			
			count++;
			
			
			System.out.println(destIpEntropy);
			if(destIpEntropy < averageDstIpEntropy - 0.25 || Double.isNaN(destIpEntropy)){
            	status = 0;  
            	System.out.println("Attack on snort!!!!!!!!");
              }
			else{
            	  averageSrcIpEntropy = ((averageSrcIpEntropy * (count-1)) + sourceIpEntropy)/count;
            	  averageDstIpEntropy = ((averageDstIpEntropy * (count-1)) + destIpEntropy)/count;
              }
		}
		
		//do the processing
		if(status == 0){
			return "yes";
		} else{
			return "unknown";
		}
		
	}
}
