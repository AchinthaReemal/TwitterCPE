package org.wso2.uima.main;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.wso2.uima.cpe.reader.data.Tweet;
import org.wso2.uima.demo.DemoClient;

import java.io.IOException;
import java.util.ArrayList;



public class RunCPE {

	public static ArrayList<Tweet> sharedList;	
	private final Logger logger = Logger.getLogger(RunCPE.class);
	public static DemoClient client;


	public static void main(String[] args)
		throws IOException, InterruptedException, InvalidXMLException, ResourceInitializationException {

		org.apache.log4j.PropertyConfigurator.configure("conf/log4j.properties");

		XMLInputSource in = new XMLInputSource("descriptors/cpe/twitterCPE.xml");
		CpeDescription cpe_desc = UIMAFramework.getXMLParser().parseCpeDescription(in);
		
		CollectionProcessingEngine cpe = null;
		sharedList = new ArrayList<Tweet>();

		long[] follow = new long[] { 711930980, 808888003, 4366881, 2984541727l };

		//TwitterStreamer streamer = new TwitterStreamer(follow);
		//StatusHandler handler = new StatusHandler(sharedList);
		//System.out.println("Application Started");

		//streamer.startStream(handler);
		Thread.sleep(10000);
		Logger.getLogger(RunCPE.class).info("Streaming Tweets Started");
		

		//runs the demo client which streams past tweets from road.lk
		// comment if you want to work only real time tweets
		client = new DemoClient(sharedList, "road_lk");
		//client.fetchNext(100);

		while (true) {

			System.out.println("\n*******************1 Minute Window Expired*********************");
			// fetch next {amount} of demo tweets
			client.fetchNext(100);

			Logger.getLogger(RunCPE.class).info(
					"Tweets Recieved : " + sharedList.size());

			for (Tweet t : sharedList) {
				Logger.getLogger(RunCPE.class).debug(
						"Recieved : " + t.toString());
			}

			System.out.println();
			cpe = UIMAFramework.produceCollectionProcessingEngine(cpe_desc);
			cpe.addStatusCallbackListener(new statusCallBackCPE());
			cpe.process();
			Thread.sleep(30000);
		}

	}

}

class statusCallBackCPE implements StatusCallbackListener{

	@Override
	public void aborted() {
		// TODO Auto-generated method stub
		System.out.println("CPE aborted");
		  System.exit(1);
	}

	@Override
	public void batchProcessComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collectionProcessComplete() {
		// TODO Auto-generated method stub
		System.out.println("Collection Process Complete");
		RunCPE.sharedList.clear();
		System.out.println("*************************Tweets Cleared*************************\n");

	//	Logger.getLogger(RunCPE.class).info("Clearing List");

	}

	@Override
	public void initializationComplete() {
		// TODO Auto-generated method stub
		System.out.println("CPE Initialization Completed");
	}

	@Override
	public void paused() {
		// TODO Auto-generated method stub
		System.out.println("CPE is paused");
	}

	@Override
	public void resumed() {
		// TODO Auto-generated method stub
		System.out.println("CPE is resumed");
	}

	@Override
	public void entityProcessComplete(CAS arg0, EntityProcessStatus arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
