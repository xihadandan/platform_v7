/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wellsoft.context.base.key;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lilin
 * @ClassName: SequenceFactory
 * @Description:
 */
@Component
public class SequenceFactory {
    private static Map<String, SingleSequence> singleSequenceMap = new ConcurrentHashMap<String, SingleSequence>();
    private static SequenceFactory factory = new SequenceFactory();

    private ApplicationContext context;

    private SequenceFactory() {
    }

    // public void init() {
    // factory = this.context.getBean(SequenceFactory.class);
    // }

    public static SingleSequence getSequence(String name) {
        SingleSequence sequence = (SingleSequence) singleSequenceMap.get(name);
        if (sequence == null) {
            synchronized (SequenceFactory.class) {
                if (sequence == null) {
                    sequence = factory.createSequence(name);
                    singleSequenceMap.put(name, sequence);
                }
            }
        }
        return sequence;
    }

    private SingleSequence createSequence(String name) {
        SingleSequence sequence = new SingleSequence(name);
        return sequence;
    }

    // @Override
    // public void setApplicationContext(ApplicationContext applicationContext)
    // throws BeansException {
    // this.context = applicationContext;
    // }

}