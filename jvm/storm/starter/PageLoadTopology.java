package storm.starter;

import storm.starter.bolt.AggregationBolt;
import storm.starter.bolt.FilterBolt;
import storm.starter.bolt.TestBolt;
import storm.starter.bolt.TransformBolt;
import storm.starter.spout.RandomLogSpout;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

public class PageLoadTopology {
	public static void main(String[] args) throws Exception {
		int paralellism = 5;

		TopologyBuilder builder = new TopologyBuilder();

		
		builder.setSpout("spout_head", new RandomLogSpout(), paralellism);
		
		builder.setBolt("bolt_transform", new TransformBolt(), paralellism).shuffleGrouping("spout_head");
		builder.setBolt("bolt_filter", new FilterBolt(), paralellism).shuffleGrouping("bolt_transform");
		builder.setBolt("bolt_join", new TestBolt(), paralellism).shuffleGrouping("bolt_filter");
		builder.setBolt("bolt_filter_2", new FilterBolt(), paralellism).shuffleGrouping("bolt_join");
		builder.setBolt("bolt_aggregate", new AggregationBolt(), paralellism).shuffleGrouping("bolt_filter_2");
		builder.setBolt("bolt_output_sink", new TestBolt(),paralellism).shuffleGrouping("bolt_aggregate");


		Config conf = new Config();
		conf.setDebug(false);
    	conf.setStatsSampleRate(1); // each message is tracked//
    	conf.setIShuffleGroupingEnable(0); //0:default, 1:latency-based load balance, 2: the Power Of Two Choice
    	conf.setIShuffleGroupingAgingRate(0.5);
		
		conf.setNumWorkers(5);
		StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());

	}

}
