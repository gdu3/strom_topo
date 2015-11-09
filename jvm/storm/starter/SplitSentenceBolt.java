package storm.starter;

import java.util.Map;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class SplitSentenceBolt extends BaseRichBolt {
	private OutputCollector collector;
	
	public void execute(Tuple tuple) {
		Utils.sleep(2);
		String sentence = tuple.getString(0);
		for(String word: sentence.split("\\s+")) {
			collector.emit(tuple, new Values(word));
		}
		collector.ack(tuple);

	}

	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
}
