 package storm.starter;

import storm.starter.bolt.AggregationBolt;
import storm.starter.bolt.FilterBolt;
import storm.starter.bolt.TestBolt;
import storm.starter.bolt.TransformBolt;
import storm.starter.spout.RandomLogSpout;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

public class ProcessingTopology {
	public static void main(String[] args) throws Exception {
		//int numBolt = 3;
		//int paralellism = 2;

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout_head", new RandomLogSpout(), 5);
		builder.setBolt("bolt_transform", new TransformBolt(), 5).shuffleGrouping("spout_head");

		builder.setBolt("bolt_filter", new TestBolt(), 5).shuffleGrouping("bolt_transform");
		builder.setBolt("bolt_join1", new TestBolt(), 5).shuffleGrouping("bolt_filter");
		builder.setBolt("bolt_output_1", new TestBolt(),5).shuffleGrouping("bolt_join1");		

		builder.setBolt("bolt_join2", new TransformBolt(), 5).shuffleGrouping("bolt_transform");
		builder.setBolt("bolt_normalize", new TestBolt(), 5).shuffleGrouping("bolt_join2");
		builder.setBolt("bolt_output_2", new TestBolt(), 5).shuffleGrouping("bolt_normalize");

		Config conf = new Config();
    	conf.setDebug(false);
    	conf.setStatsSampleRate(1); // each message is tracked//
    	
    	conf.setIShuffleGroupingEnable(1); //0:default, 1:latency-based load balance, 2: the Power Of Two Choice
    	conf.setIShuffleGroupingAgingRate(0.5);
		
		conf.setNumWorkers(5);
		StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());

	}

}
