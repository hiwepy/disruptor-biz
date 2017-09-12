/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lmax.disruptor.biz.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.biz.config.DisruptorConfig;
import com.lmax.disruptor.biz.event.DisruptorEvent;
import com.lmax.disruptor.biz.handler.DisruptorEventHandler;
import com.lmax.disruptor.biz.util.WaitStrategys;

/**
 * 
 * @className ： RingBufferFactoryBean
 * @description ： TODO(描述这个类的作用)
 * @author ：万大龙（743）
 * @date ： 2017年9月6日 下午7:29:30
 * @version V1.0
 *          http://blog.csdn.net/a314368439/article/details/72642653?utm_source=itdadao&utm_medium=referral
 */

public class RingBufferFactoryBean implements FactoryBean<RingBuffer<DisruptorEvent>>, InitializingBean {

	/**
	 * 决定一个消费者将如何等待生产者将Event置入Disruptor的策略。用来权衡当生产者无法将新的事件放进RingBuffer时的处理策略。
	 * （例如 ：当生产者太快，消费者太慢，会导致生成者获取不到新的事件槽来插入新事件，则会根据该策略进行处理，默认会堵塞）
	 */
	private WaitStrategy waitStrategy;
	private DisruptorConfig properties;
	private ThreadFactory threadFactory;
	private EventFactory<DisruptorEvent> eventFactory;
	private DisruptorEventHandler eventHandler;

	@Override
	public void afterPropertiesSet() throws Exception {

		if (waitStrategy == null) {
			setWaitStrategy(WaitStrategys.YIELDING_WAIT);
		}
		if (threadFactory == null) {
			setThreadFactory(new DisruptorEventThreadFactory());
		}
	}

	@Override
	public RingBuffer<DisruptorEvent> getObject() throws Exception {

		// 创建线程池
		ExecutorService executor = Executors.newFixedThreadPool(properties.getRingThreadNumbers());
		/*
		 * 第一个参数叫EventFactory，从名字上理解就是“事件工厂”，其实它的职责就是产生数据填充RingBuffer的区块。
		 * 第二个参数是RingBuffer的大小，它必须是2的指数倍 目的是为了将求模运算转为&运算提高效率
		 * 第三个参数是RingBuffer的生产都在没有可用区块的时候(可能是消费者（或者说是事件处理器） 太慢了)的等待策略
		 */
		RingBuffer<DisruptorEvent> ringBuffer = null;
		if (properties.isMultiProducer()) {
			// RingBuffer.createMultiProducer创建一个多生产者的RingBuffer
			ringBuffer = RingBuffer.createMultiProducer(eventFactory, properties.getRingBufferSize(), waitStrategy);
		} else {
			// RingBuffer.createSingleProducer创建一个单生产者的RingBuffer
			ringBuffer = RingBuffer.createSingleProducer(eventFactory, properties.getRingBufferSize(), waitStrategy);
		}
		
		//单个处理器
		if (null != eventHandler) {
			// 创建SequenceBarrier
			SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
			// 创建消息处理器
			BatchEventProcessor<DisruptorEvent> transProcessor = new BatchEventProcessor<DisruptorEvent>(
					ringBuffer, sequenceBarrier, eventHandler);
			// 这一部的目的是让RingBuffer根据消费者的状态 如果只有一个消费者的情况可以省略
			ringBuffer.addGatingSequences(transProcessor.getSequence());
			// 把消息处理器提交到线程池
			executor.submit(transProcessor);
		}
		
		return ringBuffer;
		
	}

	@Override
	public Class<?> getObjectType() {
		return RingBuffer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public DisruptorConfig getProperties() {
		return properties;
	}

	public void setProperties(DisruptorConfig properties) {
		this.properties = properties;
	}

	public WaitStrategy getWaitStrategy() {
		return waitStrategy;
	}

	public void setWaitStrategy(WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	public EventFactory<DisruptorEvent> getEventFactory() {
		return eventFactory;
	}

	public void setEventFactory(EventFactory<DisruptorEvent> eventFactory) {
		this.eventFactory = eventFactory;
	}

	public DisruptorEventHandler getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(DisruptorEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
}
