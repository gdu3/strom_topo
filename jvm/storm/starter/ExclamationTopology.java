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

import storm.starter.TestWordSpout;
import storm.starter.TestWordSpout_poisson;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.metric.LoggingMetricsConsumer;
import java.lang.Exception;

import java.util.Map;

/**
 * This is a basic example of a Storm topology.
 */
public class ExclamationTopology {

  public static class ExclamationBolt extends BaseRichBolt {
    OutputCollector _collector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
      _collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
      //Utils.sleep(1);
      int sum = 0;
      for(int i=0; i<200000; i++) {
          sum += i;
      }
      _collector.emit(tuple, new Values(tuple.getString(0) + sum));
      _collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word"));
    }


  }

  public static void main(String[] args) throws Exception {
    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("word", new TestWordSpout(), 5);
    builder.setBolt("exclaim1", new ExclamationBolt(), 5).shuffleGrouping("word");
    builder.setBolt("exclaim2", new ExclamationBolt(), 5).shuffleGrouping("exclaim1");
    builder.setBolt("exclaim3", new ExclamationBolt(), 5).shuffleGrouping("exclaim2");
    //builder.setBolt("exclaim4", new ExclamationBolt(), 5).shuffleGrouping("exclaim3");

    Config conf = new Config();
    conf.setDebug(false);
    conf.setStatsSampleRate(1); // each message is tracked//
    //conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
    //conf.setBuiltInMetricSecs(3);
    conf.setIShuffleGroupingEnable(1); //0:default, 1:latency-based load balance, 2: the Power Of Two Choice
    conf.setIShuffleGroupingAgingRate(0.5);


    if (args != null && args.length > 0) {
      conf.setNumWorkers(5);

      StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
    }
    else {

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("test", conf, builder.createTopology());
      Utils.sleep(10000);
      cluster.killTopology("test");
      cluster.shutdown();
    }
  }
}
