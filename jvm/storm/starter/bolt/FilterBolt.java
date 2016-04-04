package storm.starter.bolt;

import java.util.Map;
import java.util.Random;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class FilterBolt extends BaseRichBolt{
	OutputCollector _collector;
	Random _rand;
	
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
      _collector = collector;
      _rand = new Random();
    }

    public void execute(Tuple tuple) {
      Utils.sleep(1);
    	String word = tuple.getString(0); 	
    	_collector.emit(tuple, new Values(tuple.getString(0)));
    	_collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word"));
    }
}

