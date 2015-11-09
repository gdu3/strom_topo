/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package storm.starter;

import java.util.Arrays;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import backtype.storm.StormSubmitter;

import storm.starter.bolt.PrinterBolt;
import storm.starter.spout.TwitterSampleSpout;

public class PrintSampleStream {        
    public static void main(String[] args) throws Exception {
        String consumerKey = args[0]; 
        String consumerSecret = args[1]; 
        String accessToken = args[2]; 
        String accessTokenSecret = args[3];
        String[] arguments = args.clone();
        String[] keyWords = Arrays.copyOfRange(arguments, 4, arguments.length);
        
        TopologyBuilder builder = new TopologyBuilder();
        
        builder.setSpout("twitter1", new TwitterSampleSpout(consumerKey, consumerSecret,
                                accessToken, accessTokenSecret, keyWords));
        builder.setBolt("print", new PrinterBolt(), 5)
                .shuffleGrouping("twitter1");
                
                
        Config conf = new Config();
	      conf.setDebug(false);
    	  conf.setStatsSampleRate(1); // each message is tracked//
    	  //conf.setReplicationRatio(0.2);
    	  //conf.setEnableTimeoutAdjustment(false);

	
	if (true) {
      		conf.setNumWorkers(5);
      		StormSubmitter.submitTopologyWithProgressBar("twitter-example", conf, builder.createTopology());
    	}
    	else {
      		conf.setMaxTaskParallelism(3);

      		LocalCluster cluster = new LocalCluster();
      		cluster.submitTopology("word-count", conf, builder.createTopology());

      		Thread.sleep(10000);

      		cluster.shutdown();
    	}
    }
}
