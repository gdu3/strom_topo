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
package storm.starter.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.Random;

public class RandomLogSpout extends BaseRichSpout {
  SpoutOutputCollector _collector;
  Random _rand = new Random();
  int msg_id = 0;
  String[] sentences = new String[]{ "the cow jumped over the moon", "the cow jumped over the moon, The quick brown fox jumps over the lazy dog", "an apple a day keeps the doctor away,an apple a day keeps the doctor away and this is supposed to be a very long log line",
        "four score and seven years ago","random", "snow white and the seven dwarfs","snow white", "i am at two with nature" };


  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    _collector = collector;
  }

  public void nextTuple() {
    Utils.sleep(1);
    if(_rand.nextInt(10) < 4) {
      String sentence = sentences[_rand.nextInt(sentences.length)];
      _collector.emit(new Values(sentence), new Integer(msg_id++));
    }
  }

  public void ack(Object id) {
  }

  public void fail(Object id) {
  }

  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("word"));
  }

}
