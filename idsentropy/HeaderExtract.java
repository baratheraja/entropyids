package net.floodlightcontroller.fyp.idsentropy;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.fyp.http.IdsHttpRequest;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;


public class HeaderExtract extends ServerResource implements IOFMessageListener, IFloodlightModule {
	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected static Integer packetCount,threshold,totalCount;
	protected static Double averageSrcIpEntropy,averageDstIpEntropy;
	protected Queue<String> srcIps,dstIps;
	protected HashMap<String, Integer> srcIpMap,dstIpMap;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return HeaderExtract.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
	  //  Collection<Class<? extends IFloodlightService>> l =
	    //        new ArrayList<Class<? extends IFloodlightService>>();
	       // l.add(IFloodlightProviderService.class);
	   // l.add(IRestApiService.class);
	        return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	    macAddresses = new ConcurrentSkipListSet<Long>();
	    logger = LoggerFactory.getLogger(HeaderExtract.class);
	    srcIps = new LinkedList<String>();
	    dstIps = new LinkedList<String>();
	    srcIpMap = new HashMap<>();
	    dstIpMap = new HashMap<>();
	    
	    packetCount = 0;
	    threshold = 10;

	    averageSrcIpEntropy=0.0;
	    averageDstIpEntropy=0.0;
	    totalCount = 0;
	    
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
	    floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	    
	   
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {
		// TODO Auto-generated method stub
		
		totalCount++;
		
		OFPacketIn pIn = (OFPacketIn) msg;
		pIn.getInPort();
		
		int status =1;
        Ethernet eth =
                IFloodlightProviderService.bcStore.get(cntx,
                                            IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        logger.info("PACKET IN MESSAGE" + eth.getEtherType().getValue());
      /*  if(eth.getEtherType() == EthType.ARP){
        	ARP arp = (ARP) eth.getPayload();
        	logger.info(arp.getSenderHardwareAddress().toString());
        }*/
       // if(eth.getEtherType() == EthType.IPv4){
       if(eth.getEtherType().getValue() == 2048 ){
    	   logger.info("IPv4");
        IPv4 ip = (IPv4) eth.getPayload();
        	IPv4Address srcIp = ip.getSourceAddress();
     		IPv4Address dstIp = ip.getDestinationAddress();
    
     		if (ip.getProtocol().equals(IpProtocol.TCP)) {
				TCP tcp = (TCP) ip.getPayload();
				tcp.getSourcePort().toString();
				tcp.getDestinationPort();
			} else if (ip.getProtocol().equals(IpProtocol.UDP)) {
				UDP udp = (UDP) ip.getPayload();
				udp.getSourcePort().toString();
				udp.getDestinationPort().toString();
			} else if (ip.getProtocol().equals(IpProtocol.ICMP)) {
				//ICMP icmp = (ICMP) ip.getPayload();
				
			}
     		
     		
            logger.info("$$$$$$ Source MAC Address: {} Source Ip address: {} $$$$$$",
                    eth.getSourceMACAddress().toString(),
            		srcIp.toString()
            		/*sw.getId().toString()*/);
            logger.info("$$$$$$ Destination MAC Address: {} Destination Ip address: {} $$$$$$",
                    eth.getDestinationMACAddress().toString(),
            		dstIp.toString()
            		/*sw.getId().toString()*/);

             /*OFPacketIn pin = (OFPacketIn) msg;
             OFFactory factory = sw.getOFFactory(); 
             OFMatchV1 match = factory.buildMatchV1().build();
             match.writeTo(Unpooled.copiedBuffer(pin.getP));
             */
             
           
            
                 
                srcIps.add(srcIp.toString());
             	dstIps.add(dstIp.toString());
             	Integer srcIpCount = srcIpMap.get(srcIp.toString());
             	Integer dstIpCount = dstIpMap.get(dstIp.toString());
             	
             	//code for source ip
             	if(srcIpCount != null){
             		srcIpMap.put(srcIp.toString(), srcIpCount+1);
             		
             	}
             	else{
             		srcIpMap.put(srcIp.toString(), 1);
             	}
             	
             	//code for destination ip
             	if(dstIpCount != null ){
             		dstIpMap.put(dstIp.toString(), dstIpCount+1);
             	}
             	else{
             		dstIpMap.put(dstIp.toString(), 1);
             	}
             	
             
                 if(packetCount < threshold){
                	 	//do nothing ()
                	 packetCount++;
                 }
                 
                 else{
                	 
                 	//decrease count for old ip in window and remove from queue
                	 String oldSrcIp = srcIps.poll();
                	 String oldDstIp = dstIps.poll();
                	 	
                	 Integer osrcIpCount = srcIpMap.get(oldSrcIp);
                  	Integer odstIpCount = dstIpMap.get(oldDstIp);
                  	
                  	//code for source ip
                  	if(osrcIpCount != null){
                  		srcIpMap.put(oldSrcIp, osrcIpCount-1);
                  		
                  	}
                  	//code for destination ip
                  	if(dstIpCount != null ){
                  		dstIpMap.put(oldDstIp, odstIpCount-1);
                  	}
                  
                  Entropy entropy = new Entropy();
                  Double sEntropy =  entropy.calculateEntropy(srcIpMap,threshold);
                  Double dEntropy = entropy.calculateEntropy(dstIpMap,threshold);
                  
                  
                  
                  logger.info("Source ip Entropy "+sEntropy);
                  logger.info("Destination ip Entropy"+dEntropy);
                  //check for entropy rules
                  
              
                  if(dEntropy < averageDstIpEntropy - 0.25 || Double.isNaN(dEntropy)){
                	status = 0;  
                	System.out.println("Attack!!!!!!!!");
                  }else{
                	  averageSrcIpEntropy = ((averageSrcIpEntropy * (totalCount-1)) + sEntropy)/totalCount;
                	  averageDstIpEntropy = ((averageDstIpEntropy * (totalCount-1)) + dEntropy)/totalCount;
                  }
                 }
                 
                 if(status == 0){
         			System.out.println("Attack!!!!!!!!");
         			String json = "{\"switch\":\""+sw.getId().toString()+"\",\"name\":\"flow-mod-1\", \"cookie\":\"0\", \"priority\":\"32768\", \"in_port\":\""+pIn.getInPort().toString()+"\",\"active\":\"true\"}";
         			System.out.println(json);
         			IdsHttpRequest request = new IdsHttpRequest();
         			request.postJSONHttp("http://localhost:8082/wm/staticflowpusher/json", json);
         			
         		} else if( status ==1 ){
         			
         			//no attack
         			
         		} else {
         			//unknown
         			  RequestSnort requestSnort = new RequestSnort();
         			int i = requestSnort.request();
                    System.out.println("Attack detected by snort!!!!!!!!");
           			String json = "{\"switch\":\""+sw.getId().toString()+"\",\"name\":\"flow-mod-1\", \"cookie\":\"0\", \"priority\":\"32768\", \"in_port\":\""+pIn.getInPort().toString()+"\",\"active\":\"true\"}";
           			System.out.println(json);
           			IdsHttpRequest request = new IdsHttpRequest();
           			request.postJSONHttp("http://localhost:8082/wm/staticflowpusher/json", json);
           			
         		}         
                            
         }
		        
       return Command.CONTINUE;
	}
	
	
	

}
