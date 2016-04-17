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

		TopologyBuilder builder = new TopologyBuilder();

		
		builder.setSpout("spout_head", new RandomLogSpout(), 5);
		
		builder.setBolt("bolt_transform", new TransformBolt(), 5).shuffleGrouping("spout_head").addConfiguration("groupId", "1");;
		builder.setBolt("bolt_filter", new FilterBolt(), 5).shuffleGrouping("bolt_transform").addConfiguration("groupId", "2");;
		builder.setBolt("bolt_join", new TestBolt(), 5).shuffleGrouping("bolt_filter").addConfiguration("groupId", "3");;
		builder.setBolt("bolt_filter_2", new FilterBolt(), 5).shuffleGrouping("bolt_join").addConfiguration("groupId", "4");;
		builder.setBolt("bolt_aggregate", new AggregationBolt(), 5).shuffleGrouping("bolt_filter_2").addConfiguration("groupId", "5");;
		builder.setBolt("bolt_output_sink", new TestBolt(),5).shuffleGrouping("bolt_aggregate");


		Config conf = new Config();
		conf.setDebug(false);
    	conf.setStatsSampleRate(1); // each message is tracked//
    	conf.setIShuffleGroupingEnable(1); //0:default, 1:latency-based load balance, 2: the Power Of Two Choice
    	conf.setIShuffleGroupingAgingRate(0.5);
		
		conf.setNumWorkers(5);
		StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());

	}

}
