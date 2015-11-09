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

import backtype.storm.Config;
import backtype.storm.topology.OutputFieldsDeclarer;
import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import java.util.HashMap;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;


public class TestWordSpout_poisson extends BaseRichSpout {
    public static Logger LOG = LoggerFactory.getLogger(TestWordSpout_poisson.class);
    boolean _isDistributed;
    
    public SpoutOutputCollector _collector; // made it public
    int msg_id = 0;
    final String[] words = new String[] {"nathan", "mike", "jackson", "golda", "bertels"};
    final Random rand = new Random();
    ArrayList<Integer> poisson = new ArrayList(100);
    int index_ = 0;

    public TestWordSpout_poisson() {
        this(true);
    }

    public TestWordSpout_poisson(boolean isDistributed) {
        _isDistributed = isDistributed;
        for(int i=0; i<37; i++) {
            poisson.add(0);
            poisson.add(1);
        }
        for(int i=0; i<19; i++) {
            poisson.add(2);
        }
        for(int i=0; i<7; i++) {
            poisson.add(3);
        }
        Collections.shuffle(poisson, rand);
    }
        
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
    }
    
    public void close() {
        
    }
        
    public void nextTuple() {
        int num = 2 * poisson.get(index_++);
        if(num == 0) {}
        else {
            Utils.sleep(1);
            for(int i=0; i<num; i++) {
                final String word = words[rand.nextInt(words.length)];
                System.out.println(word);
                _collector.emit(new Values(word), new Integer(msg_id++));
            }
        }

        if(index_ == 100) {
            index_ = 0;
            Collections.shuffle(poisson, rand);
        }
    }
    
    public void ack(Object msgId) {

    }

    public void fail(Object msgId) {
        
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        if(!_isDistributed) {
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
            return ret;
        } else {
            return null;
        }
    }    
}